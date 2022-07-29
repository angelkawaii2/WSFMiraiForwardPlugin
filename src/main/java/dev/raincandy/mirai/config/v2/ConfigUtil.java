package dev.raincandy.mirai.config.v2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ConfigUtil {

    private static Gson gson
            = new GsonBuilder()
            .registerTypeAdapter(MinecraftServernameRewriteConfig.ServerWorld.WorldsDTO.class,
                    MinecraftServernameRewriteConfig.ServerWorld.WorldsDTO.getJsonDeserializer())
            .create();


    public static <T> T getConfigBean(File conf, Class<T> clazz) throws FileNotFoundException {
        return gson.fromJson(new FileReader(conf), clazz);
    }

}
