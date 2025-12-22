package SHAIF;

import SHAIF.controller.*;
import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private MenuScreen menuScreen;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Hiển thị menu trước
        showMenu();

        primaryStage.setTitle("Shape Shifter Platformer");
        primaryStage.show();
    }

    private void showMenu() {
        menuScreen = new MenuScreen(primaryStage);

        // Set callback khi nhấn Play
        menuScreen.setOnPlayCallback(this::startGame);

        menuScreen.show();
    }

    private void startGame() {
        // Khởi tạo View
        GameView gameView = new GameView();

        // Khởi tạo Models (sử dụng interfaces)
        Player player = new Player(100, 300);  // implements Movement
        Enemy enemy = new Enemy(600, 300);     // implements InteractiveObjects
        Bullet bullet = new Bullet();          // implements InteractiveObjects

        // QUAN TRỌNG: Chỉ add shape hiện tại của player vào view
        gameView.addNode(player.getCurrentShape());
        gameView.addNode(enemy.getShape());
        gameView.addNode(bullet.getShape());

        // Khởi tạo Controllers (truyền gameView vào KeyInput)
        DashController dashController = new DashController(player);
        KeyInput keyInput = new KeyInput(player, dashController, gameView);
        GameLoop gameLoop = new GameLoop(gameView, player, enemy, bullet, dashController);

        // Setup Scene
        Scene scene = new Scene(gameView.getRoot());
        keyInput.setupInput(scene);

        // Bắt đầu game loop
        gameLoop.start();

        // Chuyển sang game scene
        primaryStage.setScene(scene);
    }

    public static void main(String[] args) {
        launch(args);
    }
}