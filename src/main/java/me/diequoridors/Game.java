package me.diequoridors;

import me.diequoridors.network.GameNetworkAdapter;
import me.diequoridors.ui.KeyListener;
import me.diequoridors.ui.MouseListener;
import me.diequoridors.ui.Renderer;
import me.diequoridors.ui.WindowInteractions;
import me.diequoridors.world.Player;
import me.diequoridors.world.World;

import java.net.URI;
import java.util.Arrays;


public class Game {

    public boolean playerStrictMode = false;

    public final Renderer renderer;
    private final WindowInteractions windowInteractions;
    public final MouseListener mouseListener;
    private final KeyListener keyListener;
    public final World world;
    public GameNetworkAdapter networkAdapter;
    public int turnPlayer = 0;
    
    public Game(int playerCount, int wallLimit) {
        world = new World(wallLimit, this);
        renderer = new Renderer(this);
        windowInteractions = new WindowInteractions(this);
        mouseListener = new MouseListener(this);
        keyListener = new KeyListener(this);

        world.populatePlayers(playerCount);
    }

    public Game(int playerCount, int wallLimit, URI serverUrl, String gameId) {
        this(playerCount, wallLimit);

        networkAdapter = new GameNetworkAdapter(serverUrl, this, gameId);
    }

    // dummy Game
    public Game(World worldToClone) {
        world = new World(worldToClone.wallLimit, this);
        renderer = null;
        windowInteractions = null;
        mouseListener = null;
        keyListener = null;

        world.walls.addAll(worldToClone.walls);
        world.players.addAll(worldToClone.players);
    }

    public void exit() {
        keyListener.removeListener();
        mouseListener.removeListener();
        windowInteractions.removeListener();
        if (networkAdapter != null) {
            networkAdapter.close();
        }
        renderer.exit();
//        System.exit(0);
        Menu.showMainMenu();
    }

    public Player getWinner() {
        for (int i = 0; i < world.players.size(); i++) {
            Player player = world.players.get(i);
            if (player.isInWinArea()) {
                return player;
            }
        }
        
        return null;
    }

    public void updatePlayers() {
        Player winner = getWinner();
        if (winner == null) {
            return;
        }

        Menu.showWinner(winner);
        exit();
    }

    public void nextTurn() {
        int[] turnMappings = Player.playerTurnMappings[world.players.size()];
        int turnStage = Arrays.stream(turnMappings).reduce(-1, (p, o) -> o == turnPlayer ? -(p + 1) : (p < 0 ? p - 1 : p));
        if (turnStage + 1 >= world.players.size()) {
            turnStage = 0;
        } else {
            turnStage++;
        }
        turnPlayer = turnMappings[turnStage];
    }
}
