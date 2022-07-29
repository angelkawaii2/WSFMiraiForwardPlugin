package dev.raincandy.mirai.utils.reply;

import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageSource;

import java.util.UUID;

/**
 * 这个类用来记录QQ Event收到的消息
 * 例如 qq端收到聊天转发，发送到MC端
 * <p>
 * 事件：
 * 如果 收到 MC(websocket)端 replyId，等于这个的requestId
 * ----设置这条消息的Quote引用
 */
public class MiraiMsgSourceReplyContext extends ReplyContext {

    private final MessageSource msgSource;
    private final User sender;

    /**
     * @param requestId 到MC端的消息的requestId，也等同之后MC端回复此消息的replyId
     * @param msgSource 消息源
     * @param sender    User
     */
    public MiraiMsgSourceReplyContext(String requestId, MessageSource msgSource, User sender) {
        super(requestId);
        this.msgSource = msgSource;
        this.sender = sender;
    }

    public MiraiMsgSourceReplyContext(UUID requestId, MessageSource msgSource, User sender) {
        this(requestId.toString(), msgSource, sender);
    }

    public MessageSource getMsgSource() {
        return msgSource;
    }

    /**
     * 调用方可以判断 instanceof Member，就可以直接获取到来源群
     *
     * @return
     */
    public User getSender() {
        return sender;
    }
}
