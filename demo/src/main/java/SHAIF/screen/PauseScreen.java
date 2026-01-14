package SHAIF.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class PauseScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;
    private Runnable onResumeCallback;
    private Runnable onRestartCallback;
    private Runnable onQuitCallback;

    public PauseScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(20);
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(0, 0, 0, 0.8);");

        // Title
        Text title = new Text("PAUSED");
        title.setFont(Font.font("Arial", 48));
        title.setFill(Color.WHITE);

        // Resume Button
        Button resumeButton = new Button("RESUME");
        resumeButton.setPrefWidth(200);
        resumeButton.setPrefHeight(50);
        resumeButton.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px;");
        resumeButton.setOnAction(e -> {
            if (onResumeCallback != null) {
                onResumeCallback.run();
            }
        });

        // Restart Button
        Button restartButton = new Button("RESTART");
        restartButton.setPrefWidth(200);
        restartButton.setPrefHeight(50);
        restartButton.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-font-size: 16px;");
        restartButton.setOnAction(e -> {
            if (onRestartCallback != null) {
                onRestartCallback.run();
            }
        });

        // Quit Button
        Button quitButton = new Button("QUIT TO MENU");
        quitButton.setPrefWidth(200);
        quitButton.setPrefHeight(50);
        quitButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 16px;");
        quitButton.setOnAction(e -> {
            if (onQuitCallback != null) {
                onQuitCallback.run();
            }
        });

        root.getChildren().addAll(title, resumeButton, restartButton, quitButton);
        scene = new Scene(root, 1280, 720);
    }

    public void setOnResumeCallback(Runnable callback) {
        this.onResumeCallback = callback;
    }

    public void setOnRestartCallback(Runnable callback) {
        this.onRestartCallback = callback;
    }

    public void setOnQuitCallback(Runnable callback) {
        this.onQuitCallback = callback;
    }

    public void show() {
        stage.setScene(scene);
    }
}

