package me.diequoridors.world;

public class Wall {
    
    public int x;
    public int y;
    public WallRotation rotation;
    public Player placer;

    public Wall(int x, int y, WallRotation rotation, Player placer) {
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.placer = placer;
    }
    

}
