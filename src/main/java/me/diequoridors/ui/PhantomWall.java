package me.diequoridors.ui;

import me.diequoridors.world.Player;
import me.diequoridors.world.Wall;
import me.diequoridors.world.WallRotation;

public class PhantomWall extends Wall {

    public PhantomWall(int x, int y, WallRotation rotation, Player placer) {
        super(x, y, rotation, placer);
    }

    public boolean isValid() {
        return Wall.placementValid(placer.game.world, x, y, rotation);
    }

}
