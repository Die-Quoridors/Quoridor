package me.diequoridors.world;

import me.diequoridors.Game;
import me.diequoridors.world.wallsolver.WallSolver;

import java.util.ArrayList;

public class World {

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Wall> walls = new ArrayList<>();
    public int wallLimit;
    private final Game game;
    public Player ownPlayer;
    public final WallSolver wallSolver = new WallSolver(this);

    public World(int wallLimit, Game game) {
        this.wallLimit = wallLimit;
        this.game = game;
    }

    public void populatePlayers(int playerCount) throws Error {
        if (playerCount > Player.playerStartPosMap.length) {
            throw new Error("Too many players! Max: " + Player.playerStartPosMap.length);
        }

        for (int i = 0; i < playerCount; i++) {
            int[] startPos = Player.playerStartPosMap[i];
            players.add(new Player(startPos[0], startPos[1], i, game));
        }
    }
    
}
