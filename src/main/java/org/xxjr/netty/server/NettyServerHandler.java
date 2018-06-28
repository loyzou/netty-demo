package org.xxjr.netty.server;

import java.util.logging.Logger;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
@Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<Object> {

	@Override
	protected void messageReceived(ChannelHandlerContext ctx, Object msg)
			throws Exception {
	}
	
	/***
	 * ���ͻ�������
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		Logger.getLogger("AAAA");
		System.out.println("fuwuconnect");
		super.channelActive(ctx);
	}
	
	/***
	 * ��ȡ�ͻ�������
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("��ȡ�ͻ�������");
		super.channelRead(ctx, msg);
	}

}
