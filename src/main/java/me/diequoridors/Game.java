package me.diequoridors;

import me.diequoridors.ui.KeyListener;
import me.diequoridors.ui.MouseListener;
import me.diequoridors.ui.Renderer;
import me.diequoridors.ui.WindowInteractions;
import me.diequoridors.world.Player;
import me.diequoridors.world.World;


public class Game {

    public final Renderer renderer;
    private final WindowInteractions windowInteractions;
    public final MouseListener mouseListener;
    private final KeyListener keyListener;
    public final World world;
    
    public Game(int playerCount, int wallLimit) {
        world = new World(wallLimit);
        renderer = new Renderer(this);
        windowInteractions = new WindowInteractions(this);
        mouseListener = new MouseListener(this);
        keyListener = new KeyListener(this);

        world.populatePlayers(playerCount);
    }

    public void exit() {
        keyListener.removeListener();
        mouseListener.removeListener();
        windowInteractions.removeListener();
        renderer.exit();
        System.exit(0);
    }

    public Player getWinnner() {
        for (int i = 0; i < world.players.size(); i++) {
            Player player = world.players.get(i);
            int[] winArea = Player.playerWinArea[i];
            if (player.x >= winArea[0] && player.y >= winArea[1] && player.x < winArea[0] + winArea[2] && player.y < winArea[1] + winArea[3]) {
                return player;
            }
        }
        
        return null;
    }
}
