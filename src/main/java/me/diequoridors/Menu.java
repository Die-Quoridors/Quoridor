package me.diequoridors;

import me.diequoridors.network.ServerNetworkAdapter;
import me.diequoridors.world.Player;
import org.apache.commons.lang3.ArrayUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

public class Menu {

    private static final String[] playerColorTranslations = {"Weiss", "Schwarz", "Zyan", "Rosa"};

    public static void showMainMenu() {
        Frame frame = new Frame("Quoridor");
        frame.setSize(200, 400);
        JPanel panel = new JPanel();

        Button startSingleplayerBtn = new Button("  Einzelspieler  ");
        panel.add(startSingleplayerBtn);
        startSingleplayerBtn.addActionListener(e -> singlePlayerMenu(frame, panel));

        Button startMultiplayerBtn = new Button("  Mehrspieler  ");
        panel.add(startMultiplayerBtn);
        startMultiplayerBtn.addActionListener(e -> multiPlayerMenu(frame, panel));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });
        frame.add(panel);
        frame.setResizable(false);
        frame.setVisible(true);
    }

    private static void singlePlayerMenu(Frame frame, JPanel panel) {
        panel.removeAll();

        Label playerLabel = new Label("Spieleranzahl");
        panel.add(playerLabel);

        JComboBox playerSelection = new JComboBox(new String[]{"2", "4"});
        panel.add(playerSelection);

        Label wallLabel = new Label("Wandanzahl");
        panel.add(wallLabel);

        TextField wallSelection = new TextField("10");
        wallSelection.setSize(150, 15);
        panel.add(wallSelection);

        Label startLabel = new Label(" ".repeat(100));
        panel.add(startLabel);

        Button startBtn = new Button("Start");
        panel.add(startBtn);
        startBtn.addActionListener(e -> {
            int playerCount = Integer.parseInt(((String) Objects.requireNonNull(playerSelection.getSelectedItem())));
            int wallCount = Integer.parseInt(wallSelection.getText());
            frame.dispose();
            new Game(playerCount, wallCount);
        });

        frame.setVisible(true);
    }

    private static void multiPlayerMenu(Frame frame, JPanel panel) {
        panel.removeAll();

        Label playerLabel = new Label("Spieleranzahl");
        panel.add(playerLabel);

        JComboBox playerSelection = new JComboBox(new String[]{"2", "4"});
        panel.add(playerSelection);

        Label wallLabel = new Label("Wandanzahl");
        panel.add(wallLabel);

        TextField wallSelection = new TextField("10");
        panel.add(wallSelection);

        Label serverLabel = new Label("Serveraddresse");
        panel.add(serverLabel);

        TextField serverSelection = new TextField("127.0.0.1:25590");
        serverSelection.setColumns(24);
        panel.add(serverSelection);

        Label gameLabel = new Label("SpielId  (leer = neues Spiel)");
        panel.add(gameLabel);

        TextField gameSelection = new TextField();
        gameSelection.setColumns(24);
        panel.add(gameSelection);

        Label startLabel = new Label(" ".repeat(100));
        panel.add(startLabel);

        Button startBtn = new Button("Start");
        panel.add(startBtn);
        startBtn.addActionListener(e -> {
            int playerCount = Integer.parseInt(((String) Objects.requireNonNull(playerSelection.getSelectedItem())));
            int wallLimit = Integer.parseInt(wallSelection.getText());
            String serverAddress = serverSelection.getText();
            frame.dispose();
            try {
                ServerNetworkAdapter.ServerInfo serverInfo = ServerNetworkAdapter.getServerInfo(serverAddress);

                String gameId;
                if (gameSelection.getText().isEmpty()) {
                    gameId = ServerNetworkAdapter.createGame(serverAddress, playerCount, wallLimit);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(gameId), null);
                    showPopup("SpielId", gameId);
                } else {
                    gameId = gameSelection.getText();
                    ServerNetworkAdapter.GameInfo info = ServerNetworkAdapter.getGameInfo(serverAddress, gameId);
                    playerCount = info.playerCount;
                    wallLimit = info.wallLimit;
                }

                URI wsUri = new URI("ws://" + serverAddress.split(":")[0] + ":" + serverInfo.wsPort);
                new Game(playerCount, wallLimit, wsUri, gameId);
            } catch (URISyntaxException | IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        frame.setVisible(true);
    }

    public static void showWinner(Player player) {
        boolean isWinner = player == player.game.world.ownPlayer;
        int winnerIndex = ArrayUtils.indexOf(Player.playerColorMap, player.getColor());
        if (isWinner) {
            showPopup("Gewonnen", "Du hast gewonnen");
        } else {
            showPopup("Verloren", "Spieler " + playerColorTranslations[winnerIndex] + " hat gewonnen");
        }
    }

    public static void playerLeave(int playerIndex) {
        showPopup("Spieler hat das Spiel verlassen", "Der Spieler " + playerColorTranslations[playerIndex] + " hat das Spiel verlassen!");
    }

    private static void showPopup(String title, String body) {
        JOptionPane.showMessageDialog(null, body, title, JOptionPane.INFORMATION_MESSAGE);
    }

}
