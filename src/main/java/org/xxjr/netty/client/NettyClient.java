package org.xxjr.netty.client;

import java.util.concurrent.TimeUnit;


import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyClient {
	private NettyClientHandler nettyClientHandler;

	public NettyClient(){
		nettyClientHandler = new NettyClientHandler();
	}

	/***
	 * 启动客户端
	 */
	private void start() {
		try {
			NioEventLoopGroup workLoopGroup = new NioEventLoopGroup();  
			Bootstrap clientBootStrap = new Bootstrap();
			clientBootStrap.group(workLoopGroup);
			clientBootStrap.channel(NioSocketChannel.class);   
			clientBootStrap.option(ChannelOption.SO_KEEPALIVE, true)
			.option(ChannelOption.TCP_NODELAY, true)
			.option(ChannelOption.SO_REUSEADDR, true)
			.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);
			clientBootStrap.handler(new ChildChannelHandler());
			
			ChannelFuture mChannelFuture = clientBootStrap.connect("192.168.10.130",9999).sync();
			System.out.println("客户端进行连接");
			mChannelFuture.channel();
			if(!mChannelFuture.isSuccess()){
				System.out.println("----------客户端进行重新连接--------------");
				   //连接不上则尝试重连
				   mChannelFuture.channel().eventLoop().schedule(new Runnable() {
	                   public void run() {
	                    start();
	                   }
	               }, 10, TimeUnit.SECONDS);
			   }
		
			mChannelFuture.channel().closeFuture().sync();
			System.out.println("客户端进行连接完成");
	
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

	}

	class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			nettyClientHandler = new NettyClientHandler();
			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,Unpooled.copiedBuffer("_$".getBytes())));
			//设置编码及解码
			ch.pipeline().addLast("decoder", new StringDecoder());
			ch.pipeline().addLast("encoder", new StringEncoder());

			//绑定心跳,对应事件userEventTriggered
			ch.pipeline().addLast("ping",new IdleStateHandler(0, 20, 0,TimeUnit.SECONDS));
			//绑定处理类
			ch.pipeline().addLast(nettyClientHandler);
		}

	}



	public static void main(String[] args) {
		new NettyClient().start();
	}

}
