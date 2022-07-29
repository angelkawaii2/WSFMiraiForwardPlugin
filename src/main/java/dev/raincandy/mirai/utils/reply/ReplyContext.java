package dev.raincandy.mirai.utils.reply;

import java.util.UUID;

/**
 * 这个类用来处理 从 QQ 发送到 MC端 的消息
 * 可以抽象成通用的类？
 */
public abstract class ReplyContext {

    /**
     * 要监听的ID，如果收到消息的replyId为这个则触发回调
     */
    private String requestId;

    public ReplyContext(String requestId) {
        this.requestId = requestId;
    }

    public ReplyContext(UUID uuid) {
        this(uuid.toString());
    }

    public String getRequestId() {
        return requestId;
    }
}