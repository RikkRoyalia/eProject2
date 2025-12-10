package SHAIF;

import javafx.animation.AnimationTimer;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.animation.Interpolator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

public class ShapeShifterGame extends Application {

    enum Form {SQUARE, CIRCLE, TRIANGLE}

    Form currentForm = Form.SQUARE;

    Pane root;
    Shape player;
    double playerX = 100, playerY = 300;
    double velY = 0;
    boolean onGround = false;
    boolean movingLeft = false, movingRight = false;

    Rectangle squareForm = new Rectangle(30, 30);
    Circle circleForm = new Circle(0,15,15);
    Polygon triangleForm = new Polygon(
            0.0, 0.0,
            0.0, 30.0,
            30.0, 15.0
    );

    // Enemy + bullet
    Rectangle enemy = new Rectangle(30, 40);
    boolean bulletAlive = false;
    Circle bullet = new Circle(5);

    // Goal
    Rectangle goal = new Rectangle(10, 80);

    @Override
    public void start(Stage stage) {
        root = new Pane();
        root.setPrefSize(800, 450);

        squareForm.setFill(Color.DARKBLUE);
        circleForm.setFill(Color.DARKRED);
        triangleForm.setFill(Color.DARKGREEN);

        enemy.setFill(Color.PURPLE);
        enemy.setX(600);
        enemy.setY(300);

        bullet.setFill(Color.BLACK);
        bullet.setVisible(false);

        goal.setFill(Color.GOLD);
        goal.setX(750);
        goal.setY(250);

        // Platforms
        Rectangle ground = new Rectangle(800, 40);
        ground.setFill(Color.GRAY);
        ground.setY(410);

        Rectangle obstacle = new Rectangle(100, 20);
        obstacle.setFill(Color.DARKGRAY);
        obstacle.setX(300);
        obstacle.setY(360);

        Rectangle pit = new Rectangle(60, 200);
        pit.setFill(Color.BLACK);
        pit.setX(200);
        pit.setY(260);

        root.getChildren().addAll(ground, obstacle, pit, enemy, bullet, goal);

        switchToForm(Form.CIRCLE);

        Scene scene = getScene();

        setupGameLoop();

        stage.setTitle("Shape Shifter Platformer");
        stage.setScene(scene);
        stage.show();
    }

    @NotNull
    private Scene getScene() {
        Scene scene = new Scene(root);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) movingLeft = true;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) movingRight = true;
            if ((e.getCode() == KeyCode.UP && onGround) || (e.getCode() == KeyCode.W && onGround)) velY = -12;

            if (e.getCode() == KeyCode.U) switchToForm(Form.CIRCLE);
            if (e.getCode() == KeyCode.I) {
                switchToForm(Form.TRIANGLE);
                dashAttack();
            }
            if (e.getCode() == KeyCode.O) switchToForm(Form.SQUARE);
        });

        scene.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.LEFT || e.getCode() == KeyCode.A) movingLeft = false;
            if (e.getCode() == KeyCode.RIGHT || e.getCode() == KeyCode.D) movingRight = false;
        });
        return scene;
    }

    void setupGameLoop() {
        new AnimationTimer() {
            long lastShoot = 0;

            @Override
            public void handle(long now) {
                applyGravity();
                handleMovement();
                checkCollisions();

                // Enemy shoots every ~1.5s
                if (now - lastShoot > 1_500_000_000L) {
                    shootBullet();
                    lastShoot = now;
                }

                updateBullet();
            }
        }.start();
    }

    void switchToForm(Form form) {
        root.getChildren().remove(player);

        currentForm = form;
        switch (form) {
            case SQUARE:
                player = squareForm;
                break;
            case CIRCLE:
                player = circleForm;
                break;
            case TRIANGLE:
                player = triangleForm;
                break;
        }

        player.setTranslateX(playerX);
        player.setTranslateY(playerY);

        root.getChildren().add(player);
    }

    void dashAttack() {
        // Animation nhún trước
        ScaleTransition compress = new ScaleTransition(Duration.millis(70), player);
        compress.setToY(0.85);

        // Dash move
        TranslateTransition dashMove = new TranslateTransition(Duration.millis(120), player);
        dashMove.setByX(40);
        dashMove.setInterpolator(Interpolator.EASE_OUT);

        // Bật lại form bình thường
        ScaleTransition relax = new ScaleTransition(Duration.millis(70), player);
        relax.setToY(1.0);

        dashMove.setOnFinished(e -> {
            // cập nhật playerX để game logic biết ta đã dash xong
            playerX += 40;

            // kiểm tra va chạm sau dash
            if (player.getBoundsInParent().intersects(enemy.getBoundsInParent())) {
                enemy.setFill(Color.PINK);
                enemy.setDisable(true);
            }
        });

        SequentialTransition dashAnim = new SequentialTransition(
                compress,
                dashMove,
                relax
        );

        dashAnim.play();
    }

    void applyGravity() {
        velY += 0.5;
        playerY += velY;

        if (playerY > 380) {
            playerY = 380;
            velY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
    }

    void handleMovement() {
        if (currentForm == Form.CIRCLE) {
            if (movingLeft) playerX -= 3;
            if (movingRight) playerX += 3;
        }

        player.setTranslateX(playerX);
        player.setTranslateY(playerY);
    }

    void checkCollisions() {
        // Bullet hitting player
        if (bulletAlive && player.getBoundsInParent().intersects(bullet.getBoundsInParent())) {
            if (currentForm == Form.SQUARE) {
                // Square blocks damage
                bulletAlive = false;
                bullet.setVisible(false);
            } else {
                player.setFill(Color.RED);
            }
        }

        // Reaching goal
        if (player.getBoundsInParent().intersects(goal.getBoundsInParent())) {
            System.out.println("Bạn đã hoàn thành màn!");
            System.exit(0);
        }
    }

    void shootBullet() {
        if (enemy.isDisable()) return;

        bulletAlive = true;
        bullet.setVisible(true);
        bullet.setCenterX(enemy.getX());
        bullet.setCenterY(enemy.getY() + 20);
    }

    void updateBullet() {
        if (!bulletAlive) return;

        bullet.setCenterX(bullet.getCenterX() - 4);

        if (bullet.getCenterX() < 0) {
            bulletAlive = false;
            bullet.setVisible(false);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

