package SHAIF.game;

import SHAIF.controller.Dash;
import SHAIF.controller.KeyInput;
import SHAIF.model.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Game extends Application {

    public static Player player;
    static Enemy enemy;
    static KeyInput input = new KeyInput();
    static Dash dash = new Dash();
    static Pane root;
    static List<Projectile> projectiles = new ArrayList<>();

    public static void spawnProjectile() {
        double mouseX = Game.input.mouseX;
        double mouseY = Game.input.mouseY;


        Projectile p = new Projectile(
                player.x, player.y,
                mouseX - player.x,
                mouseY - player.y
        );

        projectiles.add(p);
        root.getChildren().add(p.shape);
    }

    @Override
    public void start(Stage stage) {
        root = new Pane();
        Scene scene = new Scene(root, 900, 600, Color.BLACK);

        input.bind(scene);

        player = new Player(450, 300);
        root.getChildren().add(player.shape);

        enemy = new Enemy(200, 200);
        root.getChildren().add(enemy.shape);

        new GameLoop().start();

        stage.setTitle("Shape Shifter Game");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
