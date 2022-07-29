package dev.raincandy.mirai.utils.reply;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 这个类用来管理 从 QQ 发送到 MC端  的消息回调?
 * 可以抽象成通用的类？
 */
public class ReplyContextManager {


    static Map<String, ReplyContext> ss = getCircularMap(500);


    /**
     * 添加一个context，用于之后可能的回调
     * 注意，此方法会自动移除较早的请求
     *
     * @param reply reply对象
     */
    public static void addContext(ReplyContext reply) {
        ss.put(reply.getRequestId(), reply);
    }

    public static ReplyContext getReplyContext(String requestId) {
        return ss.get(requestId);
    }

    public static void setQueueLength(int size) {
        Map<String, ReplyContext> tmp = getCircularMap(size);
        if (!ss.isEmpty()) {
            tmp.putAll(ss);
        }
        ss = tmp;
    }

    private static Map<String, ReplyContext> getCircularMap(int size) {
        return new LinkedHashMap<>(size) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<String, ReplyContext> eldest) {
                return size() > size;
            }
        };
    }
}

