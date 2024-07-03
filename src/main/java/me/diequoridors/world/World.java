package me.diequoridors.world;

import java.util.ArrayList;

public class World {

    public ArrayList<Player> players = new ArrayList<>();
    public ArrayList<Wall> walls = new ArrayList<>();

    public World() {

    }

    public void placeWalls(int x, int y,  WallRotation rotation) {
        walls.add(new Wall(x, y, rotation));
    }
    
}
