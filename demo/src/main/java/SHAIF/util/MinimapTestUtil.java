package SHAIF.util;

import SHAIF.model.Room;
import SHAIF.model.WorldMap;

/**
 * Utility to test and debug minimap coordinates
 */
public class MinimapTestUtil {

    /**
     * Print world layout for visualization
     */
    public static void printWorldLayout() {
        WorldMap worldMap = WorldMap.getInstance();

        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║        WORLD MAP LAYOUT                ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        // Find bounds
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (Room room : worldMap.getRooms().values()) {
            minX = Math.min(minX, room.getWorldX());
            maxX = Math.max(maxX, room.getWorldX());
        }

        System.out.println("World X Range: " + minX + " to " + maxX);
        System.out.println("Total World Width: " + (maxX - minX) + "\n");

        // Print rooms sorted by X position
        worldMap.getRooms().values().stream()
                .sorted((r1, r2) -> Double.compare(r1.getWorldX(), r2.getWorldX()))
                .forEach(room -> {
                    String arrow = getRoomArrow(room, worldMap);
                    String discovered = room.isDiscovered() ? "✓" : "✗";
                    String current = room.getId().equals(worldMap.getCurrentRoomId()) ? " ← YOU ARE HERE" : "";

                    System.out.printf("  %s  worldX: %6.0f  %-20s [%s]%s\n",
                            arrow, room.getWorldX(), room.getName(), discovered, current);
                });

        System.out.println("\n" + getAsciiMap(worldMap));
    }

    /**
     * Get arrow showing position
     */
    private static String getRoomArrow(Room room, WorldMap worldMap) {
        double worldX = room.getWorldX();
        if (worldX < -2000) return "◀◀◀";
        if (worldX < 0) return "◀◀";
        if (worldX == 0) return "◆";
        if (worldX < 4000) return "▶▶";
        return "▶▶▶";
    }

    /**
     * Create ASCII map visualization
     */
    private static String getAsciiMap(WorldMap worldMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("ASCII MAP (X-axis only):\n\n");
        sb.append("  ◀◀◀ WEST          CENTER          EAST ▶▶▶\n");
        sb.append("  ");

        // Create a simple linear visualization
        double minX = -4000;
        double maxX = 6000;
        int width = 50;

        // Create array
        char[] map = new char[width];
        for (int i = 0; i < width; i++) map[i] = '─';

        // Place rooms
        for (Room room : worldMap.getRooms().values()) {
            int pos = (int)((room.getWorldX() - minX) / (maxX - minX) * (width - 1));
            if (pos >= 0 && pos < width) {
                if (room.getId().equals(worldMap.getCurrentRoomId())) {
                    map[pos] = '█'; // Current room
                } else if (room.isDiscovered()) {
                    map[pos] = '■'; // Discovered
                } else {
                    map[pos] = '□'; // Undiscovered
                }
            }
        }

        sb.append(new String(map));
        sb.append("\n\n");
        sb.append("  Legend: █ = Current  ■ = Discovered  □ = Undiscovered\n");

        return sb.toString();
    }

    /**
     * Test coordinate transformation
     */
    public static void testCoordinateTransform() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║   COORDINATE TRANSFORMATION TEST       ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        WorldMap worldMap = WorldMap.getInstance();

        // Calculate world bounds
        double worldMinX = Double.MAX_VALUE;
        double worldMaxX = Double.MIN_VALUE;

        for (Room room : worldMap.getRooms().values()) {
            worldMinX = Math.min(worldMinX, room.getWorldX());
            worldMaxX = Math.max(worldMaxX, room.getWorldX());
        }

        double worldWidth = worldMaxX - worldMinX;

        // Test minimap canvas (assuming 200x150)
        double canvasWidth = 200;
        double padding = 15;
        double availableWidth = canvasWidth - (2 * padding);
        double scale = availableWidth / worldWidth;

        System.out.println("World bounds: " + worldMinX + " to " + worldMaxX);
        System.out.println("World width: " + worldWidth);
        System.out.println("Canvas width: " + canvasWidth);
        System.out.println("Available width: " + availableWidth);
        System.out.println("Scale: " + scale);
        System.out.println();

        // Test each room
        System.out.println("Room transformations:");
        for (Room room : worldMap.getRooms().values()) {
            double worldX = room.getWorldX();
            double canvasX = ((worldX - worldMinX) * scale) + padding;

            System.out.printf("  %-20s: worldX=%6.0f -> canvasX=%6.2f %s\n",
                    room.getName(), worldX, canvasX,
                    (canvasX >= 0 && canvasX <= canvasWidth) ? "✓" : "✗ OUT OF BOUNDS!");
        }
    }

    /**
     * Verify all rooms will be visible
     */
    public static void verifyVisibility() {
        System.out.println("\n╔════════════════════════════════════════╗");
        System.out.println("║      MINIMAP VISIBILITY CHECK          ║");
        System.out.println("╚════════════════════════════════════════╝\n");

        WorldMap worldMap = WorldMap.getInstance();
        int totalRooms = worldMap.getRooms().size();
        int visibleRooms = 0;

        // Simulate minimap rendering
        double canvasWidth = 200;
        double canvasHeight = 150;
        double padding = 15;

        // Calculate bounds
        double worldMinX = Double.MAX_VALUE, worldMaxX = Double.MIN_VALUE;
        double worldMinY = Double.MAX_VALUE, worldMaxY = Double.MIN_VALUE;

        for (Room room : worldMap.getRooms().values()) {
            worldMinX = Math.min(worldMinX, room.getWorldX());
            worldMaxX = Math.max(worldMaxX, room.getWorldX());
            worldMinY = Math.min(worldMinY, room.getWorldY());
            worldMaxY = Math.max(worldMaxY, room.getWorldY());
        }

        double worldWidth = worldMaxX - worldMinX;
        double worldHeight = worldMaxY - worldMinY;

        double scaleX = (canvasWidth - 2 * padding) / worldWidth;
        double scaleY = (canvasHeight - 2 * padding) / worldHeight;
        double scale = Math.min(scaleX, scaleY);

        System.out.println("Canvas: " + canvasWidth + "x" + canvasHeight);
        System.out.println("World bounds: (" + worldMinX + "," + worldMinY + ") to (" + worldMaxX + "," + worldMaxY + ")");
        System.out.println("Scale: " + scale);
        System.out.println();

        // Check each room
        for (Room room : worldMap.getRooms().values()) {
            double canvasX = ((room.getWorldX() - worldMinX) * scale) + padding;
            double canvasY = ((room.getWorldY() - worldMinY) * scale) + padding;

            boolean visible = (canvasX >= 0 && canvasX <= canvasWidth &&
                    canvasY >= 0 && canvasY <= canvasHeight);

            if (visible) visibleRooms++;

            System.out.printf("  %-20s: canvas(%.1f, %.1f) %s\n",
                    room.getName(), canvasX, canvasY, visible ? "✓ VISIBLE" : "✗ HIDDEN");
        }

        System.out.println("\n" + (visibleRooms == totalRooms ? "✅ ALL ROOMS VISIBLE!" :
                "⚠️  " + (totalRooms - visibleRooms) + " ROOMS HIDDEN!"));
    }

    /**
     * Run all tests
     */
    public static void runAllTests() {
        printWorldLayout();
        System.out.println("\n" + "=".repeat(50) + "\n");
        testCoordinateTransform();
        System.out.println("\n" + "=".repeat(50) + "\n");
        verifyVisibility();
    }
}

// Usage in your main class or game initialization:
/*
public static void main(String[] args) {
    // Initialize world
    WorldMap.getInstance();

    // Run tests
    MinimapTestUtil.runAllTests();

    // Launch game
    Application.launch(args);
}
*/