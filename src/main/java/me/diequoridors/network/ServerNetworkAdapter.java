package me.diequoridors.network;

import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;

public class ServerNetworkAdapter {

    public static ServerInfo getServerInfo(String serverAddress) throws IOException, URISyntaxException {
        String response = Http.getRequest(serverAddress, "/info");
        JSONObject res = new JSONObject(response);

        return new ServerInfo(res);
    }

    public static GameInfo getGameInfo(String serverAddress, String gameId) throws IOException, URISyntaxException {
        String response = Http.getRequest(serverAddress, "/game/" + gameId);
        JSONObject res = new JSONObject(response);

        return new GameInfo(res);
    }

    public static String createGame(String serverAddress, int playerCount, int wallLimit) throws IOException, URISyntaxException {
        JSONObject data = new JSONObject();
        data.put("playerCount", playerCount);
        data.put("wallLimit", wallLimit);
        String dataToSend = data.toString();

        String response = Http.postRequest(serverAddress, "/game", dataToSend);

        JSONObject res = new JSONObject(response);
        return res.getString("gameId");
    }


    public static class ServerInfo {

        public int wsPort;

        public ServerInfo(JSONObject data) {
            wsPort = data.getInt("wsPort");
        }
    }

    public static class GameInfo {

        public int playerCount;
        public int wallLimit;
        public boolean strictPlayer;
        public int currentPlayers;

        public GameInfo(JSONObject data) {
            playerCount = data.getInt("playerCount");
            wallLimit = data.getInt("wallLimit");
            strictPlayer = data.getBoolean("strictPlayer");
            currentPlayers = data.getInt("currentPlayers");
        }

    }
}


