package dev.raincandy.mirai.utils.reply;

import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.message.data.SingleMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 此类用于管理回复缓存
 * 由于指令响应时间不同，需要延迟发送，以避免bot连续发送多条消息
 */
public class MiraiEchoManager {

    /**
     * 当超过此秒数未更新时则发送
     */
    private static int timeoutSec = 2;


    private static final Map<String, MiraiEchoProcesser> processerMap = new HashMap<>();


    private final static ExecutorService pool = Executors.newFixedThreadPool(1000,
            r -> new Thread(r, "MiraiEchoManager " + System.currentTimeMillis()));

    /**
     * 当消息回复时调用此方法
     *
     * @param replyId 回复信息的replyId
     * @param msg     消息
     */
    public static void add(String replyId, User sender, QuoteReply reply, SingleMessage msg) {

        //初始化
        var miraiEchoProcesser = processerMap.get(replyId);
        var isNewThread = (miraiEchoProcesser == null);
        if (isNewThread) {
            miraiEchoProcesser = new MiraiEchoProcesser(replyId, sender, reply);
        }
        miraiEchoProcesser.add(msg);

        processerMap.put(replyId, miraiEchoProcesser);
        if (isNewThread) {
            pool.submit(miraiEchoProcesser);
        }
    }

    /**
     * 结束所有线程，并发送所有剩余数据
     */
    public static void shutdownAll() {
        pool.shutdown();
        for (MiraiEchoProcesser p : processerMap.values()) {
            p.flush();
        }
    }

    private static class MiraiEchoProcesser implements Runnable {

        private final User sender;
        private long lastUpdateTime = 0;
        private final String replyId;
        private MessageChain msgChain = MessageUtils.newChain();


        public MiraiEchoProcesser(String replyId, User sender) {
            this.replyId = replyId;
            this.sender = sender;
        }

        public MiraiEchoProcesser(String replyId, User sender, QuoteReply quote) {
            this(replyId, sender);
            if (quote != null) {
                msgChain = msgChain.plus(quote);
            }
        }

        public String getReplyId() {
            return replyId;
        }

        public void add(SingleMessage msg) {
            //更新时间戳
            this.lastUpdateTime = System.currentTimeMillis();
            if (msgChain.size() > 1) {
                msgChain = msgChain.plus("\n");
            }
            msgChain = msgChain.plus(msg);
        }

        public long getLastUpdateTime() {
            return lastUpdateTime;
        }

        /**
         * 立即发送所有数据
         */
        public void flush() {
            if (sender instanceof Member) {
                ((Member) sender).getGroup().sendMessage(msgChain);
            } else {
                sender.sendMessage(msgChain);
            }
        }

        @Override
        public void run() {

            try {

                var current = System.currentTimeMillis();

                //休眠到预计时间为止
                while (getLastUpdateTime() + timeoutSec * 1000L > current) {
                    Thread.sleep((getLastUpdateTime() + timeoutSec * 1000L) - current);
                    current = System.currentTimeMillis();
                }

                this.flush();
                processerMap.remove(this.getReplyId());
                //todo 由线程池管理?
                //threadMap.remove(this.getReplyId());
            } catch (InterruptedException ignored) {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
