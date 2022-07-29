package dev.raincandy.mirai;

import dev.raincandy.mirai.config.v2.*;
import dev.raincandy.mirai.utils.reply.MiraiEchoManager;
import dev.raincandy.mirai.websocket.MiraiWebsocketListener;
import dev.raincandy.mirai.websocket.handler.MiraiMcChatWsMsgHandler;
import dev.raincandy.mirai.websocket.handler.MiraiMcCmdEchoWsMsgHandler;
import dev.raincandy.sdk.websocket.protocol.ConnectionInfo;
import dev.raincandy.sdk.websocket.util.MessageHandler;
import dev.raincandy.sdk.websocket.util.SDKWebsocketConnManager;
import dev.raincandy.sdk.websocket.util.WSConnInfoManager;
import dev.raincandy.sdk.websocket.util.WSClientUitl;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescriptionBuilder;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.events.MessageEvent;
import net.mamoe.mirai.utils.MiraiLogger;

import java.io.*;
import java.util.Objects;

/**
 * @author angelkawaii2
 */
public final class MiraiWsForwardClient extends JavaPlugin {

    public static final MiraiWsForwardClient INSTANCE = new MiraiWsForwardClient();
    private static final SDKWebsocketConnManager<MiraiWebsocketListener> connManager = new SDKWebsocketConnManager<>();

    public MiraiLogger logger = getLogger();

    private MiraiWsForwardClient() {
        super(new JvmPluginDescriptionBuilder(
                "com.mcshiyi.mirai.MiraiWsForwardClient",
                "1.4.0")
                .name("TimoryMiraiWsForwardClient")
                .author("angelkawaii2")
                .build());
    }

    public static SDKWebsocketConnManager<MiraiWebsocketListener> getMiraiConnectionManager() {
        return connManager;
    }

    @Override
    public void onEnable() {
        //getLogger().info("Plugin loaded!");

        //读取配置文件
        var gConf = WSFGlobalConfig.getInstance();

        //todo 整理这部分代码
        try {
            gConf.setChatConfig(
                    ConfigUtil.getConfigBean(initDefaultConfig("chatForward.json")
                            , ChatConfig.class));

            gConf.setCmdConfig(
                    ConfigUtil.getConfigBean(initDefaultConfig("commandForward.json")
                            , CommandConfig.class));
            gConf.setServerConfig(
                    ConfigUtil.getConfigBean(initDefaultConfig("config.json")
                            , ServerConfig.class));

            gConf.setIncomingMinecraftChatConfig(
                    ConfigUtil.getConfigBean(initDefaultConfig("incomingMinecraftChatConfig.json")
                            , IncomingMinecraftChatConfig.class));


            gConf.setMcServernameRewriteConfig(
                    ConfigUtil.getConfigBean(initDefaultConfig("minecraft-chat-servername-rewrite.json")
                            , MinecraftServernameRewriteConfig.class));


        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error("初始化配置文件出现异常，停止插件加载！");
            return;
        }

        printServerConfig(gConf.getServerConfig());
        printChatConfig(gConf.getChatConfig());
        printCommandConfig(gConf.getCmdConfig());


        if (!gConf.getServerConfig().getEnable()) {
            getLogger().warning("插件未启用！请在配置文件中启用插件！");
            return;
        }

        var serverConfig = gConf.getServerConfig();

        //websocket消息handler
        var mcChatHandler = new MiraiMcChatWsMsgHandler();
        var cmdEchoHandler = new MiraiMcCmdEchoWsMsgHandler();

        //消息解析处理器
        MessageHandler msgHandler = new MessageHandler();
        msgHandler.registerType("minecraft-command-echo", cmdEchoHandler);
        msgHandler.registerType("minecraft-chat", mcChatHandler);

        for (ServerConfig.ServersDTO server : serverConfig.getServers()) {
            //构造连接信息

            var connectionId = server.getConnectionId();

            var connInfo = new ConnectionInfo(connectionId, server.getUrl()
                    , server.getToken(), server.getClientId());

            WSConnInfoManager.addConnInfo(connInfo);

            //开启连接
            var wsListener = new MiraiWebsocketListener(this, connInfo);

            wsListener.setHandler(msgHandler);

            WSClientUitl.createConnection(connInfo, wsListener);

            //添加链接
            connManager.addConnection(connectionId, wsListener);
        }


        GlobalEventChannel.INSTANCE.subscribeAlways(MessageEvent.class
                , new QQMsgEvent(this));

    }

    @Override
    public void onDisable() {
        //结束ws连接
        logger.info("结束Websocket所有连接中...");
        connManager.shutdownAllConnection();
        //清空消息队列，并全部发送
        logger.info("结束EchoManager所有等待回复的线程中...");
        MiraiEchoManager.shutdownAll();
    }


    private File initDefaultConfig(String configName) throws IOException {
        var conf = new File(getConfigFolder(), configName);
        if (conf.exists()) {
            return conf;
        }
        //创建默认配置文件
        try (var is = Objects.requireNonNull(getResourceAsStream(configName)
                , "配置文件初始化失败! " + configName);
             var fos = new FileOutputStream(conf)) {
            fos.write(is.readAllBytes());
            fos.flush();
        }
        return conf;
    }


    private void printServerConfig(ServerConfig conf) {
        logger.info("----插件配置:ServerConfig-----");
        logger.info("启用状态: " + (conf.getEnable() ? "已启用" : "未启用"));
        logger.info("全局管理员:" + conf.getGlobalAdminQQ());
        logger.info("");
        for (ServerConfig.ServersDTO server : conf.getServers()) {
            logger.info("  ------");
            logger.info("  配置ConnectionID:" + server.getConnectionId());
            logger.info("  注释:" + server.getComments());
            logger.info("  启用状态: " + server.isEnable());
            var url = server.getUrl();
            logger.info("  服务器地址:" + url);
            if (url.startsWith("http://") || url.startsWith("ws://")) {
                logger.warning("  提示: 此地址为非加密连接，建议使用 https/wss替代！");
            }
            //server.getToken();
        }
    }

    private void printCommandConfig(CommandConfig conf) {
        logger.info("----指令转发配置:CommandConfig-----");
        logger.info("启用命令转发:" + conf.getEnableCommandForward());
        for (var target : conf.getTargets()) {
            logger.info("  -------");
            logger.info("  目标连接ID: " + target.getConnectionId());
            logger.info("  注释: " + target.getComments());
            logger.info("  启用: " + target.getEnable());
            logger.info("  指令重写为:" + target.getRewrite());
            logger.info("  监听的指令:" +
                    (target.isDefaultCommand() ? "【所有指令(default)】" : target.getCommands()));
            logger.info("  目标客户端ID:" + target.getTargetClientsId());
            var auth = target.getAuthentication();
            var privateChat = auth.getPrivateChat();
            logger.info("  ---私聊指令鉴权---");
            logger.info("  私聊指令前缀: " + privateChat.getPrefix());
            logger.info("  允许私聊指令的QQ(全局管理以外): "
                    + (privateChat.allowAllUser() ? "【所有用户】" : privateChat.getWhitelistQQ()));

            if (privateChat.allowAllUser()) {
                logger.warning("  【警告】此指令被配置为允许所有用户执行，请注意安全！");
            }
            logger.info("  ---群聊鉴权---");
            for (var group : auth.getGroups()) {
                logger.info("    ---群---");
                logger.info("    指令前缀: " + group.getPrefix());
                logger.info("    启用的QQ群:" + group.getGroupsId());
                logger.info("    允许执行指令的群员QQ(全局管理以外): "
                        + (group.allowAllUser() ? "【所有用户】" : group.getWhitelistQQ()));
                if (group.allowAllUser()) {
                    logger.warning("    【警告】此指令被配置为允许所有群员执行，请注意安全！");
                }
            }
        }
    }

    private void printChatConfig(ChatConfig conf) {
        logger.info("----聊天转发配置:ChatConfig-----");
        logger.info("启用聊天转发:" + conf.getEnableChatForward());
        for (var target : conf.getTargets()) {
            logger.info("-------");
            logger.info("目标连接ID: " + target.getConnectionId());
            logger.info("注释: " + target.getComments());
            logger.info("启用: " + target.getEnable());
            var auth = target.getAuthentication();
            var privateChat = auth.getPrivateChat();
            logger.info("---私聊聊天转发鉴权---");
            logger.info("私聊转发前缀: " + privateChat.getPrefix());
            logger.info("允许私聊转发的QQ(全局管理以外): "
                    + (privateChat.allowAllUser() ? "所有群员" : privateChat.getWhitelistQQ()));
            logger.info("---群聊聊天转发鉴权---");
            for (var group : auth.getGroups()) {
                System.out.println("---群---");
                logger.info("转发前缀: " + group.getPrefix());
                logger.info("启用的QQ群:" + group.getGroupsId());
                logger.info("允许转发聊天的群员QQ(全局管理以外): "
                        + (group.allowAllUser() ? "所有群员" : group.getWhitelistQQ()));
            }
        }
    }

}