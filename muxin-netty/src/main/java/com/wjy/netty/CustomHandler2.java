package com.wjy.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * @Description: 创建自定义助手类
 */
@Slf4j
// SimpleChannelInboundHandler: 对于请求来讲，其实相当于[入站，入境]
public class CustomHandler2 extends SimpleChannelInboundHandler<TextWebSocketFrame> {

	private ChannelGroup channels=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
		String text = msg.text();
		log.info("服务端接收到消息：{}",text);

		channels.writeAndFlush(new TextWebSocketFrame("[服务器在]" + LocalDateTime.now()
				+ "接受到消息, 消息为：" + text));
	}

	/**
	 * 当客户端连接服务端之后（打开连接）
	 * 获取客户端的channle，并且放到ChannelGroup中去进行管理
	 */
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		channels.add(ctx.channel());
		log.info("有客户端连接，channle对应的长id为：" + ctx.channel().id().asLongText());
		log.info("当前channels长度："+channels.size());
	}

	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		// 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
//		clients.remove(ctx.channel());
		log.info("客户端断开，channle对应的长id为："
				+ ctx.channel().id().asLongText());
		log.info("客户端断开，channle对应的短id为："
				+ ctx.channel().id().asShortText());
	}
}
