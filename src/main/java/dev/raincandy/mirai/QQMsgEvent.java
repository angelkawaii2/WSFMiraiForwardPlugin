package dev.raincandy.mirai;

import dev.raincandy.mirai.config.v2.WSFGlobalConfig;
import dev.raincandy.mirai.config.v2.dto.AuthenticationDTO;
import dev.raincandy.mirai.config.v2.dto.target.ChatTargetsDTO;
import dev.raincandy.mirai.config.v2.dto.target.CmdTargetsDTO;
import dev.raincandy.mirai.protocol.module.qq.chat.MiraiQQChatDatabean;
import dev.raincandy.mirai.utils.MiraiMessageUtil;
import dev.raincandy.mirai.utils.reply.MiraiMsgSourceReplyContext;
import dev.raincandy.mirai.utils.reply.MiraiOutgoingMsgManager;
import dev.raincandy.mirai.utils.reply.ReplyContextManager;
import dev.raincandy.sdk.websocket.protocol.v1.bean.body.modules.minecraft.command.MinecraftCommandBodyBean;
import dev.raincandy.sdk.websocket.protocol.v1.bean.body.modules.qq.chat.QQChatBodyBean;
import dev.raincandy.sdk.websocket.protocol.v1.bean.send.ForwardBean;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.User;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;
import org.apache.commons.lang3.StringUtils;

import java.util.UUID;
import java.util.function.Consumer;

public class QQMsgEvent implements Consumer<MessageEvent> {

    private JavaPlugin miraiPlugin;

    public QQMsgEvent(JavaPlugin miraiPlugin) {
        this.miraiPlugin = miraiPlugin;
    }


    /**
     * 处理聊天消息事件
     *
     * @param e
     */
    @Override
    public void accept(MessageEvent e) {


        var gConfig = WSFGlobalConfig.getInstance();
        var serverConfig = gConfig.getServerConfig();

        var sender = e.getSender();

        //先解析指令

        // todo 这里只是第一条plainText，但转发时会重新contentToString构造原始的消息，看情况
        var originPlainMsg = MiraiMessageUtil.getFirstPlainMessage(e.getMessage());
        if (originPlainMsg == null) {
            return;
        }

        String msgStr = originPlainMsg.getContent();

        //检查所有配置的聊天转发条目
        var senderQQ = sender.getId();
        var isAdminSender = serverConfig.isAdminQQ(senderQQ);


        //如果检查前缀，走chatHandler，就没办法做回复的检查
        //但如果做了回复的检查，就没法在chatHandler里设置replyId？
        //或者把replyId当参数传进去？【可行】

        //这里检查是否此消息是回复之前mirai发送的消息，如果是的话就置replyId为之前那条消息的requestId
        //如果是null就不处理


        var quoteReply = MiraiMessageUtil.getQuoteReply(e.getMessage());

        String replyId = null;
        if (quoteReply != null) {
            replyId = MiraiOutgoingMsgManager.get(quoteReply.getSource());
        }

        //如果有replyId，那么要省略部分鉴权
        //检查聊天转发
        var chatConfig = gConfig.getChatConfig();
        if (chatConfig.getEnableChatForward()) {

            //检查聊天转发的所有已配置映射
            for (var target : chatConfig.getTargets()) {
                if (!target.getEnable()) {
                    continue;
                }
                //鉴权
                var authConf = target.getAuthentication();

                var authed = false;

                //群聊
                if (sender instanceof Member) {
                    var group = ((Member) sender).getGroup();

                    for (AuthenticationDTO.GroupsDTO g : authConf.getGroups()) {
                        if (!g.hasGroupId(group.getId())) {
                            continue;
                        }
                        //有replyId就不需要检查前缀
                        if (replyId == null && !msgStr.startsWith(g.getPrefix())) {
                            continue;
                        }
                        if (!isAdminSender && !g.hasWhitelist(senderQQ)) {
                            continue;
                        }
                        authed = true;
                        break;
                    }

                } else {
                    //私聊
                    var privateChat = authConf.getPrivateChat();
                    //有replyId就不需要检查前缀
                    if (replyId == null && !msgStr.startsWith(privateChat.getPrefix())) {
                        continue;
                    }
                    if (!isAdminSender && !privateChat.hasWhitelist(senderQQ)) {
                        continue;
                    }
                    authed = true;
                }

                if (authed) {
                    getLogger().info("鉴权通过,从QQ转发聊天到...");
                    chatForwardHandler(e, target, replyId);
                }
            }//所有转发配置for
        }


        var cmdConfig = gConfig.getCmdConfig();
        if (cmdConfig.getEnableCommandForward()) {

            //这里的getTargets会将所有default配置文件放在最后
            //如果执行过非默认指令，就不响应默认指令
            //如果未执行过非默认指令，将会执行所有的默认指令
            boolean isForward = false;
            for (var target : cmdConfig.getTargets()) {
                if (!target.getEnable()) {
                    continue;
                }
                //鉴权
                var authConf = target.getAuthentication();
                var authed = false;

                //每次循环重置
                var cmdStr = msgStr;

                //群聊
                if (sender instanceof Member) {
                    var group = ((Member) sender).getGroup();

                    for (AuthenticationDTO.GroupsDTO g : authConf.getGroups()) {
                        if (!g.hasGroupId(group.getId())) {
                            continue;
                        }
                        if (!msgStr.startsWith(g.getPrefix())) {
                            continue;
                        }
                        //如果不是全局管理，且不在允许列表中，就跳过
                        if (!isAdminSender && !g.hasWhitelist(senderQQ)) {
                            continue;
                        }
                        //移除前缀
                        cmdStr = msgStr.substring(g.getPrefix().length());
                        authed = true;
                        break;
                    }

                } else {
                    //私聊
                    var privateChat = authConf.getPrivateChat();
                    if (!msgStr.startsWith(privateChat.getPrefix())) {
                        continue;
                    }
                    //如果不是全局管理，且不在允许列表中，就跳过
                    if (!isAdminSender && !privateChat.hasWhitelist(senderQQ)) {
                        continue;
                    }
                    authed = true;
                    cmdStr = msgStr.substring(privateChat.getPrefix().length());
                }


                if (authed) {
                    var tmpCmdStr = cmdStr.replaceAll(" +", " ").split(" ");

                    //编码要用UTF8，不然会乱码
                    //指令不在预设列表中，跳过
                    if (!target.isDefaultCommand() && !target.getCommands().contains(tmpCmdStr[0])) {
                        continue;
                    }

                    //如果这个已经处理过了，并且当前是默认指令，就不再处理
                    if (isForward && target.isDefaultCommand()) {
                        continue;
                    }

                    //如果当前指令不是默认指令，标识这个指令已经执行过了
                    //这样确保所有默认指令都能被执行到
                    if (!target.isDefaultCommand()) {
                        isForward = true;
                    }

                    //处理指令重写
                    if (target.hasRewrite()) {
                        tmpCmdStr[0] = target.getRewrite();
                    }

                    //鉴权完毕。在这里执行转发？
                    getLogger().info("鉴权通过,转发CMD...");
                    cmdForwardHandler(e, target, tmpCmdStr);
                }
            }
        }
    }

    /**
     * 处理来自QQ to MC 的消息转发，应在调用前自行鉴权
     *
     * @param e         聊天事件
     * @param targetDTO 转发配置
     * @param replyId   为空则不设置此字段
     */
    private void chatForwardHandler(MessageEvent e, ChatTargetsDTO targetDTO, String replyId) {

        //构造转发对象

        var qqcdb = new MiraiQQChatDatabean(e);

        if (replyId != null) {
            qqcdb.setReplyId(replyId);
        }

        //todo 处理前缀部分？
        //qqcdb.setMsg(qqcdb.getMsg().substring());

        var msgSource = MiraiMessageUtil.getMessageSource(e.getMessage());

        //e.getMessage();

        //添加到QQ2MC的记录里，requestId就是这条到MC的消息的ID
        //这里的sender由调用方判断instanceof Member，就可以拿到group了
        ReplyContextManager.addContext(
                new MiraiMsgSourceReplyContext(qqcdb.getRequestId(), msgSource, e.getSender()));

        //获取连接对象，构造数据包
        var fb = new ForwardBean(new QQChatBodyBean(qqcdb));
        fb.addTargetClientId(targetDTO.getTargetClientsId());

        var connId = targetDTO.getConnectionId();
        var conn = MiraiWsForwardClient.getMiraiConnectionManager().getConnection(connId);

        //conn.send(fb);
        conn.getWebsocketContext().send(fb);

    }

    /**
     * @param e      事件
     * @param target 目标配置
     *               //@param requestId 为空则自动生成，注意：这里必须保证对于同一指令多次发送，应当
     * @param cmds   命令
     */
    private void cmdForwardHandler(MessageEvent e, CmdTargetsDTO target, String[] cmds) {
        User sender = e.getSender();
        boolean isGroupMsg = sender instanceof Member;


        //接收方自行处理

        var cmd = StringUtils.join(cmds, " ");

        var source = "";
        if (isGroupMsg) {
            source = String.format("QQ群: %s", ((Member) sender).getGroup().getName());
        } else {
            source = String.format("QQ: %d", sender.getId());
        }

        //这里不能直接打断点
        var requestId = UUID.randomUUID().toString();

        var mcbb = new MinecraftCommandBodyBean(requestId, source, sender.getNick(), cmd);

        //添加到QQ2MC的记录里，requestId就是这条到MC的指令的ID，复用chat的reply
        var msgSource = MiraiMessageUtil.getMessageSource(e.getMessage());
        ReplyContextManager.addContext(
                new MiraiMsgSourceReplyContext(requestId, msgSource, e.getSender()));


        var fb = new ForwardBean(mcbb);
        //设置目标客户端ID
        fb.addTargetClientId(target.getTargetClientsId());


        //获取连接对象
        var connId = target.getConnectionId();
        //发送数据包
        var conn = MiraiWsForwardClient.getMiraiConnectionManager().getConnection(connId);
        //todo 启动时要检查其他几个配置文件的connId有没有在config里，如果没有就要警告
        //conn.send(fb);
        conn.getWebsocketContext().send(fb);

    }


    private MiraiLogger getLogger() {
        return miraiPlugin.getLogger();
    }


}
