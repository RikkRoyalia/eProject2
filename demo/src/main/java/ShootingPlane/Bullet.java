package ShootingPlane;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.geometry.Rectangle2D;

class Bullet {
    int x, y;
    int r = 8;

    public Bullet(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void update() {
        y -= 10;
    }

    public void render(GraphicsContext g) {
        g.setFill(Color.YELLOW);
        g.fillOval(x - (double) r /2, y - (double) r /2, r, r);
    }

    public Rectangle2D hitBox() {
        return new Rectangle2D(x - (double) r /2, y - (double) r /2, r, r);
    }
}

