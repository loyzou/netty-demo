package org.xxjr.netty.client;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class NettyClientHandler extends ChannelHandlerAdapter{

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		System.out.println("读取服务端信息");
		super.channelRead(ctx, msg);
	}
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("与服务器建立连接");
		//  向服务器发送数据
		ctx.writeAndFlush("AAAAAAAAAAA"+"_$");
		
	}
}
