package org.xxjr.netty.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyClientHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("��ȡ�������Ϣ");
		super.channelRead(ctx, msg);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("���������������");
		//  ���������������
		ctx.writeAndFlush("AAAAAAAAAAA"+"_$");
		
	}
}
