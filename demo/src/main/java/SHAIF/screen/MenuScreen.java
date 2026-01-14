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
    private Runnable onPlayCallback;
    private Runnable onSettingsCallback;
    private Runnable onLevelSelectCallback;
    private Runnable onShopCallback;
    private Runnable onAchievementsCallback;

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

        // Level Select Button
        Button levelSelectButton = new Button("LEVEL SELECT");
        levelSelectButton.getStyleClass().add("menu-button");
        levelSelectButton.setOnAction(e -> onLevelSelectClicked());

        // Shop Button
        Button shopButton = new Button("SHOP");
        shopButton.getStyleClass().add("menu-button");
        shopButton.setOnAction(e -> onShopClicked());

        // Achievements Button
        Button achievementsButton = new Button("ACHIEVEMENTS");
        achievementsButton.getStyleClass().add("menu-button");
        achievementsButton.setOnAction(e -> onAchievementsClicked());

        // Settings Button
        Button settingsButton = new Button("SETTINGS");
        settingsButton.getStyleClass().add("menu-button");
        settingsButton.setOnAction(e -> onSettingsClicked());

        // Exit Button
        Button exitButton = new Button("EXIT");
        exitButton.getStyleClass().add("exit-button");
        exitButton.setOnAction(e -> System.exit(0));

        root.getChildren().addAll(title, subtitle, playButton, levelSelectButton,
                shopButton, achievementsButton, settingsButton, exitButton);

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
        if (onSettingsCallback != null) {
            onSettingsCallback.run();
        }
    }

    private void onLevelSelectClicked() {
        if (onLevelSelectCallback != null) {
            onLevelSelectCallback.run();
        }
    }

    private void onShopClicked() {
        if (onShopCallback != null) {
            onShopCallback.run();
        }
    }

    private void onAchievementsClicked() {
        if (onAchievementsCallback != null) {
            onAchievementsCallback.run();
        }
    }

    public void setOnPlayCallback(Runnable callback) {
        this.onPlayCallback = callback;
    }

    public void setOnSettingsCallback(Runnable callback) {
        this.onSettingsCallback = callback;
    }

    public void setOnLevelSelectCallback(Runnable callback) {
        this.onLevelSelectCallback = callback;
    }

    public void setOnShopCallback(Runnable callback) {
        this.onShopCallback = callback;
    }

    public void setOnAchievementsCallback(Runnable callback) {
        this.onAchievementsCallback = callback;
    }

    public Scene getScene() {
        return scene;
    }

    public void show() {
        stage.setScene(scene);
    }
}
