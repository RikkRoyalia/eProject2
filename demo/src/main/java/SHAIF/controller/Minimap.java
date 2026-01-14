package SHAIF.controller;

import SHAIF.model.Room;
import SHAIF.model.RoomConnection;
import SHAIF.model.WorldMap;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Minimap - Essential cho Metroidvania để player không bị lạc
 * FIXED: Properly handles negative world coordinates
 */
public class Minimap {
    private Canvas canvas;
    private GraphicsContext gc;
    private static final double ROOM_SIZE = 25; // Kích thước mỗi room trên map
    private static final double PADDING = 15; // Padding around minimap

    // World bounds (calculated from all rooms)
    private double worldMinX, worldMaxX;
    private double worldMinY, worldMaxY;
    private double worldWidth, worldHeight;

    // Scale factor (calculated to fit all rooms)
    private double scale;

    public Minimap(double width, double height) {
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        calculateWorldBounds();
        calculateScale();
    }

    /**
     * Calculate bounds of entire world from all rooms
     * This handles negative coordinates properly
     */
    private void calculateWorldBounds() {
        WorldMap worldMap = WorldMap.getInstance();

        worldMinX = Double.MAX_VALUE;
        worldMaxX = Double.MIN_VALUE;
        worldMinY = Double.MAX_VALUE;
        worldMaxY = Double.MIN_VALUE;

        // Find min/max coordinates
        for (Room room : worldMap.getRooms().values()) {
            double roomLeft = room.getWorldX();
            double roomRight = room.getWorldX() + room.getWidth();
            double roomTop = room.getWorldY();
            double roomBottom = room.getWorldY() + room.getHeight();

            worldMinX = Math.min(worldMinX, roomLeft);
            worldMaxX = Math.max(worldMaxX, roomRight);
            worldMinY = Math.min(worldMinY, roomTop);
            worldMaxY = Math.max(worldMaxY, roomBottom);
        }

        worldWidth = worldMaxX - worldMinX;
        worldHeight = worldMaxY - worldMinY;

        System.out.println("=== Minimap World Bounds ===");
        System.out.println("World X: " + worldMinX + " to " + worldMaxX + " (width: " + worldWidth + ")");
        System.out.println("World Y: " + worldMinY + " to " + worldMaxY + " (height: " + worldHeight + ")");
    }

    /**
     * Calculate scale to fit all rooms in canvas
     */
    private void calculateScale() {
        double availableWidth = canvas.getWidth() - (2 * PADDING);
        double availableHeight = canvas.getHeight() - (2 * PADDING);

        // Scale to fit within available space
        double scaleX = availableWidth / worldWidth;
        double scaleY = availableHeight / worldHeight;

        // Use smaller scale to ensure everything fits
        scale = Math.min(scaleX, scaleY);

        System.out.println("Minimap scale: " + scale);
    }

    /**
     * Convert world coordinates to canvas coordinates
     */
    private double worldToCanvasX(double worldX) {
        return ((worldX - worldMinX) * scale) + PADDING;
    }

    private double worldToCanvasY(double worldY) {
        return ((worldY - worldMinY) * scale) + PADDING;
    }

    /**
     * Vẽ minimap với proper coordinate transformation
     */
    public void render() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        WorldMap worldMap = WorldMap.getInstance();
        String currentRoomId = worldMap.getCurrentRoomId();

        // Vẽ background
        gc.setFill(Color.rgb(20, 20, 30, 0.9));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Vẽ border
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        gc.strokeRect(2, 2, canvas.getWidth() - 4, canvas.getHeight() - 4);

        // Vẽ connections trước (để rooms vẽ đè lên)
        drawConnections(worldMap, currentRoomId);

        // Vẽ từng room
        for (Room room : worldMap.getRooms().values()) {
            if (!room.isDiscovered()) continue; // Chỉ hiển thị rooms đã khám phá

            drawRoom(room, currentRoomId);
        }

        // Vẽ player position
        drawPlayer(worldMap);

        // Vẽ legend/info
        drawLegend(worldMap);
    }

    /**
     * Vẽ connections giữa các rooms
     */
    private void drawConnections(WorldMap worldMap, String currentRoomId) {
        gc.setStroke(Color.rgb(100, 100, 150, 0.5));
        gc.setLineWidth(2);

        for (Room room : worldMap.getRooms().values()) {
            if (!room.isDiscovered()) continue;

            double roomCenterX = worldToCanvasX(room.getWorldX() + room.getWidth() / 2);
            double roomCenterY = worldToCanvasY(room.getWorldY() + room.getHeight() / 2);

            for (RoomConnection conn : room.getConnections()) {
                Room targetRoom = worldMap.getRooms().get(conn.getTargetRoomId());
                if (targetRoom != null && targetRoom.isDiscovered()) {
                    double targetCenterX = worldToCanvasX(targetRoom.getWorldX() + targetRoom.getWidth() / 2);
                    double targetCenterY = worldToCanvasY(targetRoom.getWorldY() + targetRoom.getHeight() / 2);

                    gc.strokeLine(roomCenterX, roomCenterY, targetCenterX, targetCenterY);
                }
            }
        }
    }

    /**
     * Vẽ một room
     */
    private void drawRoom(Room room, String currentRoomId) {
        // Transform world coordinates to canvas coordinates
        double canvasX = worldToCanvasX(room.getWorldX());
        double canvasY = worldToCanvasY(room.getWorldY());
        double canvasWidth = room.getWidth() * scale;
        double canvasHeight = room.getHeight() * scale;

        // Ensure minimum size for visibility
        canvasWidth = Math.max(canvasWidth, ROOM_SIZE);
        canvasHeight = Math.max(canvasHeight, ROOM_SIZE * 0.6);

        // Màu khác nhau dựa trên state
        if (room.getId().equals(currentRoomId)) {
            // Current room - bright yellow
            gc.setFill(Color.YELLOW);
            gc.setStroke(Color.ORANGE);
            gc.setLineWidth(3);
        } else if (room.getRequiredAbility() != null &&
                !AbilityManager.getInstance().hasAbility(room.getRequiredAbility())) {
            // Locked room - red
            gc.setFill(Color.rgb(200, 50, 50, 0.7));
            gc.setStroke(Color.DARKRED);
            gc.setLineWidth(1.5);
        } else {
            // Discovered room - light blue
            gc.setFill(Color.rgb(100, 150, 200, 0.7));
            gc.setStroke(Color.LIGHTBLUE);
            gc.setLineWidth(1.5);
        }

        // Draw room
        gc.fillRect(canvasX, canvasY, canvasWidth, canvasHeight);
        gc.strokeRect(canvasX, canvasY, canvasWidth, canvasHeight);

        // Draw room name if current room
        if (room.getId().equals(currentRoomId)) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 8));
            gc.fillText(room.getName(), canvasX, canvasY - 2);
        }
    }

    /**
     * Vẽ player position
     */
    private void drawPlayer(WorldMap worldMap) {
        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom == null) return;

        // Player's world position (relative to current room)
        double playerWorldX = currentRoom.getWorldX() + worldMap.getPlayerWorldX();
        double playerWorldY = currentRoom.getWorldY() + worldMap.getPlayerWorldY();

        double playerCanvasX = worldToCanvasX(playerWorldX);
        double playerCanvasY = worldToCanvasY(playerWorldY);

        // Draw player as pulsing dot
        double pulseSize = 3 + Math.sin(System.currentTimeMillis() / 200.0) * 1;

        // Outer glow
        gc.setFill(Color.rgb(0, 255, 0, 0.3));
        gc.fillOval(playerCanvasX - pulseSize * 2, playerCanvasY - pulseSize * 2,
                pulseSize * 4, pulseSize * 4);

        // Inner dot
        gc.setFill(Color.LIME);
        gc.fillOval(playerCanvasX - pulseSize, playerCanvasY - pulseSize,
                pulseSize * 2, pulseSize * 2);
    }

    /**
     * Vẽ legend và info
     */
    private void drawLegend(WorldMap worldMap) {
        double legendY = canvas.getHeight() - 40;

        gc.setFont(javafx.scene.text.Font.font("Arial", 9));

        // Current room name
        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom != null) {
            gc.setFill(Color.WHITE);
            gc.fillText("Current: " + currentRoom.getName(), 10, legendY);
        }

        // Room count
        long discoveredRooms = worldMap.getRooms().values().stream()
                .filter(Room::isDiscovered)
                .count();
        long totalRooms = worldMap.getRooms().size();

        gc.setFill(Color.LIGHTBLUE);
        gc.fillText("Rooms: " + discoveredRooms + "/" + totalRooms, 10, legendY + 12);

        // Legend symbols
        double legendX = canvas.getWidth() - 80;

        // Current room indicator
        gc.setFill(Color.YELLOW);
        gc.fillRect(legendX, legendY - 8, 8, 8);
        gc.setFill(Color.WHITE);
        gc.fillText("Current", legendX + 12, legendY);

        // Locked room indicator
        gc.setFill(Color.rgb(200, 50, 50));
        gc.fillRect(legendX, legendY + 4, 8, 8);
        gc.setFill(Color.WHITE);
        gc.fillText("Locked", legendX + 12, legendY + 12);
    }

    /**
     * Update minimap (call this every frame or when needed)
     */
    public void update() {
        render();
    }

    /**
     * Mark current room as discovered
     */
    public void discoverCurrentRoom() {
        WorldMap worldMap = WorldMap.getInstance();
        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom != null && !currentRoom.isDiscovered()) {
            currentRoom.setDiscovered(true);
            System.out.println("🗺️  Discovered: " + currentRoom.getName());
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }

    /**
     * For debugging: print minimap coordinates
     */
    public void debugPrint() {
        System.out.println("\n=== Minimap Debug ===");
        System.out.println("Canvas size: " + canvas.getWidth() + "x" + canvas.getHeight());
        System.out.println("World bounds: (" + worldMinX + "," + worldMinY + ") to (" + worldMaxX + "," + worldMaxY + ")");
        System.out.println("Scale: " + scale);

        WorldMap worldMap = WorldMap.getInstance();
        for (Room room : worldMap.getRooms().values()) {
            if (room.isDiscovered()) {
                double canvasX = worldToCanvasX(room.getWorldX());
                double canvasY = worldToCanvasY(room.getWorldY());
                System.out.println("  " + room.getName() + ": world(" + room.getWorldX() + "," + room.getWorldY() +
                        ") -> canvas(" + canvasX + "," + canvasY + ")");
            }
        }
        System.out.println("====================\n");
    }
}

/*
 * KEY FIXES:
 *
 * 1. WORLD BOUNDS CALCULATION:
 *    - Calculate min/max X and Y from ALL rooms
 *    - This handles negative coordinates (e.g., worldX = -2000, -4000)
 *
 * 2. COORDINATE TRANSFORMATION:
 *    - worldToCanvasX/Y() methods properly transform coordinates
 *    - Formula: canvasPos = ((worldPos - worldMin) * scale) + padding
 *    - This ensures negative world coords map to positive canvas coords
 *
 * 3. SCALE CALCULATION:
 *    - Scale is calculated to fit ALL rooms in canvas
 *    - Uses the smaller of scaleX and scaleY to ensure everything fits
 *
 * 4. EXAMPLE with your data:
 *    Database has:
 *    - Starting Area: worldX = 0
 *    - Crystal Cavern: worldX = -2000
 *    - Ancient Temple: worldX = -4000
 *
 *    worldMinX = -4000, worldMaxX = 6000 (Boss Chamber)
 *    worldWidth = 10000
 *
 *    For Crystal Cavern at worldX = -2000:
 *    canvasX = ((-2000 - (-4000)) * scale) + padding
 *            = (2000 * scale) + padding
 *            = positive number ✓
 *
 * 5. PLAYER POSITION:
 *    - Player position is relative to current room
 *    - Must add room's worldX/Y to get absolute world position
 */