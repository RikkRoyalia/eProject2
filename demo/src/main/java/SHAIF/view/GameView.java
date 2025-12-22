package SHAIF.view;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GameView {
    private final Pane root;
    private Rectangle goal;

    public GameView() {
        root = new Pane();
        root.setPrefSize(800, 450);

        setupPlatforms();
        setupGoal();
    }

    private void setupPlatforms() {
        // Nền
        Rectangle ground = new Rectangle(800, 40);
        ground.setFill(Color.GRAY);
        ground.setY(410);

        // Chướng ngại vật
        Rectangle obstacle = new Rectangle(100, 20);
        obstacle.setFill(Color.DARKGRAY);
        obstacle.setX(300);
        obstacle.setY(360);

        // Hố đen
        Rectangle pit = new Rectangle(60, 200);
        pit.setFill(Color.BLACK);
        pit.setX(200);
        pit.setY(260);

        root.getChildren().addAll(ground, obstacle, pit);
    }

    private void setupGoal() {
        goal = new Rectangle(10, 80);
        goal.setFill(Color.GOLD);
        goal.setX(750);
        goal.setY(250);
        root.getChildren().add(goal);
    }

    public void addNode(javafx.scene.Node node) {
        root.getChildren().add(node);
    }

    public void removeNode(javafx.scene.Node node) {
        root.getChildren().remove(node);
    }

    public Pane getRoot() { return root; }
    public Rectangle getGoal() { return goal; }
}