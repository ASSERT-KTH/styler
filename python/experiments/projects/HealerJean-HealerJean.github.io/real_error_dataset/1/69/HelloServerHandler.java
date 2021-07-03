package com.lc.server;

import java.net.InetAddress;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * netty客户端
 *
 * @author HealerJean
 * @date 2017年6月26日 下午5:37:23
 *  通道入站处理器
 */

public class HelloServerHandler extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		// 收到消息直接打印输出
		System.out.println(ctx.channel().remoteAddress() + " Say : " + msg);

		// 返回客户端信息 - 我已经接收到了你的消息
		// writeAndFlush写消息并发送 给客户端
		ctx.writeAndFlush("received your message !\n");
	}


	// 当Channel变成活跃状态时被调用 (客户端刚刚启动的时候调用，也就是客户端激活的时候)
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		//打印内容 RamoteAddress : /127.0.0.1:49335 active !
		System.out.println("RamoteAddress : " + ctx.channel().remoteAddress() + " active !");

		//这个是向客户端打印
		ctx.writeAndFlush("welcome to " + InetAddress.getLocalHost().getHostName() + " server.\n");

		super.channelActive(ctx);
	}
}
