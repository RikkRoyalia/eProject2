package ShootingPlane;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

class Players {
    int x, y;
    long lastShot = 0;

    public Players(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void move(int dx) {
        x += dx;
        if (x < 20) x = 20;
        if (x > Game.WIDTH - 20) x = Game.WIDTH - 20;
    }

    public void render(GraphicsContext g) {
        g.setFill(Color.CYAN);

        double[] px = { x, x - 20, x + 20 };
        double[] py = { y, y + 40, y + 40 };

        g.fillPolygon(px, py, 3);
    }
}

