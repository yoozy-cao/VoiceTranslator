package com.example.aicard.treanlste;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class SpeechTranslateClient extends WebSocketClient {
    public SpeechTranslateClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("SpeechTranslateClient  onopen");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("SpeechTranslateClient  onmessage");
        System.out.println(s);
    }

    @Override
    public void onMessage(ByteBuffer message) {
        System.out.println("SpeechTranslateClient  onmessage");
        System.out.println(new String(message.array(), StandardCharsets.UTF_8));
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("SpeechTranslateClient  onclose");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("SpeechTranslateClient  onerror:" + e.getMessage());
        e.printStackTrace();
    }
}
