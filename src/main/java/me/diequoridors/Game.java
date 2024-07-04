package me.diequoridors;

import me.diequoridors.ui.MouseListener;
import me.diequoridors.ui.Renderer;
import me.diequoridors.ui.WindowInteractions;
import me.diequoridors.world.World;


public class Game {

    private final Renderer renderer;
    private final WindowInteractions windowInteractions;
    private final MouseListener mouseListener;

    public Game(int playerCount) {
        World world = new World();
        renderer = new Renderer(world);
        windowInteractions = new WindowInteractions(renderer, this);
        mouseListener = new MouseListener(renderer, world);

        world.populatePlayers(playerCount);
    }

    public void exit() {
        mouseListener.removeListener();
        windowInteractions.removeListener();
        renderer.exit();
        System.exit(0);
    }

}
