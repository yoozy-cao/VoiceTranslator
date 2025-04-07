package com.example.aicard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * JSON格式化工具类
 */
public class JsonFormatter {

    /**
     * 美化JSON字符串
     *
     * @param jsonString 原始JSON字符串
     * @return 格式化后的JSON字符串
     */
    public static String prettyPrintJson(String jsonString) {
        try {
            // 解析JSON
            Object jsonObj = JSON.parse(jsonString);
            // 使用SerializerFeature.PrettyFormat进行格式化
            return JSON.toJSONString(jsonObj,
                    SerializerFeature.PrettyFormat,
                    SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat);
        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            return jsonString;
        }
    }

    /**
     * 美化控制台输出的JSON
     *
     * @param jsonString 原始JSON字符串
     */
    public static void printBeautifiedJson(String jsonString) {
        System.out.println("----------------------------------------");
        System.out.println(prettyPrintJson(jsonString));
        System.out.println("----------------------------------------");
    }
} 