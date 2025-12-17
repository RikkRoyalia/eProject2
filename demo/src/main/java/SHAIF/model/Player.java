package SHAIF.model;

import javafx.scene.paint.Color;
import javafx.scene.shape.*;

public class Player {

    private Shape currentShape;
    private double x = 100, y = 300;
    private double velY = 0;
    private boolean onGround = false;

    private final Rectangle square = new Rectangle(30, 30);
    private final Circle circle = new Circle(15);
    private final Polygon triangle = new Polygon(
            0.0, 0.0,
            0.0, 30.0,
            30.0, 15.0
    );

    private FormType currentForm = FormType.SQUARE;

    public Player() {
        square.setFill(Color.DARKBLUE);
        circle.setFill(Color.DARKRED);
        triangle.setFill(Color.DARKGREEN);
        switchForm(FormType.CIRCLE);
    }

    public void switchForm(FormType form) {
        currentForm = form;
        switch (form) {
            case SQUARE : {
                currentShape = square;
                break;
            }
            case CIRCLE : {currentShape = circle; break;}
            case TRIANGLE : {currentShape = triangle;}
        }
        updatePosition();
    }

    public void applyGravity() {
        velY += 0.5;
        y += velY;

        if (y > 380) {
            y = 380;
            velY = 0;
            onGround = true;
        } else {
            onGround = false;
        }
        updatePosition();
    }

    public void move(double dx) {
        x += dx;
        updatePosition();
    }

    public void jump() {
        if (onGround) velY = -12;
    }

    private void updatePosition() {
        currentShape.setTranslateX(x);
        currentShape.setTranslateY(y);
    }

    public Shape getShape() {
        return currentShape;
    }

    public FormType getCurrentForm() {
        return currentForm;
    }
}
