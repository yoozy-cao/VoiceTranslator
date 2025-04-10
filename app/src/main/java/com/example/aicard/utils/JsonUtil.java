package com.example.aicard.utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    private static final String TAG = JsonUtil.class.getSimpleName();

    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String toJson(JSONObject jsonObject) {
        try {
            // 将 JSONObject 转换为格式化后的 JSON 字符串，缩进为 4 个空格
            return jsonObject.toString(4);
        } catch (JSONException e) {
            Log.w(TAG, "JSON 格式化错误: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static String toJson(JSONArray jsonArray) {
        try {
            // 将 JSONObject 转换为格式化后的 JSON 字符串，缩进为 4 个空格
            return jsonArray.toString(4);
        } catch (JSONException e) {
            Log.w(TAG, "JSON 格式化错误: ", e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static <T> String toJson(T obj) {
        return simpleGson().toJson(obj);
    }

    public static <T> T toObject(String json, Class<T> clazz) {
        return simpleGson().fromJson(json, clazz);
    }

    private static final Gson INSTANCE = new GsonBuilder()
            .setPrettyPrinting()    // 格式化
            .serializeNulls()   // 输出空值null
            .setDateFormat(DATE_FORMAT)
            // .addSerializationExclusionStrategy(new GsonAdapter_View())
            // .setExclusionStrategies(new GsonAdapter_View()) // 应用排除策略
            // .registerTypeAdapter(Date.class, new DateAdapter())      // 自定义类型适配器
            // .excludeFieldsWithoutExposeAnnotation()     // 仅处理 @Expose 注解字段
            // .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)     // setFieldNamingStrategy  {"user_name":"Charlie","user_age":30}
            // .setFieldNamingStrategy()
            // .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)    // 排除字段 忽略transient, static字段
            .create();

    public static Gson simpleGson() {
        return INSTANCE;
    }
}
