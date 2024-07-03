package me.diequoridors.world;

public class Player {

    public int x;
    public int y;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

}
