package ShootingPlane;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Game extends Application {

    public static final int WIDTH = 500;
    public static final int HEIGHT = 600;

    boolean left, right, shoot;

    Players player;
    ArrayList<Bullet> bullets = new ArrayList<>();
    ArrayList<Enemy> enemies = new ArrayList<>();

    Random rand = new Random();

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        player = new Players(WIDTH / 2, HEIGHT - 100);

        Scene scene = new Scene(new StackPane(canvas));

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) left = true;
            if (e.getCode() == KeyCode.RIGHT) right = true;
            if (e.getCode() == KeyCode.SPACE) shoot = true;
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT) left = false;
            if (e.getCode() == KeyCode.RIGHT) right = false;
            if (e.getCode() == KeyCode.SPACE) shoot = false;
        });

        stage.setTitle("Ban May Bay - JavaFX");
        stage.setScene(scene);
        stage.show();

        // Game Loop
        new AnimationTimer() {
            final long lastShotTime = 0;

            @Override
            public void handle(long now) {
                update(now);
                render(gc);
            }
        }.start();
    }

    private void update(long now) {
        // Player move
        if (left) player.move(-5);
        if (right) player.move(5);

        // Shoot
        if (shoot && now - player.lastShot > 250_000_000) { // 250ms
            bullets.add(new Bullet(player.x, player.y - 20));
            player.lastShot = now;
        }

        // Update bullets
        Iterator<Bullet> bIter = bullets.iterator();
        while (bIter.hasNext()) {
            Bullet b = bIter.next();
            b.update();
            if (b.y < 0) bIter.remove();
        }

        // Spawn enemies
        if (rand.nextInt(25) == 0) {
            enemies.add(new Enemy(rand.nextInt(WIDTH - 30), -40));
        }

        // Update enemies + collision
        Iterator<Enemy> eIter = enemies.iterator();
        while (eIter.hasNext()) {
            Enemy e = eIter.next();
            e.update();

            if (e.y > HEIGHT) eIter.remove();

            // Bullet collision
            for (Bullet b : bullets) {
                if (e.hitBox().intersects(b.hitBox())) {
                    eIter.remove();
                    bullets.remove(b);
                    break;
                }
            }
        }
    }

    private void render(GraphicsContext g) {
        g.clearRect(0, 0, WIDTH, HEIGHT);

        player.render(g);

        for (Bullet b : bullets) b.render(g);
        for (Enemy e : enemies) e.render(g);
    }

    public static void main(String[] args) {
        launch();
    }
}

