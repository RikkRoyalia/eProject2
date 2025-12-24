package SHAIF;

import SHAIF.controller.*;
import SHAIF.database.DatabaseConnection;
import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {

    private Stage primaryStage;
    private MenuScreen menuScreen;
    private int currentMapId = 1; // Default map

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Test database connection
        DatabaseConnection.getConnection();

        // Hiển thị menu trước
        showMenu();

        primaryStage.setTitle("Shape Shifter Platformer");
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void showMenu() {
        menuScreen = new MenuScreen(primaryStage);
        menuScreen.setOnPlayCallback(this::startGame);
        menuScreen.show();
    }

    private void startGame() {
        // Khởi tạo View từ database
        GameView gameView = new GameView(currentMapId); // Load map từ database

        // Khởi tạo Player
        Player player = new Player(750, 300);
        player.setGroundLevel(gameView.getGroundLevel());

        // Khởi tạo Enemies từ map data
        List<EnemyData> enemiesData = gameView.getEnemiesData();
        Enemy enemy;
        if (!enemiesData.isEmpty()) {
            EnemyData firstEnemy = enemiesData.get(0);
            enemy = new Enemy(firstEnemy.getX(), firstEnemy.getY());
        } else {
            // Fallback nếu không có enemy trong database
            enemy = new Enemy(600, 300);
        }

        Bullet bullet = new Bullet();

        // Add shapes vào view
        gameView.addNode(player.getCurrentShape());
        gameView.addNode(enemy.getShape());
        gameView.addNode(bullet.getShape());

        // Khởi tạo Controllers
        DashController dashController = new DashController(player, gameView);
        KeyInput keyInput = new KeyInput(player, dashController, gameView);
        GameLoop gameLoop = new GameLoop(gameView, player, enemy, bullet, dashController);

        // Setup Scene
        Scene scene = new Scene(gameView.getRoot());

        // Load CSS
        String css = getClass().getResource("/SHAIF/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        keyInput.setupInput(scene);

        // Bắt đầu game loop
        gameLoop.start();

        // Chuyển sang game scene
        primaryStage.setScene(scene);
    }

    /**
     * Phương thức để thay đổi map
     */
    public void loadMap(int mapId) {
        this.currentMapId = mapId;
        startGame();
    }

    @Override
    public void stop() {
        // Đóng database connection khi thoát
        DatabaseConnection.closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}