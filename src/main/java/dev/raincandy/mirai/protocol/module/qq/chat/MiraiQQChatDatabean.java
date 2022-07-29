package dev.raincandy.mirai.protocol.module.qq.chat;

import dev.raincandy.sdk.websocket.protocol.v1.bean.body.modules.qq.chat.QQChatDataBean;
import net.mamoe.mirai.contact.AnonymousMember;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.SingleMessage;
import net.mamoe.mirai.message.data.UnsupportedMessage;

import java.util.UUID;

public class MiraiQQChatDatabean extends QQChatDataBean {

    public MiraiQQChatDatabean(MessageEvent e) {
        var sender = e.getSender();
        setGroup(sender instanceof Member ? ((Member) sender).getGroup().getId() : 0);
        setRequestId(UUID.randomUUID().toString());
        setSenderQQ(sender instanceof AnonymousMember ? 0 : sender.getId());
        setSenderName(sender.getNick());
        setMsg(msgChain2Msg(e.getMessage()));
    }

    private static String msgChain2Msg(MessageChain mChain) {
        var sb = new StringBuilder();
        for (SingleMessage sm : mChain) {
            if (sm instanceof UnsupportedMessage) {
                continue;
            }
            sb.append(sm.contentToString());
        }
        return sb.toString();
    }
}
