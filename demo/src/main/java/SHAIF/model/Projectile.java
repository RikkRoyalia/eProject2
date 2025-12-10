package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class Projectile {
    public Circle shape = new Circle(5, Color.RED);
    double x, y, vx, vy;

    public Projectile(double x, double y, double dirX, double dirY) {
        this.x = x;
        this.y = y;

        double len = Math.sqrt(dirX * dirX + dirY * dirY);
        vx = (dirX / len) * 12;
        vy = (dirY / len) * 12;

        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public void update() {
        x += vx;
        y += vy;
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }
}
