package dev.raincandy.mirai.websocket.handler;

import com.google.gson.Gson;
import dev.raincandy.mirai.utils.reply.MiraiEchoManager;
import dev.raincandy.mirai.utils.reply.MiraiMsgSourceReplyContext;
import dev.raincandy.mirai.utils.reply.ReplyContextManager;
import dev.raincandy.sdk.websocket.util.IWsListenerMsgHandler;
import dev.raincandy.sdk.websocket.protocol.v1.bean.body.modules.minecraft.command_echo.MinecraftCommandEchoDataBean;
import dev.raincandy.sdk.websocket.protocol.v1.bean.received.ReceivedMainBean;
import dev.raincandy.sdk.websocket.websocket.SDKAbstractWebsocketListener;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;

/**
 * mirai 处理mc指令回显 的 websocket onMsg handler
 */
public class MiraiMcCmdEchoWsMsgHandler implements IWsListenerMsgHandler {

    @Override
    public void onMessage(SDKAbstractWebsocketListener listener, ReceivedMainBean mainBean, String originText) {

        var mb = new Gson().fromJson(originText, ReceivedMainBean.class);

        //指令不需要配置文件，因为必定有replyId，而发送方是有记录的
        var data = mb.getBody().getData(MinecraftCommandEchoDataBean.class);
        var replyId = data.getReplyId();

        //获取发送者上下文
        var replyContext = (MiraiMsgSourceReplyContext)
                ReplyContextManager.getReplyContext(replyId);

        var quote = new QuoteReply(replyContext.getMsgSource());
        //构造消息
        var msg = new PlainText(
                String.format("[%s]\n%s",
                        mb.getSourceClientId(), data.getEcho()));

        //这里给发送加个缓冲区
        MiraiEchoManager.add(replyId, replyContext.getSender(), quote, msg);
    }
}
