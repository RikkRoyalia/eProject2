package SHAIF.view;

import SHAIF.controller.GameStats;
import SHAIF.model.Player;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class GameHUD {
    private final Pane root;
    private final GameStats stats;
    private final Player player;
    private final Text scoreText;
    private final Text timeText;
    private final Text healthText;
    private final HBox healthBar;
    private final VBox powerUpIndicators;

    public GameHUD(GameStats stats, Player player) {
        this.stats = stats;
        this.player = player;
        this.root = new Pane();
        root.setPrefSize(1280, 720);
        root.setMouseTransparent(true); // Không chặn input

        // Score
        scoreText = new Text("Score: 0");
        scoreText.setFont(Font.font("Arial", 20));
        scoreText.setFill(Color.WHITE);
        scoreText.setX(20);
        scoreText.setY(30);

        // Time
        timeText = new Text("Time: 00:00");
        timeText.setFont(Font.font("Arial", 20));
        timeText.setFill(Color.WHITE);
        timeText.setX(20);
        timeText.setY(60);

        // Health
        healthText = new Text("Health:");
        healthText.setFont(Font.font("Arial", 16));
        healthText.setFill(Color.WHITE);
        healthText.setX(20);
        healthText.setY(100);

        // Health bar
        healthBar = new HBox(5);
        healthBar.setLayoutX(20);
        healthBar.setLayoutY(110);
        updateHealthBar();

        // Power-up indicators
        powerUpIndicators = new VBox(5);
        powerUpIndicators.setLayoutX(20);
        powerUpIndicators.setLayoutY(150);

        root.getChildren().addAll(scoreText, timeText, healthText, healthBar, powerUpIndicators);
    }

    public void update() {
        stats.update();
        scoreText.setText("Score: " + stats.getScore());
        timeText.setText("Time: " + stats.getFormattedTime());
        updateHealthBar();
        updatePowerUpIndicators();
    }

    private void updateHealthBar() {
        healthBar.getChildren().clear();
        int maxHits = player.getMaxHits();
        int currentHits = player.getHitCount();

        for (int i = 0; i < maxHits; i++) {
            Rectangle heart = new Rectangle(20, 20);
            if (i < maxHits - currentHits) {
                heart.setFill(Color.RED);
            } else {
                heart.setFill(Color.GRAY);
            }
            healthBar.getChildren().add(heart);
        }
    }

    private void updatePowerUpIndicators() {
        powerUpIndicators.getChildren().clear();

        if (player.hasShield()) {
            Text shieldText = new Text("🛡 Shield");
            shieldText.setFont(Font.font("Arial", 14));
            shieldText.setFill(Color.CYAN);
            powerUpIndicators.getChildren().add(shieldText);
        }

        if (player.hasSpeedBoost()) {
            Text speedText = new Text("⚡ Speed");
            speedText.setFont(Font.font("Arial", 14));
            speedText.setFill(Color.YELLOW);
            powerUpIndicators.getChildren().add(speedText);
        }

        if (player.hasDashBoost()) {
            Text dashText = new Text("💨 Dash Boost");
            dashText.setFont(Font.font("Arial", 14));
            dashText.setFill(Color.ORANGE);
            powerUpIndicators.getChildren().add(dashText);
        }

        if (player.hasDoubleJump()) {
            Text jumpText = new Text("🦘 Double Jump");
            jumpText.setFont(Font.font("Arial", 14));
            jumpText.setFill(Color.GREEN);
            powerUpIndicators.getChildren().add(jumpText);
        }
    }

    public Pane getRoot() {
        return root;
    }
}

