package com.example.aicard.treanlste;

import com.volcengine.model.ApiInfo;
import com.volcengine.model.ServiceInfo;
import com.volcengine.service.BaseServiceImpl;

import java.util.Map;

public class SpeechTranslateService extends BaseServiceImpl {

    public SpeechTranslateService(ServiceInfo info, Map<String, ApiInfo> apiInfoList) {
        super(info, apiInfoList);
    }
}
