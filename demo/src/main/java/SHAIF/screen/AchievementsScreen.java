package SHAIF.screen;

import SHAIF.controller.AchievementManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class AchievementsScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;
    private Runnable onBackCallback;
    private final AchievementManager achievementManager;

    public AchievementsScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(20);
        this.achievementManager = AchievementManager.getInstance();
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2C3E50;");

        // Title
        Text title = new Text("ACHIEVEMENTS");
        title.setFont(Font.font("Arial", 36));
        title.setFill(Color.WHITE);

        // Achievements list
        VBox achievementsBox = new VBox(10);
        Map<String, String> achievementNames = getAchievementNames();
        
        for (String achievementId : achievementNames.keySet()) {
            Label achievementLabel = createAchievementLabel(achievementId, achievementNames.get(achievementId));
            achievementsBox.getChildren().add(achievementLabel);
        }

        // Back Button
        Button backButton = new Button("BACK");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(50);
        backButton.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 16px;");
        backButton.setOnAction(e -> {
            if (onBackCallback != null) {
                onBackCallback.run();
            }
        });

        root.getChildren().addAll(title, achievementsBox, backButton);
        scene = new Scene(root, 800, 600);
    }

    private Map<String, String> getAchievementNames() {
        Map<String, String> names = new HashMap<>();
        names.put("first_steps", "First Steps - Complete Level 1");
        names.put("shape_master", "Shape Master - Change shape 100 times");
        names.put("dash_king", "Dash King - Defeat 10 enemies with dash");
        names.put("perfect_run", "Perfect Run - Complete level without taking damage");
        names.put("speed_demon", "Speed Demon - Complete level in under 60 seconds");
        names.put("collector", "Collector - Collect 20 items");
        names.put("survivor", "Survivor - Survive for 5 minutes");
        names.put("coin_collector", "Coin Collector - Collect 50 coins");
        names.put("level_master", "Level Master - Complete all 5 levels");
        names.put("boss_slayer", "Boss Slayer - Defeat the boss");
        return names;
    }

    private Label createAchievementLabel(String achievementId, String name) {
        Label label = new Label();
        label.setFont(Font.font("Arial", 14));
        label.setPrefWidth(600);
        
        boolean unlocked = achievementManager.isUnlocked(achievementId);
        int progress = achievementManager.getProgress(achievementId);
        
        if (unlocked) {
            label.setText("✓ " + name);
            label.setTextFill(Color.GOLD);
        } else {
            label.setText("○ " + name + " (Progress: " + progress + ")");
            label.setTextFill(Color.GRAY);
        }
        
        return label;
    }

    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }

    public void show() {
        stage.setScene(scene);
    }
}

