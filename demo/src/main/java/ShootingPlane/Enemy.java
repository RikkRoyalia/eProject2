package ShootingPlane;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

class Enemy {
    int x, y;
    int size = 30;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y += 4;
    }

    public void render(GraphicsContext g) {
        g.setFill(Color.RED);
        g.fillRect(x, y, size, size);
    }

    public Rectangle2D hitBox() {
        return new Rectangle2D(x, y, size, size);
    }
}

