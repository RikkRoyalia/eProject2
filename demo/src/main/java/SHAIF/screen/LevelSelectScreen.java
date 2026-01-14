package SHAIF.screen;

import SHAIF.controller.LevelManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;
    private Runnable onBackCallback;
    private Runnable onLevelSelectedCallback;
    private final List<Button> levelButtons;

    public LevelSelectScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(20);
        this.levelButtons = new ArrayList<>();
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2C3E50;");

        // Title
        Text title = new Text("SELECT LEVEL");
        title.setFont(Font.font("Arial", 36));
        title.setFill(Color.WHITE);

        // Level buttons
        GridPane levelGrid = new GridPane();
        levelGrid.setAlignment(Pos.CENTER);
        levelGrid.setHgap(20);
        levelGrid.setVgap(20);

        LevelManager levelManager = LevelManager.getInstance();
        for (int i = 1; i <= levelManager.getMaxLevels(); i++) {
            final int level = i;
            Button levelButton = new Button("LEVEL " + level);
            levelButton.setPrefWidth(150);
            levelButton.setPrefHeight(80);
            
            if (levelManager.isUnlocked(level)) {
                levelButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 18px;");
                levelButton.setOnAction(e -> {
                    levelManager.setCurrentLevel(level);
                    if (onLevelSelectedCallback != null) {
                        onLevelSelectedCallback.run();
                    }
                });
            } else {
                levelButton.setStyle("-fx-background-color: #7F8C8D; -fx-text-fill: gray; -fx-font-size: 18px;");
                levelButton.setDisable(true);
            }
            
            levelButtons.add(levelButton);
            levelGrid.add(levelButton, (i - 1) % 3, (i - 1) / 3);
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

        root.getChildren().addAll(title, levelGrid, backButton);
        scene = new Scene(root, 800, 600);
    }

    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }

    public void setOnLevelSelectedCallback(Runnable callback) {
        this.onLevelSelectedCallback = callback;
    }

    public void refresh() {
        levelButtons.clear();
        setupUI();
    }

    public void show() {
        stage.setScene(scene);
    }
}

