package SHAIF.view;

import SHAIF.model.Player;
import javafx.scene.Group;
import javafx.scene.transform.Translate;

public class Camera {
    private final Group target;
    private final double viewWidth;
    private final double viewHeight;
    private final double worldWidth;
    private final double worldHeight;
    private final Translate translate;

    public Camera(Group target, double viewWidth, double viewHeight, 
                  double worldWidth, double worldHeight) {
        this.target = target;
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
        this.worldWidth = worldWidth;
        this.worldHeight = worldHeight;
        this.translate = new Translate();
        target.getTransforms().add(translate);
    }

    public void update(Player player) {
        double playerX = player.getX();
        double playerY = player.getY();

        // Camera follow player với smooth movement
        double targetX = -playerX + viewWidth / 2;
        double targetY = -playerY + viewHeight / 2;

        // Giới hạn camera trong world bounds
        targetX = Math.max(Math.min(targetX, 0), -(worldWidth - viewWidth));
        targetY = Math.max(Math.min(targetY, 0), -(worldHeight - viewHeight));

        // Smooth camera movement (lerp)
        double currentX = translate.getX();
        double currentY = translate.getY();
        translate.setX(currentX + (targetX - currentX) * 0.1);
        translate.setY(currentY + (targetY - currentY) * 0.1);
    }
}

