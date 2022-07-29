package dev.raincandy.mirai.utils;

import com.google.gson.Gson;
import dev.raincandy.mirai.config.v2.ChatConfig;
import dev.raincandy.mirai.config.v2.CommandConfig;
import dev.raincandy.mirai.config.v2.IncomingMinecraftChatConfig;
import dev.raincandy.mirai.config.v2.ServerConfig;
import org.intellij.lang.annotations.Language;

public class Debug {


    @Language("JSON")
    static String serverConfig = "{\n" +
            "  \"version\": 2,\n" +
            "  \"enable\": true,\n" +
            "  \"globalAdminQQ\": [\n" +
            "    1145141919,\n" +
            "    1375796656\n" +
            "  ],\n" +
            "  \"servers\": [\n" +
            "    {\n" +
            "      \"enable\": true,\n" +
            "      \"connectionId\": \"forward-server-1\",\n" +
            "      \"comments\": \"WS转发服务器-1\",\n" +
            "      \"url\": \"http://localhost:3009" +
            "\",\n" +
            "      \"token\": \"sec-key-114514\",\n" +
            "      \"clientId\": \"mirai-client-01\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @Language("JSON")
    static String chatConfig = "{\n" +
            "  \"version\": 1,\n" +
            "  \"enableChatForward\": true,\n" +
            "  \"targets\": [\n" +
            "    {\n" +
            "      \"enable\": true,\n" +
            "      \"connectionId\": \"forward-server-1\",\n" +
            "      \"comments\": \"默认转发配置\",\n" +
            "      \"authentication\": {\n" +
            "        \"privateChat\": {\n" +
            "          \"prefix\": \"#\",\n" +
            "          \"whitelistQQ\": [\n" +
            "            1145141919\n" +
            "          ]\n" +
            "        },\n" +
            "        \"groups\": [\n" +
            "          {\n" +
            "            \"prefix\": \"#\",\n" +
            "            \"groupsId\": [\n" +
            "              123456789,\n" +
            "              1145141919,\n" +
            "              669403824\n" +
            "            ],\n" +
            "            \"whitelistQQ\": []\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"targetClientsId\": [\n" +
            "        \"minecraft-client-1\"\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    @Language("JSON")
    static String cmdConf = "{\n" +
            "  \"version\": 1,\n" +
            "  \"enableCommandForward\": true,\n" +
            "  \"targets\": [\n" +
            "    {\n" +
            "      \"enable\": tru" +
            "e,\n" +
            "      \"connectionId\": \"forward-server-1\",\n" +
            "      \"comments\": \"默认指令转发配置\",\n" +
            "      \"authentication\": {\n" +
            "        \"privateChat\": {\n" +
            "          \"prefix\": \":\",\n" +
            "          \"whitelistQQ\": [\n" +
            "            0\n" +
            "          ]\n" +
            "        },\n" +
            "        \"groups\": [\n" +
            "          {\n" +
            "            \"prefix\": \":\",\n" +
            "            \"groupsId\": [\n" +
            "              123456789,\n" +
            "              1145141919\n" +
            "            ],\n" +
            "            \"whitelistQQ\": [0]\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"targetClientsId\": [\n" +
            "        \"minecraft-client-1\"\n" +
            "      ],\n" +
            "      \"commands\": [\n" +
            "        \"list\",\n" +
            "        \"在线人数\",\n" +
            "        \"リスト\"\n" +
            "      ],\n" +
            "      \"rewrite\": \"glist\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";


    static String incomingMCChatConfig = "{\n" +
            "  \"version\": 1,\n" +
            "  \"mapping\": [\n" +
            "    {\n" +
            "      \"enable\": true,\n" +
            "      \"botQQ\": 2689755649,\n" +
            "      \"comments\": \"测试用MC端\",\n" +
            "      \"incomingClients\": [\n" +
            "        \"minecraft-client-1\"\n" +
            "      ],\n" +
            "      \"targetQQGroups\": [\n" +
            "        669403824" +
            "\n" +
            "      ]\n" +
            "    }\n" +
            "  ]\n" +
            "}";

    public static ChatConfig getChatConfig() {
        return new Gson().fromJson(chatConfig, ChatConfig.class);
    }

    public static CommandConfig getCommandConfig() {
        return new Gson().fromJson(cmdConf, CommandConfig.class);
    }

    public static ServerConfig getServerConfig() {
        return new Gson().fromJson(serverConfig, ServerConfig.class);
    }

    public static IncomingMinecraftChatConfig getIncomingMinecraftChatConfig() {
        return new Gson().fromJson(incomingMCChatConfig, IncomingMinecraftChatConfig.class);
    }

}
