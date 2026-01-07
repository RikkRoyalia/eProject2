package SHAIF.database;

import SHAIF.model.MapData;

public class TestDatabase {
    public static void main(String[] args) {
        System.out.println("=== Testing Database Connection ===\n");

        // Test 1: Connection
        if (DatabaseConnection.getConnection() != null) {
            System.out.println("✓ Database connected successfully!\n");
        } else {
            System.out.println("❌ Database connection failed!\n");
            return;
        }

        // Test 2: Load Map 1
        System.out.println("=== Loading Map 1 ===");
        MapData mapData = MapDAO.loadMap(1);

        if (mapData == null) {
            System.out.println("❌ Failed to load map data!");
            return;
        }

        System.out.println("\n=== Map Data Summary ===");
        System.out.println("Map Name: " + mapData.getMapName());
        System.out.println("Screen: " + mapData.getScreenWidth() + "x" + mapData.getScreenHeight());
        System.out.println("Ground Level: " + mapData.getGroundLevel());
        System.out.println("Platforms: " + mapData.getPlatforms().size());
        System.out.println("Hazards: " + mapData.getObstacles().size());
        System.out.println("Enemies: " + mapData.getEnemies().size());
        System.out.println("Items: " + mapData.getItems().size());

        // Test 3: Check if data exists
        if (mapData.getPlatforms().isEmpty()) {
            System.out.println("\n⚠️  WARNING: No platforms found!");
            System.out.println("Please run the SQL script to insert data.");
        } else {
            System.out.println("\n✓ All data loaded successfully!");
        }

        DatabaseConnection.closeConnection();
    }
}