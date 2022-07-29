package dev.raincandy.mirai.websocket;

import com.google.gson.JsonSyntaxException;
import dev.raincandy.sdk.websocket.protocol.ConnectionInfo;
import dev.raincandy.sdk.websocket.protocol.v1.bean.received.ReceivedMainBean;
import dev.raincandy.sdk.websocket.util.GsonUtil;
import dev.raincandy.sdk.websocket.util.MessageHandler;
import dev.raincandy.sdk.websocket.websocket.SDKAbstractWebsocketListener;
import net.mamoe.mirai.console.plugin.jvm.JavaPlugin;
import net.mamoe.mirai.utils.MiraiLogger;
import okhttp3.Response;
import okhttp3.WebSocket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiraiWebsocketListener extends SDKAbstractWebsocketListener {


    private final JavaPlugin miraiPlugin;
    private final MiraiLogger logger;
    private MessageHandler msgHandler = new MessageHandler();

    public MiraiWebsocketListener(JavaPlugin miraiPlugin, ConnectionInfo connInfo) {
        super(connInfo);
        this.logger = miraiPlugin.getLogger();
        this.miraiPlugin = miraiPlugin;
    }

    @Override
    public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
        super.onOpen(webSocket, response);
        var content = """
                --------------------
                Connection: %s established.
                code: %d , msg: %s
                --------------------
                """
                .formatted(getConnInfo().getConnectionId(), response.code(), response.message());
        logger.info(content);
    }

    @Override
    public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
        logger.info("收到服务器消息: \n" + text);

        try {
            //解析，这里要做解析失败的try catch
            var mb = GsonUtil.getGson().fromJson(text, ReceivedMainBean.class);

            getMsgHandler().handleMsg(this, mb, text);

        } catch (JsonSyntaxException e) {
            e.printStackTrace();
            logger.warning("解析错误，无法正确解析的json: \n" + text);
        } catch (Exception e) {
            e.printStackTrace();
            logger.warning("处理时发生其他异常: \n" + text);
        }
    }


    @Override
    public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
        super.onFailure(webSocket, t, response);
        var content = """
                --------------------
                Connection: %s onFailure.
                Exception: %s
                Response: %s
                --------------------
                """.formatted(getConnInfo().getConnectionId(), t, response);
        logger.warning(content);
    }

    @Override
    public void onClosed(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
        logger.info("-----------------");
        logger.info("连接: %s 被服务器关闭，理由: " + reason);
        //非正常原因关闭的，需要重连
        switch (code) {
            case 1000 -> {
                logger.info(getConnInfo().getConnectionId() + " 1000 连接正常关闭，不执行重连。");
            }
            case 4010 -> logger.warning(getConnInfo().getConnectionId() + " 4010 身份验证失败！" + reason);
            case 5000 -> logger.warning(getConnInfo().getConnectionId() + " 5000 服务器错误: " + reason);
            default -> logger.warning(getConnInfo().getConnectionId() + " " + code + " 其他错误: " + reason);
        }
    }

    private ConnectionInfo getConnInfo() {
        return getWebsocketContext().getConnInfo();
    }


    public MessageHandler getMsgHandler() {
        return msgHandler;
    }

    public void setHandler(MessageHandler msgHandler) {
        this.msgHandler = msgHandler;
    }

}
