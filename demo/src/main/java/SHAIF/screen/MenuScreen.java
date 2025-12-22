package SHAIF.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;

    public MenuScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(20);
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #2C3E50;");

        // Title
        Text title = new Text("SHAPE SHIFTER");
        title.setFont(Font.font("Arial", 48));
        title.setFill(Color.WHITE);

        // Subtitle
        Text subtitle = new Text("Platformer Game");
        subtitle.setFont(Font.font("Arial", 20));
        subtitle.setFill(Color.LIGHTGRAY);

        // Play Button
        Button playButton = new Button("PLAY");
        styleButton(playButton);
        playButton.setOnAction(e -> onPlayClicked());

        // Settings Button
        Button settingsButton = new Button("SETTINGS");
        styleButton(settingsButton);
        settingsButton.setOnAction(e -> onSettingsClicked());

        // Exit Button (bonus)
        Button exitButton = new Button("EXIT");
        styleButton(exitButton);
        exitButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white;");
        exitButton.setOnMouseEntered(e ->
                exitButton.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-font-size: 18px;")
        );
        exitButton.setOnMouseExited(e ->
                exitButton.setStyle("-fx-background-color: #C0392B; -fx-text-fill: white; -fx-font-size: 16px;")
        );
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, subtitle, playButton, settingsButton, exitButton);

        scene = new Scene(root, 800, 450);
    }

    private void styleButton(Button button) {
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");

        // Hover effect
        button.setOnMouseEntered(e ->
                button.setStyle("-fx-background-color: #5DADE2; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;")
        );
        button.setOnMouseExited(e ->
                button.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;")
        );
    }

    private void onPlayClicked() {
        // Callback sẽ được set từ Main.java
        if (onPlayCallback != null) {
            onPlayCallback.run();
        }
    }

    private void onSettingsClicked() {
        System.out.println("Settings clicked - Coming soon!");
        // TODO: Implement settings screen
    }

    // Callback cho nút Play
    private Runnable onPlayCallback;

    public void setOnPlayCallback(Runnable callback) {
        this.onPlayCallback = callback;
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.setScene(scene);
    }
}