package com.lc.client2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class HelloClientHandler extends SimpleChannelInboundHandler<String> {

	//客户端读取控制台内容的时候打印
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		//msg为从服务端发来的消息
		System.out.println("Server say : " + msg);
	}
	//客户端开启的时候打印
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client active ");
		super.channelActive(ctx);
	}
	//客户端关闭的时候打印
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client close ");
		super.channelInactive(ctx);
	}
}
