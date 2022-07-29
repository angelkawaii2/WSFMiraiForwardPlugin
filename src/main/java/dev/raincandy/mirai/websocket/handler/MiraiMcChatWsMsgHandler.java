package dev.raincandy.mirai.websocket.handler;

import com.google.gson.Gson;
import dev.raincandy.mirai.config.v2.IncomingMinecraftChatConfig;
import dev.raincandy.mirai.config.v2.MinecraftServernameRewriteConfig;
import dev.raincandy.mirai.config.v2.WSFGlobalConfig;
import dev.raincandy.mirai.utils.reply.MiraiMsgSourceReplyContext;
import dev.raincandy.mirai.utils.reply.MiraiOutgoingMsgManager;
import dev.raincandy.mirai.utils.reply.ReplyContextManager;
import dev.raincandy.sdk.websocket.util.IWsListenerMsgHandler;
import dev.raincandy.sdk.websocket.protocol.v1.bean.body.modules.minecraft.chat.MinecraftChatDataBean;
import dev.raincandy.sdk.websocket.protocol.v1.bean.received.ReceivedMainBean;
import dev.raincandy.sdk.websocket.websocket.SDKAbstractWebsocketListener;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.message.MessageReceipt;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.message.data.PlainText;
import net.mamoe.mirai.message.data.QuoteReply;

import java.util.Optional;

public class MiraiMcChatWsMsgHandler implements IWsListenerMsgHandler {

    @Override
    public void onMessage(SDKAbstractWebsocketListener listener, ReceivedMainBean mainBean, String originText) {

        var gConfig = WSFGlobalConfig.getInstance();

        //todo 是gsonUtil里的有问题，可能是自定义反序列化的地方有问题
        //var mb = GsonUtil.getGson().fromJson(text, ReceivedMainBean.class);
        var mb = new Gson().fromJson(originText, ReceivedMainBean.class);
        var body = mb.getBody();

        var data = body.getData(MinecraftChatDataBean.class);

        //构造消息
        var sourceClientId = mb.getSourceClientId();

        //处理mc消息来源重写，便于阅读
        var rewriteConf = WSFGlobalConfig.getInstance().getMcServernameRewriteConfig();
        var worldName = data.getSource().getWorldName();

        //服务器的显示名
        var displayServerName
                = Optional.ofNullable(rewriteConf.getServer(sourceClientId))
                .map(MinecraftServernameRewriteConfig.ServerWorld::getDisplayName)
                .orElse(sourceClientId);


        var displayWorldName = Optional.ofNullable(rewriteConf.getServer(sourceClientId))
                .map(MinecraftServernameRewriteConfig.ServerWorld::getWorlds)
                .map(worldsDTO -> worldsDTO.getWorldName(worldName))
                .orElse(worldName);


        var miraiMsg = new PlainText(String.format("[%s]\n[%s]<%s> %s"
                , displayServerName
                , displayWorldName
                , data.getSender().getDisplayName()
                , data.getMsg()));


        MessageChain msgChain = MessageUtils.newChain();


        //如果有reply的话，是要发给sender，没有的话才走配置文件的消息
        //有reply ID，寻找对应的[QQ]发送到[MC端]的mirai消息
        if (data.hasReplyId()) {
            //SDKWebsocketReplyContextManager.doCallback();

            //从 QQ2MC 的记录里获取
            var replyContext = ReplyContextManager.getReplyContext(data.getReplyId());

            if (replyContext != null) {
                var miraiReply = (MiraiMsgSourceReplyContext) replyContext;
                //原始消息的引用
                var msgSource = miraiReply.getMsgSource();

                //构造引用？
                var quoteReply = new QuoteReply(msgSource);
                msgChain = msgChain.plus(quoteReply).plus(miraiMsg);

                //发送给来源
                var sender = ((MiraiMsgSourceReplyContext) replyContext).getSender();

                //todo 这里发出的Quote好像没法直接回复？
                //如果用户回复此消息，是没办法拿到相同的ID的
                MessageReceipt receipt;
                if (sender instanceof Member) {
                    receipt = ((Member) sender).getGroup().sendMessage(msgChain);
                } else {
                    receipt = sender.sendMessage(msgChain);
                }

                //这里发出的也要加到列表里，给用户直接回复
                MiraiOutgoingMsgManager.add(receipt.getSource(), data.getRequestId());

            }
        } else {

            msgChain = msgChain.plus(miraiMsg);
            //读取配置文件，找到目标群

            var incomingChatConf = gConfig.getIncomingMinecraftChatConfig();
            for (IncomingMinecraftChatConfig.MappingDTO mappingDTO : incomingChatConf.getMapping()) {

                if (!mappingDTO.getIncomingClients().contains(sourceClientId)) {
                    continue;
                }

                var botInstance = Bot.getInstance(mappingDTO.getBotQQ());

                if (!botInstance.isOnline()) {
                    continue;
                }

                for (Long gId : mappingDTO.getTargetQQGroups()) {
                    var group = botInstance.getGroup(gId);
                    if (group == null) {
                        continue;
                    }
                    var msgReceipt = group.sendMessage(msgChain);
                    //这里是 MC 2 QQ 的记录
                    var source = msgReceipt.getSource();

                    MiraiOutgoingMsgManager.add(source, data.getRequestId());
                }
            }
        }
    }
}
