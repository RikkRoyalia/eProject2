package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;


public class Player extends InteractiveObjects {
    public enum Form {CIRCLE, SQUARE, TRIANGLE, STAR}
    public Form form = Form.CIRCLE;
    public Shape shape;
    public double x, y;
    public double speed = 5;
    public Player(double x, double y) {
        this.x = x;
        this.y = y;
        setForm(Form.CIRCLE);
    }

    public void setForm(Form newForm) {
        this.form = newForm;

        switch (newForm) {
            case CIRCLE:
                shape = new Circle(20, Color.CYAN);
                speed = 6;
                break;

            case SQUARE:
                shape = new Rectangle(40, 40);
                shape.setFill(Color.ORANGE);
                speed = 3;
                break;

            case TRIANGLE:
                Polygon tri = new Polygon();
                tri.getPoints().addAll(
                        0.0, -25.0,
                        20.0, 15.0,
                        -20.0, 15.0
                );
                tri.setFill(Color.LIGHTGREEN);
                shape = tri;
                speed = 5;
                break;

            case STAR:
                Polygon star = new Polygon(
                        0, -25,
                        7, -7,
                        25, -7,
                        10, 5,
                        15, 25,
                        0, 12,
                        -15, 25,
                        -10, 5,
                        -25, -7,
                        -7, -7
                );
                star.setFill(Color.YELLOW);
                shape = star;
                speed = 4;
                break;
        }

        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }

    public void updatePos() {
        shape.setTranslateX(x);
        shape.setTranslateY(y);
    }
}
