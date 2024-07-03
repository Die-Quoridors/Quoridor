package me.diequoridors;

import me.diequoridors.renderer.Renderer;
import me.diequoridors.renderer.WindowInteractions;
import me.diequoridors.world.World;


public class Game {

    private final Renderer renderer;
    private final WindowInteractions windowInteractions;

    public Game(int playerCount) {
        World world = new World();
        renderer = new Renderer(world);
        windowInteractions = new WindowInteractions(renderer, this);

        world.populatePlayers(playerCount);
    }

    public void exit() {
        windowInteractions.removeListener();
        renderer.exit();
        System.exit(0);
    }

}
