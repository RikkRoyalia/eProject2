package SHAIF.controller;

import java.io.*;

/**
 * MetroidvaniaSaveSystem - Save game state với world exploration
 */
public class MetroidvaniaSaveSystem {
    private static final String SAVE_FILE = "metroidvania_save.dat";

    /**
     * Save toàn bộ game state
     */
    public static void saveGame(SaveData data) {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(SAVE_FILE))) {
            oos.writeObject(data);
            System.out.println("Game saved successfully!");
        } catch (IOException e) {
            System.err.println("Failed to save game: " + e.getMessage());
        }
    }

    /**
     * Load game state
     */
    public static SaveData loadGame() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            return null;
        }

        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(SAVE_FILE))) {
            return (SaveData) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load game: " + e.getMessage());
            return null;
        }
    }

    /**
     * Check nếu có save game
     */
    public static boolean hasSaveFile() {
        return new File(SAVE_FILE).exists();
    }

    /**
     * Xóa save file
     */
    public static void deleteSave() {
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
    }
}