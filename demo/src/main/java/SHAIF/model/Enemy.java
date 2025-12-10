package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Enemy {
    public Circle shape = new Circle(20, Color.PINK);
    double x, y;

    public Enemy(double x, double y) {
        this.x = x;
        this.y = y;
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public void update(Player p) {
        double dx = p.x - x;
        double dy = p.y - y;

        double len = Math.sqrt(dx*dx + dy*dy);
        x += (dx / len) * 2;
        y += (dy / len) * 2;

        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }
}
