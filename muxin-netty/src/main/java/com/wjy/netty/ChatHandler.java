package com.wjy.netty;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Joiner;
import com.wjy.constant.MsgActionEnum;
import com.wjy.service.UserService;
import com.wjy.util.SpringContextHolder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;

@Log4j2
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    // 用于记录和管理所有客户端的channle
    public static ChannelGroup users = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        // 获取客户端传输过来的消息
        String content = msg.text();

        Channel currentChannel = ctx.channel();
        DataContent dataContent = JSON.parseObject(content, DataContent.class);
        Integer action = dataContent.getAction();
        if(action==MsgActionEnum.CONNECT.type){
            String senderId = dataContent.getChatMsg().getSenderId();
            log.error("与前端建立连接，发送者：{}",senderId);
            UserChannelRel.put(senderId,currentChannel);
            // 测试
            for (Channel c : users) {
                System.out.println(c.id().asLongText());
            }
            UserChannelRel.output();
        }else if(action == MsgActionEnum.CHAT.type){
            ChatMsg chatMsg = dataContent.getChatMsg();
            String senderId = chatMsg.getSenderId();
            String receiverId = chatMsg.getReceiverId();
            String msgText = chatMsg.getMsg();
            log.error("{}发来消息：{}，接收者：{}",senderId,msgText,receiverId);
            UserService userService = SpringContextHolder.getBean(UserService.class);
            String msgId = userService.saveMsg(chatMsg);
            chatMsg.setMsgId(msgId);
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);

            Channel receiverChannel = UserChannelRel.get(receiverId);
            if (receiverChannel == null) {
                // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
                log.error("receiverChannel为空，接收者id：{}",receiverId);
            } else {
                Channel findChannel = users.find(receiverChannel.id());
                if(findChannel==null){
                    // TODO channel为空代表用户离线，推送消息（JPush，个推，小米推送）
                    log.error("findChannel为空，接收者id：{}",receiverId);
                }else {
                    // 用户在线
                    log.info("receiverChannel--{} : findChannel--{}",receiverChannel,findChannel);
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(JSON.toJSONString(dataContentMsg)));
                }
            }
        }else if(action==MsgActionEnum.SIGNED.type){
            log.error("签收消息");
            UserService userService = SpringContextHolder.getBean(UserService.class);
            String msgIds = dataContent.getExtand();
            List<String> msgIdList= Arrays.asList(msgIds.split(","));
            System.out.println(msgIdList.toString());
            if(!CollectionUtils.isEmpty(msgIdList)){
                userService.batchUpdateMsgSigned(msgIdList);
            }
        }
    }

    /**
     * 当客户端连接服务端之后（打开连接）
     * 获取客户端的channle，并且放到ChannelGroup中去进行管理
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        users.add(ctx.channel());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        String channelId = ctx.channel().id().asShortText();
        System.out.println("客户端被移除，channelId为：" + channelId);

        // 当触发handlerRemoved，ChannelGroup会自动移除对应客户端的channel
        users.remove(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(),cause);
        ctx.channel().close();
        users.remove(ctx.channel());
    }
}
