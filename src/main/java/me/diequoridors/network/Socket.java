package me.diequoridors.network;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

import java.net.URI;

public abstract class Socket extends WebSocketClient {

    public Socket(URI serverUrl) {
        super(serverUrl);
        System.out.println("connecting to " + serverUrl);
        connect();
    }

    abstract void onEvent(String event, JSONObject data);
    abstract void onConnect();

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("connected to server");
        onConnect();
    }

    @Override
    public void onMessage(String msg) {
        JSONObject payload = new JSONObject(msg);
        String event = payload.getString("event");
        JSONObject data = payload.getJSONObject("data");
        onEvent(event, data);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("disconnected from server");
    }

    @Override
    public void onError(Exception e) {
        System.out.println(e);
    }

    public void emit(String event, JSONObject data) {
        JSONObject obj = new JSONObject();
        obj.put("event", event);
        obj.put("data", data);
        send(obj.toString());
    }
}
