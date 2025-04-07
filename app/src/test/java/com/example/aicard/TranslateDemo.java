package com.example.aicard;

import com.alibaba.fastjson.JSON;
import com.volcengine.model.request.translate.LangDetectRequest;
import com.volcengine.model.request.translate.TranslateTextRequest;
import com.volcengine.model.response.translate.LangDetectResponse;
import com.volcengine.model.response.translate.TranslateTextResponse;
import com.volcengine.service.translate.ITranslateService;
import com.volcengine.service.translate.impl.TranslateServiceImpl;

import java.util.Arrays;
import java.util.List;

/**
 * 火山引擎文本翻译演示类
 * 演示如何使用火山引擎的文本翻译服务，包括：
 * 1. 语言检测
 * 2. 文本翻译
 */
public class TranslateDemo {

    // 待翻译的文本列表
    private static List<String> textList = Arrays.asList("In the PALM trial, ZMapp served as a control, meaning scientists used it as a baseline and compared the three other treatments to it.");

    /**
     * 主方法，演示文本翻译功能
     *
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        // 获取翻译服务实例
        ITranslateService translateService = TranslateServiceImpl.getInstance();

        // 设置访问凭证（如果未在~/.volc/config中配置）
        translateService.setAccessKey("");
        translateService.setSecretKey("");

        // 语言检测
        try {
            // 创建语言检测请求
            LangDetectRequest langDetectRequest = new LangDetectRequest();
            langDetectRequest.setTextList(textList);

            // 执行语言检测并打印结果
            LangDetectResponse langDetectResponse = translateService.langDetect(langDetectRequest);
            JsonFormatter.printBeautifiedJson(JSON.toJSONString(langDetectResponse));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 文本翻译
        try {
            // 创建翻译请求
            TranslateTextRequest translateTextRequest = new TranslateTextRequest();
            // translateTextRequest.setSourceLanguage("en"); // 不设置表示自动检测源语言
            translateTextRequest.setTargetLanguage("zh");  // 设置目标语言为中文
            translateTextRequest.setTextList(textList);    // 设置待翻译文本

            // 执行翻译并打印结果
            TranslateTextResponse translateText = translateService.translateText(translateTextRequest);
            JsonFormatter.printBeautifiedJson(JSON.toJSONString(translateText));
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 释放资源
        translateService.destroy();
    }
}
