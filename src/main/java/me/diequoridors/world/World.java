package me.diequoridors.world;

import java.awt.*;
import java.util.ArrayList;

public class World {

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Wall> walls = new ArrayList<>();
    public int wallLimit;

    public World(int wallLimit) {
        this.wallLimit = wallLimit;
    }

    public void populatePlayers(int playerCount) throws Error {
        if (playerCount > Player.playerStartPosMap.length) {
            throw new Error("Too many players! Max: " + Player.playerStartPosMap.length);
        }

        for (int i = 0; i < playerCount; i++) {
            int[] startPos = Player.playerStartPosMap[i];
            Color color = Player.playerColorMap[i];
            players.add(new Player(startPos[0], startPos[1], color, this));
        }
    }
    
}
