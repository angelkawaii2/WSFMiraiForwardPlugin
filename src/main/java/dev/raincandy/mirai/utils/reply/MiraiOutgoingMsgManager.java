package dev.raincandy.mirai.utils.reply;

import net.mamoe.mirai.message.data.MessageSource;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 这个类用来记录mirai发送出去的消息
 * 用于处理用户quote消息进行回复，mirai找到上下文的功能
 * 默认记录500条最新数据，超出后将覆盖最早的数据
 */
public class MiraiOutgoingMsgManager {


    /**
     * 每个群独立变量？
     */
    static Map<MessageSource, String> ss = getCircularMap(500);


    /**
     * 添加一个mirai发出的消息
     *
     * @param source    消息源，内部消息基于ID进行判断
     * @param requestId
     */
    public static void add(MessageSource source, String requestId) {
        ss.put(source, requestId);
    }

    /**
     * * 这里魔改了getter，使用IDs进行匹配
     *
     * @param source
     * @return
     */
    public static String get(MessageSource source) {
        //source
        for (Map.Entry<MessageSource, String> kv : ss.entrySet()) {
            if (kv.getKey().getIds()[0] == source.getIds()[0]) {
                return kv.getValue();
            }
        }
        return null;
    }

    private static Map<MessageSource, String> getCircularMap(int size) {
        return new LinkedHashMap<>(size) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<MessageSource, String> eldest) {
                return size() > size;
            }
        };
    }
}
