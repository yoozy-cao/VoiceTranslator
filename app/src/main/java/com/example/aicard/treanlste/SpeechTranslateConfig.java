package com.example.aicard.treanlste;

import com.volcengine.helper.Const;
import com.volcengine.model.ApiInfo;
import com.volcengine.model.Credentials;
import com.volcengine.model.ServiceInfo;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SpeechTranslateConfig {
    public static String accessKey = "your access key";
    public static String secretKey = "your secret key";

    public static String api = "SpeechTranslate";
    public static String path = "/api/translate/speech/v1/";
    public static String host = "translate.volces.com";

    public static ServiceInfo serviceInfo = new ServiceInfo(
            new HashMap<>() {
                {
                    put(Const.CONNECTION_TIMEOUT, 5000);
                    put(Const.SOCKET_TIMEOUT, 5000);
                    put(Const.Host, host);
                    put(Const.Header, new ArrayList<Header>() {
                        {
                            add(new BasicHeader("Accept", "application/json"));
                        }
                    });
                    put(Const.Credentials, new Credentials(Const.REGION_CN_NORTH_1, "translate"));
                }
            }
    );


    public static Map<String, ApiInfo> apiInfoList = new HashMap<>() {
        {
            put(api, new ApiInfo(
                    new HashMap<>() {
                        {
                            put(Const.Method, "GET");
                            put(Const.Path, path);
                            put(Const.Query, new ArrayList<NameValuePair>() {
                                {
                                    add(new BasicNameValuePair("Action", api));
                                    add(new BasicNameValuePair("Version", "2020-06-01"));
                                }
                            });
                        }
                    }
            ));
        }
    };
}
