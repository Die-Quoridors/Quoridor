package me.diequoridors.network;

import me.diequoridors.Game;
import me.diequoridors.Menu;
import me.diequoridors.world.Player;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class GameNetworkAdapter {

    private final Socket socket;
    private final Game game;

    private final String gameId;

    public GameNetworkAdapter(URI serverUrl, Game game, String gameId) {
        this.game = game;
        this.gameId = gameId;
        socket = new Socket(serverUrl) {
            @Override
            void onEvent(String event, JSONObject data) {
                switch (event) {
                    case "error":
                        hndError(data);
                        break;
                    case "gameInit":
                        hndGameInit(data);
                        break;
                    case "nextPlayer":
                        hndNextPlayer(data);
                        break;
                    case "playerJoin":
                        hndPlayerJoin(data);
                        break;
                    case "gameLeave":
                        hndGameLeave(data);
                        break;
                    case "playerMove":
                        hndPlayerMove(data);
                        break;
                    case "wallPlace":
                        hndWallPlace(data);
                        break;
                    case "syncResponse":
                        hndSync(data);
                        break;
                    default:
                        System.out.println("Event " + event + " not found");
                        System.out.println(data);
                        break;
                }
            }

            @Override
            void onConnect() {
                sendGameJoin();
            }
        };
    }

    public void close() {
        socket.close();
    }

    private void hndError(JSONObject data) {
        String error = data.getString("error");
        System.out.print("Socket Error: ");
        System.out.println(error);
        game.exit();
    }

    private void sendGameJoin() {
        JSONObject data = new JSONObject();
        data.put("game", gameId);
        socket.emit("gameJoin", data);
    }

    private void hndGameLeave(JSONObject data) {
        int playerIndex = data.getInt("player");
        Menu.playerLeave(playerIndex);
    }

    private void hndGameInit(JSONObject data) {
        int playerCount = data.getInt("playerCount");
        game.world.players.clear();
        game.world.populatePlayers(playerCount);

        int ownPlayerId = data.getInt("player");
        game.world.ownPlayer = game.world.players.get(ownPlayerId);

        game.playerStrictMode = data.getBoolean("strictPlayer");

        System.out.println("connected to game as player " + ownPlayerId);
    }

    private void hndNextPlayer(JSONObject data) {
        game.turnPlayer = data.getInt("player");
    }

    private void hndPlayerJoin(JSONObject data) {
        int playerIndex = data.getInt("player");
        Menu.playerJoin(playerIndex);
    }

    public void sendPlayerMove(Player player) {
        JSONObject data = new JSONObject();
        data.put("player", player.playerId);
        data.put("x", player.x);
        data.put("y", player.y);
        socket.emit("playerMove", data);
    }

    private void hndPlayerMove(JSONObject data) {
        int id = data.getInt("player");
        int x = data.getInt("x");
        int y = data.getInt("y");
        Player player = game.world.players.get(id);
        player.x = x;
        player.y = y;
        game.updatePlayers();
    }

    public void sendWallPlace(Wall wall) {
        JSONObject data = new JSONObject();
        data.put("player", wall.placer.playerId);
        data.put("x", wall.x);
        data.put("y", wall.y);
        data.put("rotation", wall.rotation.toString());
        socket.emit("wallPlace", data);
    }

    private void hndWallPlace(JSONObject data) {
        int playerId = data.getInt("player");
        int x = data.getInt("x");
        int y = data.getInt("y");
        String rotationId = data.getString("rotation");
        Player player = game.world.players.get(playerId);
        WallRotation rotation = WallRotation.valueOf(rotationId);
        Wall wall = new Wall(x, y, rotation, player);
        game.world.walls.add(wall);
    }

    private void hndSync(JSONObject data) {
        JSONArray players = data.getJSONArray("players");
        JSONArray walls = data.getJSONArray("walls");

        for (int i = 0; i < players.length(); i++) {
            JSONObject playerData = players.getJSONObject(i);
            int x = playerData.getInt("x");
            int y = playerData.getInt("y");
            
            Player player = game.world.players.get(i);
            player.x = x;
            player.y = y;
        }

        game.world.walls.clear();
        for (int i = 0; i < walls.length(); i++) {
            JSONObject wallData = walls.getJSONObject(i);
            int x = wallData.getInt("x");
            int y = wallData.getInt("y");
            WallRotation rotation = WallRotation.valueOf(wallData.getString("rotation"));
            Player player = game.world.players.get(wallData.getInt("placer"));
            game.world.walls.add(new Wall(x, y, rotation, player));
        }
    }

}
