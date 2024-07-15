package me.diequoridors.network;

import me.diequoridors.Game;
import me.diequoridors.world.Player;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;
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
                    case "playerMove":
                        hndPlayerMove(data);
                        break;
                    case "wallPlace":
                        hndWallPlace(data);
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

    private void hndGameInit(JSONObject data) {
        int playerCount = data.getInt("playerCount");
        game.world.players.clear();
        game.world.populatePlayers(playerCount);

        int ownPlayerId = data.getInt("player");
        game.world.ownPlayer = game.world.players.get(ownPlayerId);

        game.playerStrictMode = data.getBoolean("strictPlayer");

        System.out.println("connected to game as player " + ownPlayerId);
    }

    public void sendPlayerMove(Player player) {
        JSONObject data = new JSONObject();
        data.put("player", Player.playerToIndex(player));
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
    }

    public void sendWallPlace(Wall wall) {
        JSONObject data = new JSONObject();
        data.put("player", Player.playerToIndex(wall.placer));
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

}
