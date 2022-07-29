package dev.raincandy.mirai.config.v2;

/**
 * 管理全局配置文件
 */
public class WSFGlobalConfig {

    private ChatConfig chatConfig;
    private CommandConfig cmdConfig;
    private ServerConfig serverConfig;
    private IncomingMinecraftChatConfig incomingMinecraftChatConfig;
    private MinecraftServernameRewriteConfig mcServernameRewriteConfig;


    private static boolean debug = false;

    private static final WSFGlobalConfig GLOBAL_CONFIG = new WSFGlobalConfig();

    public ChatConfig getChatConfig() {
        return chatConfig;
    }

    public void setChatConfig(ChatConfig chatConfig) {
        this.chatConfig = chatConfig;
    }

    public CommandConfig getCmdConfig() {
        return cmdConfig;
    }

    public void setCmdConfig(CommandConfig cmdConfig) {
        this.cmdConfig = cmdConfig;
    }

    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    public void setServerConfig(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    public IncomingMinecraftChatConfig getIncomingMinecraftChatConfig() {
        return incomingMinecraftChatConfig;
    }

    public void setIncomingMinecraftChatConfig(IncomingMinecraftChatConfig incomingMinecraftChatConfig) {
        this.incomingMinecraftChatConfig = incomingMinecraftChatConfig;
    }

    public MinecraftServernameRewriteConfig getMcServernameRewriteConfig() {
        return mcServernameRewriteConfig;
    }

    public void setMcServernameRewriteConfig(MinecraftServernameRewriteConfig mcServernameRewriteConfig) {
        this.mcServernameRewriteConfig = mcServernameRewriteConfig;
    }

    public static WSFGlobalConfig getInstance() {

        return GLOBAL_CONFIG;
    }
}

