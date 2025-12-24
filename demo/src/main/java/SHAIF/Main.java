package SHAIF;

import SHAIF.controller.*;
import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameView;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.transform.Scale;
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

        // Set fullscreen khi chạy game
        primaryStage.setMaximized(true);

        primaryStage.show();
    }



    private void showMenu() {
        menuScreen = new MenuScreen(primaryStage);
        menuScreen.setOnPlayCallback(this::startGame);
        menuScreen.show();
    }



    private void startGame() {
        // Khởi tạo View
        GameView gameView = new GameView();

        Group gameRoot = new Group();
        gameRoot.getChildren().add(gameView.getRoot());

        // Khởi tạo Models
        Player player = new Player(100, 680);
        player.setGroundLevel(gameView.getGroundLevel());


        player.setGroundLevel(gameView.getGroundLevel()); // Set ground level từ GameView

        Enemy enemy = new Enemy(600, 300);
        Bullet bullet = new Bullet();

        // Add shapes vào view
        gameView.addNode(player.getCurrentShape());
        gameView.addNode(enemy.getShape());
        gameView.addNode(bullet.getShape());

        // Khởi tạo Controllers
        DashController dashController = new DashController(player, gameView);
        KeyInput keyInput = new KeyInput(player, dashController, gameView);
        GameLoop gameLoop = new GameLoop(gameView, player, enemy, bullet, dashController,
                primaryStage, menuScreen);

        // Setup Scene
        Scene scene = new Scene(gameRoot, 1280, 720);

        Scale scale = new Scale(1, 1);
        gameRoot.getTransforms().add(scale);

        scene.widthProperty().addListener((obs, oldVal, newVal) -> {
            scale.setX(newVal.doubleValue() / 1280);
        });

        scene.heightProperty().addListener((obs, oldVal, newVal) -> {
            scale.setY(newVal.doubleValue() / 720);
        });


        // Load CSS cho game scene
        String css = getClass().getResource("/SHAIF/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

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