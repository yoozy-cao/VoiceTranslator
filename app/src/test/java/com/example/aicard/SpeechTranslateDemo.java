package com.example.aicard;

import com.example.aicard.treanlste.SpeechTranslateClient;
import com.example.aicard.treanlste.SpeechTranslateConfig;
import com.example.aicard.treanlste.SpeechTranslateService;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.Base64;

public class SpeechTranslateDemo {

    @Test
    public void translate() throws Exception {
        File file = new File("D:\\AndroidStudioProjects\\AICard\\cn_test.pcm");
        System.out.println(file.getAbsolutePath());
        if (!file.exists()) return;

        SpeechTranslateService translateService = new SpeechTranslateService(SpeechTranslateConfig.serviceInfo, SpeechTranslateConfig.apiInfoList);
        translateService.setAccessKey(SpeechTranslateConfig.accessKey);
        translateService.setSecretKey(SpeechTranslateConfig.secretKey);

        String signUrl = translateService.getSignUrl(SpeechTranslateConfig.api, null);
        URI url = new URI("wss://" + SpeechTranslateConfig.host + SpeechTranslateConfig.path + "?" + signUrl);
        System.out.println(url);


        // open websocket
        SpeechTranslateClient client = new SpeechTranslateClient(url);

        client.connectBlocking();
        client.send("{\n" +
                "    \"Configuration\": {\n" +
                "        \"SourceLanguage\": \"zh\",\n" +
                "        \"TargetLanguages\": [\n" +
                "            \"en\"\n" +
                "        ],\n" +
                "        \"HotWordList\": [\n" +
                "            {\n" +
                "                \"Word\": \"hello\",\n" +
                "                \"Scale\": 1\n" +
                "            }\n" +
                "        ]\n" +
                "    }\n" +
                "}");


        byte[] buffer = new byte[200 * 32];
        int bytesLeft = 100 * 1024 * 1024;
        try (FileInputStream fis = new FileInputStream(file)) {
            while (bytesLeft > 0) {
                int read = fis.read(buffer, 0, Math.min(bytesLeft, buffer.length));
                if (read == -1) {
                    break;
                }
                client.send(bytesToMessage(buffer));
                Thread.sleep(200);
                bytesLeft -= read;
            }
        } finally {
            client.send("{\n" +
                    "    \"End\": true\n" +
                    "}");
        }
    }

    static String bytesToMessage(byte[] data) {
        String base64Data = Base64.getEncoder().encodeToString(data);
        return "{\n" +
                "    \"AudioData\": \"" +
                base64Data +
                "\"\n" +
                "}";
    }
}
