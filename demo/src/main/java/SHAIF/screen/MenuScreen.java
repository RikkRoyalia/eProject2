package SHAIF.screen;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MenuScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;

    public MenuScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox();
        setupUI();
    }

    private void setupUI() {
        root.getStyleClass().add("menu-root");

        // Title
        Text title = new Text("SHAPE SHIFTER");
        title.getStyleClass().add("menu-title");

        // Subtitle
        Text subtitle = new Text("Platformer Game");
        subtitle.getStyleClass().add("menu-subtitle");

        // Play Button
        Button playButton = new Button("PLAY");
        playButton.getStyleClass().add("menu-button");
        playButton.setOnAction(e -> onPlayClicked());

        // Settings Button
        Button settingsButton = new Button("SETTINGS");
        settingsButton.getStyleClass().add("menu-button");
        settingsButton.setOnAction(e -> onSettingsClicked());

        // Exit Button
        Button exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, subtitle, playButton, settingsButton, exitButton);

        scene = new Scene(root, 800, 450);

        // Load CSS
        String css = getClass().getResource("/SHAIF/styles.css").toExternalForm();
        scene.getStylesheets().add(css);
    }

    private void onPlayClicked() {
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