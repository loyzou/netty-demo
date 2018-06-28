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
	 * ���з���
	 */
	public  void doServer(){
		try {
			mBossGroup = new NioEventLoopGroup();//��������
			mWorkGroup = new NioEventLoopGroup();//������

			ServerBootstrap serverBootstrap = new ServerBootstrap(); //�����
			serverBootstrap.group(mBossGroup, mWorkGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024)
			.option(ChannelOption.RCVBUF_ALLOCATOR, new AdaptiveRecvByteBufAllocator(64, 1024, 2048))//���ջ�����
			.option(ChannelOption.SO_REUSEADDR, true)
			//������ͬһ���û�����ֹ�˿ڽٳ֣��Ķ������/�̹߳���һ���˿�
			.option(EpollChannelOption.SO_REUSEPORT, true)
			.option(ChannelOption.TCP_NODELAY, false)
			.childOption(ChannelOption.TCP_NODELAY, true)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(new ChildChannelHandler());//�ͻ����½��������socketChannel��Ӧ��ChannelPipeline��Handler;
			
			//�󶨶˿ڼ�ip
			ChannelFuture mChannelFuture = serverBootstrap.bind("192.168.10.130", 9999).sync();
			System.out.println("��������");
			Channel mChanel = mChannelFuture.channel();
			mChanel.closeFuture().sync();
			System.out.println("����ر�");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			reaseResource();
		}

	}
	
	/***
	 * �ͷ���Դ
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
			// �����˳� �ͷ��̳߳���Դ
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
			ch.pipeline().addLast(new StringEncoder());//����
			ch.pipeline().addLast(new StringDecoder());//����
			ch.pipeline().addLast("pong", new IdleStateHandler(60, 0, 0,TimeUnit.SECONDS));
			ch.pipeline().addLast(nettyServerHandler);

		}
	}
	
	public static void main(String[] args) {
		new NettyServer().run();
	}

}
