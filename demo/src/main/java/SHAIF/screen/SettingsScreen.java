package SHAIF.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SettingsScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;

    public SettingsScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(15);
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #34495E;");

        // Title
        Text title = new Text("SETTINGS");
        title.setFont(Font.font("Arial", 36));
        title.setFill(Color.WHITE);

        // Volume Control
        Label volumeLabel = new Label("Volume");
        volumeLabel.setTextFill(Color.WHITE);
        volumeLabel.setFont(Font.font(16));

        Slider volumeSlider = new Slider(0, 100, 50);
        volumeSlider.setPrefWidth(300);
        volumeSlider.setShowTickLabels(true);

        // Fullscreen Option
        CheckBox fullscreenCheck = new CheckBox("Fullscreen");
        fullscreenCheck.setTextFill(Color.WHITE);
        fullscreenCheck.setFont(Font.font(16));

        // Music Option
        CheckBox musicCheck = new CheckBox("Music");
        musicCheck.setTextFill(Color.WHITE);
        musicCheck.setFont(Font.font(16));
        musicCheck.setSelected(true);

        // Back Button
        Button backButton = new Button("BACK");
        backButton.setPrefWidth(200);
        backButton.setPrefHeight(40);
        backButton.setStyle("-fx-background-color: #95A5A6; -fx-text-fill: white; -fx-font-size: 16px;");
        backButton.setOnAction(e -> onBackClicked());

        root.getChildren().addAll(
                title,
                volumeLabel,
                volumeSlider,
                fullscreenCheck,
                musicCheck,
                backButton
        );

        scene = new Scene(root, 800, 450);
    }

    private void onBackClicked() {
        if (onBackCallback != null) {
            onBackCallback.run();
        }
    }

    private Runnable onBackCallback;

    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.setScene(scene);
    }
}