package SHAIF.database;

import SHAIF.model.MapData;
import java.util.List;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===");

        // Test 1: Connection
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✓ Database connection successful!");
        } else {
            System.out.println("✗ Database connection failed!");
            return;
        }

        // Test 2: Load map
        System.out.println("\n=== Loading Map 1 ===");
        MapData mapData = MapDAO.loadMap(1);

        if (mapData != null) {
            System.out.println("✓ Map loaded: " + mapData.getMapName());
            System.out.println("  - Screen: " + mapData.getScreenWidth() + "x" + mapData.getScreenHeight());
            System.out.println("  - Ground Level: " + mapData.getGroundLevel());
            System.out.println("  - Platforms: " + mapData.getPlatforms().size());
            System.out.println("  - Obstacles: " + mapData.getObstacles().size());
            System.out.println("  - Enemies: " + mapData.getEnemies().size());
        } else {
            System.out.println("✗ Failed to load map!");
        }

        // Test 3: Get all maps
        System.out.println("\n=== All Available Maps ===");
        List<MapData> allMaps = MapDAO.getAllMaps();

        if (!allMaps.isEmpty()) {
            for (MapData map : allMaps) {
                System.out.println("  - Map " + map.getMapId() + ": " + map.getMapName());
            }
        } else {
            System.out.println("  No maps found in database!");
        }

        // Test 4: Create new map (optional)
        System.out.println("\n=== Creating Test Map ===");
        int newMapId = MapDAO.createMap("Test Level", 1920, 1080, 1040, 1800, 600);

        if (newMapId > 0) {
            System.out.println("✓ New map created with ID: " + newMapId);

            // Add some platforms
            boolean added = MapDAO.addPlatform(newMapId, 500, 300, 150, 20, "normal");
            if (added) {
                System.out.println("✓ Platform added to test map");
            }

            // Clean up - delete test map
            boolean deleted = MapDAO.deleteMap(newMapId);
            if (deleted) {
                System.out.println("✓ Test map deleted");
            }
        }

        // Close connection
        DatabaseConnection.closeConnection();
        System.out.println("\n=== Test Complete ===");
    }
}