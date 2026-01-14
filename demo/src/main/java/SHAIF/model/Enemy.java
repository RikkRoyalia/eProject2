package SHAIF.model;

import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

public class Enemy implements InteractiveObjects {
    private final Rectangle shape;
    private boolean active;
    private long lastShootTime;
    private final long shootInterval = 1_500_000_000L; // 1.5 giây
    private final EnemyType enemyType;

    // Movement for Patroller
    private double startX;
    private double endX;
    private double speed = 1;
    private boolean movingRight = true;

    // Jumping for Jumper
    private double startY;
    private double jumpHeight = 50;
    private double velY = 0;
    private boolean onGround = true;
    private final double gravity = 0.3;

    // Chasing for Chaser
    private Player targetPlayer;
    private double chaseSpeed = 2;
    private double chaseRange = 200;

    // Boss
    private int health = 1;
    private boolean isBoss = false;

    public Enemy(double x, double y) {
        this(x, y, EnemyType.SHOOTER);
    }

    public Enemy(double x, double y, EnemyType type) {
        this.enemyType = type;

        if (type == EnemyType.BOSS) {
            shape = new Rectangle(60, 80);
            health = 3;
            isBoss = true;
        } else {
            shape = new Rectangle(30, 40);
        }

        shape.getStyleClass().add("enemy-" + type.name().toLowerCase());
        shape.setX(x);
        shape.setY(y);
        active = true;
        lastShootTime = 0;

        startX = x;
        startY = y;

        // Setup cho Patroller
        if (type == EnemyType.PATROLLER) {
            endX = x + 100; // Di chuyển 100 pixels
        }
    }

    public void setTargetPlayer(Player player) {
        this.targetPlayer = player;
    }

    // ===== InteractiveObjects Interface Implementation =====

    @Override
    public void activate() {
        active = true;
        shape.setDisable(false);
        shape.getStyleClass().clear();
        shape.getStyleClass().add("enemy-" + enemyType.name().toLowerCase());
    }

    @Override
    public void deactivate() {
        active = false;
        shape.setDisable(true);
        shape.getStyleClass().clear();
        shape.getStyleClass().add("enemy-defeated");
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public Shape getShape() {
        return shape;
    }

    @Override
    public boolean intersects(Shape other) {
        return shape.getBoundsInParent().intersects(other.getBoundsInParent());
    }

    @Override
    public void update() {
        if (!active) return;

        switch (enemyType) {
            case PATROLLER:
                updatePatroller();
                break;
            case JUMPER:
                updateJumper();
                break;
            case CHASER:
                updateChaser();
                break;
            case BOSS:
            case SHOOTER:
            default:
                // Shooter và Boss không di chuyển
                break;
        }
    }

    private void updatePatroller() {
        double currentX = shape.getX();

        if (movingRight) {
            currentX += speed;
            if (currentX >= endX) {
                currentX = endX;
                movingRight = false;
            }
        } else {
            currentX -= speed;
            if (currentX <= startX) {
                currentX = startX;
                movingRight = true;
            }
        }

        shape.setX(currentX);
    }

    private void updateJumper() {
        if (onGround) {
            velY = -8; // Jump force
            onGround = false;
        }

        velY += gravity;
        double newY = shape.getY() + velY;

        if (newY >= startY) {
            newY = startY;
            velY = 0;
            onGround = true;
        }

        shape.setY(newY);
    }

    private void updateChaser() {
        if (targetPlayer == null) return;

        double enemyX = shape.getX();
        double playerX = targetPlayer.getX();
        double distance = Math.abs(playerX - enemyX);

        if (distance <= chaseRange) {
            if (playerX < enemyX) {
                shape.setX(enemyX - chaseSpeed);
            } else if (playerX > enemyX) {
                shape.setX(enemyX + chaseSpeed);
            }
        }
    }

    @Override
    public String getType() {
        return "ENEMY_" + enemyType.name();
    }

    // ===== Enemy specific methods =====

    public boolean shouldShoot(long currentTime) {
        if (!active) return false;

        // Boss bắn nhanh hơn
        long interval = isBoss ? shootInterval / 2 : shootInterval;

        // Jumper chỉ bắn khi ở trên không
        if (enemyType == EnemyType.JUMPER && onGround) {
            return false;
        }

        // Patroller và Chaser không bắn
        if (enemyType == EnemyType.PATROLLER || enemyType == EnemyType.CHASER) {
            return false;
        }

        if (currentTime - lastShootTime > interval) {
            lastShootTime = currentTime;
            return true;
        }
        return false;
    }

    public void defeat() {
        if (isBoss) {
            health--;
            if (health <= 0) {
                deactivate();
            }
        } else {
            deactivate();
        }
    }

    public EnemyType getEnemyType() {
        return enemyType;
    }

    public boolean isBoss() {
        return isBoss;
    }

    public int getHealth() {
        return health;
    }

    public double getX() {
        return shape.getX();
    }

    public double getY() {
        return shape.getY();
    }
}
