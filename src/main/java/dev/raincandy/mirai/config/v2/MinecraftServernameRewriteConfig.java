package dev.raincandy.mirai.config.v2;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MinecraftServernameRewriteConfig {

    @SerializedName("ver")
    private Integer ver;
    @SerializedName("servers")
    private List<ServerWorld> servers;

    public Integer getVer() {
        return ver;
    }

    public List<ServerWorld> getServers() {
        return servers;
    }

    @Nullable
    public ServerWorld getServer(String name) {
        for (ServerWorld s : servers) {
            if (s.originClientId.equals(name)) {
                return s;
            }
        }
        return null;
    }

    //todo 要重新写反序列化
    public static class ServerWorld {

        @SerializedName("originClientId")
        private String originClientId;

        @SerializedName("displayName")
        private String displayName;

        @SerializedName("worlds")
        private WorldsDTO worlds;

        public String getOriginClientId() {
            return originClientId;
        }

        public void setOriginClientId(String originClientId) {
            this.originClientId = originClientId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public WorldsDTO getWorlds() {
            return worlds;
        }

        public void setWorlds(WorldsDTO worlds) {
            this.worlds = worlds;
        }


        public static class WorldsDTO {

            Map<String, String> worldName = new HashMap<>();

            public static JsonDeserializer<WorldsDTO> getJsonDeserializer() {
                return (json, typeOfT, context) -> {
                    Type type = new TypeToken<Map<String, String>>() {
                    }.getType();
                    WorldsDTO w = new WorldsDTO();
                    w.worldName = context.deserialize(json, type);
                    return w;
                };
            }

            /**
             * 获取指定世界名对应的映射
             *
             * @param key 原始世界名
             * @return 如果不存在返回 null
             */
            @Nullable
            public String getWorldName(String key) {
                return worldName.get(key);
            }
        }
    }

}
