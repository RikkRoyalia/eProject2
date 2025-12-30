package SHAIF.screen;

import SHAIF.controller.UpgradeShop;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class ShopScreen {
    private final Stage stage;
    private final VBox root;
    private Scene scene;
    private Runnable onBackCallback;
    private final UpgradeShop shop;

    public ShopScreen(Stage stage) {
        this.stage = stage;
        this.root = new VBox(20);
        this.shop = UpgradeShop.getInstance();
        setupUI();
    }

    private void setupUI() {
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: #34495E;");

        // Title
        Text title = new Text("UPGRADE SHOP");
        title.setFont(Font.font("Arial", 36));
        title.setFill(Color.WHITE);

        // Coins display
        Text coinsText = new Text("Coins: " + shop.getCoins());
        coinsText.setFont(Font.font("Arial", 24));
        coinsText.setFill(Color.GOLD);

        // Upgrades
        VBox upgradesBox = new VBox(15);
        for (String upgradeId : shop.getAllUpgrades().keySet()) {
            UpgradeShop.Upgrade upgrade = shop.getUpgrade(upgradeId);
            HBox upgradeRow = createUpgradeRow(upgradeId, upgrade);
            upgradesBox.getChildren().add(upgradeRow);
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

        root.getChildren().addAll(title, coinsText, upgradesBox, backButton);
        scene = new Scene(root, 800, 600);
    }

    private HBox createUpgradeRow(String upgradeId, UpgradeShop.Upgrade upgrade) {
        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);

        Label nameLabel = new Label(upgrade.getName() + " (Lv " + upgrade.getLevel() + "/" + upgrade.getMaxLevel() + ")");
        nameLabel.setFont(Font.font("Arial", 16));
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setPrefWidth(200);

        Label costLabel = new Label("Cost: " + upgrade.getCost() + " coins");
        costLabel.setFont(Font.font("Arial", 14));
        costLabel.setTextFill(Color.YELLOW);
        costLabel.setPrefWidth(150);

        Button buyButton = new Button("BUY");
        buyButton.setPrefWidth(100);
        buyButton.setDisable(upgrade.getLevel() >= upgrade.getMaxLevel() || shop.getCoins() < upgrade.getCost());
        buyButton.setOnAction(e -> {
            if (shop.buyUpgrade(upgradeId)) {
                setupUI(); // Refresh UI
            }
        });

        row.getChildren().addAll(nameLabel, costLabel, buyButton);
        return row;
    }

    public void setOnBackCallback(Runnable callback) {
        this.onBackCallback = callback;
    }

    public void show() {
        stage.setScene(scene);
    }
}

