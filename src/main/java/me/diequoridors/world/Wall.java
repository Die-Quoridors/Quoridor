package me.diequoridors.world;

public class Wall {

    public int x;
    public int y;
    public WallRotation rotation;

    public Wall(int x, int y, WallRotation rotation) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }

}
