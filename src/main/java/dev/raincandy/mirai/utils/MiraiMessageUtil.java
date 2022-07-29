package dev.raincandy.mirai.utils;

import net.mamoe.mirai.message.data.*;

public class MiraiMessageUtil {


    public static PlainText getFirstPlainMessage(MessageChain msgChain) {
        for (SingleMessage msg : msgChain) {
            if (msg instanceof PlainText) {
                return ((PlainText) msg);
            }
        }
        return null;
    }

    /**
     * 获取此消息链中的MessageSources引用
     *
     * @param msgChain
     * @return 根据mirai文档，服务器来的消息链中一定含有source，但如果找不到会返回null
     */
    public static MessageSource getMessageSource(MessageChain msgChain) {
        for (SingleMessage sm : msgChain) {
            if (sm instanceof MessageSource) {
                return (MessageSource) sm;
            }
        }
        return null;
    }

    public static QuoteReply getQuoteReply(MessageChain msgChain) {
        for (SingleMessage sm : msgChain) {
            if (sm instanceof QuoteReply) {
                return (QuoteReply) sm;
            }
        }
        return null;
    }
}
