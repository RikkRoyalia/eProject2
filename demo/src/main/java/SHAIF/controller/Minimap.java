package SHAIF.controller;

import SHAIF.model.Room;
import SHAIF.model.WorldMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.io.*;
import java.util.*;

/**
 * Minimap - Essential cho Metroidvania để player không bị lạc
 */
public class Minimap {
    private Canvas canvas;
    private GraphicsContext gc;
    private static final double ROOM_SIZE = 20; // Kích thước mỗi room trên map
    private static final double SCALE = 0.05; // Scale world coordinates

    public Minimap(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
    }

    /**
     * Vẽ minimap
     */
    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        WorldMap worldMap = WorldMap.getInstance();
        String currentRoomId = worldMap.getCurrentRoomId();

        // Vẽ background
        gc.setFill(Color.rgb(20, 20, 30, 0.8));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Vẽ border
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(2, 2, canvas.getWidth() - 4, canvas.getHeight() - 4);

        // Vẽ từng room
        for (Room room : worldMap.getRooms().values()) {
            if (!room.isDiscovered()) continue; // Chỉ hiển thị rooms đã khám phá

            double x = room.getWorldX() * SCALE + 10;
            double y = room.getWorldY() * SCALE + 10;

            // Màu khác nhau cho current room
            if (room.getId().equals(currentRoomId)) {
                gc.setFill(Color.YELLOW);
            } else if (room.getRequiredAbility() != null &&
                    !AbilityManager.getInstance().hasAbility(room.getRequiredAbility())) {
                gc.setFill(Color.RED); // Room locked
            } else {
                gc.setFill(Color.LIGHTBLUE);
            }

            gc.fillRect(x, y, ROOM_SIZE, ROOM_SIZE);

            // Vẽ border cho room
            gc.setStroke(Color.WHITE);
            gc.strokeRect(x, y, ROOM_SIZE, ROOM_SIZE);
        }

        // Vẽ player position
        double playerX = worldMap.getPlayerWorldX() * SCALE + 10;
        double playerY = worldMap.getPlayerWorldY() * SCALE + 10;
        gc.setFill(Color.LIME);
        gc.fillOval(playerX - 2, playerY - 2, 4, 4);
    }

    public Canvas getCanvas() {
        return canvas;
    }
}