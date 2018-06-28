package org.xxjr.netty.server;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.AdaptiveRecvByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.epoll.EpollChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class NettyServer extends Thread{
	private NettyServerHandler nettyServerHandler;
	private static NioEventLoopGroup mBossGroup;
	private static NioEventLoopGroup mWorkGroup;

	public NettyServer() {
		nettyServerHandler = new NettyServerHandler();
	}

	@Override
	public void run() {
		doServer();
	}

	/***
	 * 运行服务
	 */
	public  void doServer(){
		try {
			mBossGroup = new NioEventLoopGroup();//主运行组
			mWorkGroup = new NioEventLoopGroup();//工作组

			ServerBootstrap serverBootstrap = new ServerBootstrap(); //服务端
			serverBootstrap.group(mBossGroup, mWorkGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 2048))//接收缓冲器
			.option(ChannelOption.SO_REUSEADDR, true)
			//隶属于同一个用户（防止端口劫持）的多个进程/线程共享一个端口
			.option(EpollChannelOption.SO_REUSEPORT, true)
			.option(ChannelOption.TCP_NODELAY, false)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChildChannelHandler());//客户端新接入的连接socketChannel对应的ChannelPipeline的Handler;
			
			//绑定端口及ip
			ChannelFuture mChannelFuture = serverBootstrap.bind("192.168.10.130", 9999).sync();
			System.out.println("服务启动");
			Channel mChanel = mChannelFuture.channel();
			mChanel.closeFuture().sync();
			System.out.println("服务关闭");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reaseResource();
		}

	}
	
	/***
	 * 释放资源
	 */
	public static void reaseResource() {
		try {		
			if (mWorkGroup != null) {
				mWorkGroup.shutdownGracefully();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {	
			// 优雅退出 释放线程池资源
			if (mBossGroup != null) {
				mBossGroup.shutdownGracefully();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Sharable
	class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,Unpooled.copiedBuffer("_$".getBytes())));
			ch.pipeline().addLast(new StringEncoder());//加码
			ch.pipeline().addLast(new StringDecoder());//解码
			ch.pipeline().addLast("pong", new IdleStateHandler(60, 0, 0,TimeUnit.SECONDS));
			ch.pipeline().addLast(nettyServerHandler);

		}
	}
	
	public static void main(String[] args) {
		new NettyServer().run();
	}

}
