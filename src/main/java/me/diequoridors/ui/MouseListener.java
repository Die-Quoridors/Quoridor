package me.diequoridors.ui;

import me.diequoridors.world.Player;
import me.diequoridors.world.World;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Optional;

public class MouseListener extends MouseAdapter {

    private final Renderer renderer;
    private final World world;

    private Player movingPlayer;

    public MouseListener(Renderer renderer, World world) {
        this.renderer = renderer;
        this.world = world;
        renderer.canvas.addMouseListener(this);
    }

    public void removeListener() {
        renderer.canvas.removeMouseListener(this);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        Optional<Player> clickedPlayer = world.players.stream().filter(player ->
                e.getX() > renderer.coordinatesToScreen(player.x)
                && e.getX() < renderer.coordinatesToScreen(player.x + 1)
                && e.getY() > renderer.coordinatesToScreen(player.y)
                && e.getY() < renderer.coordinatesToScreen(player.y + 1)
        ).findFirst();
        clickedPlayer.ifPresent(player -> movingPlayer = player);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (movingPlayer == null) {
            return;
        }
        int x = renderer.screenToCoordinates(e.getX());
        int y = renderer.screenToCoordinates(e.getY());
        movingPlayer.move(x, y);
        movingPlayer = null;
    }
}
