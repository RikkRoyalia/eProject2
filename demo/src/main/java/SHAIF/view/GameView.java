package SHAIF.view;

import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import SHAIF.model.*;
import SHAIF.controller.*;

public class GameView {

    public GameView(Stage stage) {

        Pane root = new Pane();
        root.setPrefSize(800, 450);

        Player player = new Player();
        Enemy enemy = new Enemy();
        Bullet bullet = new Bullet();

        root.getChildren().addAll(
                player.getShape(),
                enemy.getBody(),
                bullet.getShape()
        );

        Scene scene = new Scene(root);
        DashController dash = new DashController(player, enemy);
        KeyInput input = new KeyInput(scene, player, dash);

        new GameLoop(player, enemy, bullet, input).start();

        stage.setScene(scene);
        stage.setTitle("Shape Shifter Platformer");
        stage.show();
    }
}
