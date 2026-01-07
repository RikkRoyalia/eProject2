package SHAIF.controller;

import SHAIF.model.*;
import SHAIF.screen.MenuScreen;
import SHAIF.view.GameHUD;
import SHAIF.view.GameView;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Iterator;
import java.util.List;

/**
 * MetroidvaniaGameLoop - Extended GameLoop với:
 * - Room transitions
 * - Ability pickups
 * - Save points
 * - Minimap updates
 * - Persistent world state
 */
public class MetroidvaniaGameLoop extends GameLoop {

    private final Minimap minimap;
    private final WorldMap worldMap;
    private final AbilityManager abilityManager;
    private final Stage primaryStage;
    private final GameView gameView;
    private RoomTransitionDetector transitionDetector;

    // Transition state
    private boolean isTransitioning = false;
    private RoomTransition pendingTransition = null;

    // Ability pickups trong room hiện tại
    private List<AbilityPickup> abilityPickups;

    // Save points trong room hiện tại
    private List<SavePoint> savePoints;

    // Fade overlay cho transitions
    private Rectangle fadeOverlay;
    private Pane rootPane;

    public MetroidvaniaGameLoop(GameView gameView, Player player, Enemy enemy,
                                Bullet bullet, DashController dashController,
                                Stage primaryStage, MenuScreen menuScreen,
                                GameStats stats, GameHUD hud, ComboSystem comboSystem,
                                Minimap minimap, WorldMap worldMap,
                                AbilityManager abilityManager,
                                Pane rootPane) {
        super(gameView, player, enemy, bullet, dashController, primaryStage,
                menuScreen, stats, hud, comboSystem);

        this.minimap = minimap;
        this.worldMap = worldMap;
        this.abilityManager = abilityManager;
        this.primaryStage = primaryStage;
        this.gameView = gameView;
        this.rootPane = rootPane;

        setupFadeOverlay();
        loadRoomPickups();

        // Initialize transition detector
        this.transitionDetector = new RoomTransitionDetector(
                worldMap,
                gameView.getScreenWidth(),
                gameView.getScreenHeight()
        );
    }

    private void setupFadeOverlay() {
        fadeOverlay = new Rectangle(1280, 720);
        fadeOverlay.setFill(Color.BLACK);
        fadeOverlay.setOpacity(0);
        fadeOverlay.setMouseTransparent(true);
        rootPane.getChildren().add(fadeOverlay);
    }

    /**
     * Load ability pickups và save points cho room hiện tại
     */
    private void loadRoomPickups() {
        // TODO: Load từ database based on current room
        // For now, create empty lists
        abilityPickups = new java.util.ArrayList<>();
        savePoints = new java.util.ArrayList<>();

        // Example: Load from database
        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom != null) {
            // Load ability pickups for this room
            // abilityPickups = AbilityPickupDAO.loadForRoom(currentRoom.getMapId());

            // Load save points for this room
            // savePoints = SavePointDAO.loadForRoom(currentRoom.getMapId());
        }
    }

    @Override
    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused || isTransitioning) return;

                // Update game entities
                updateEntities();

                // Update HUD
                hud.update();

                // Update minimap
                minimap.render();

                // Check achievements
                checkAchievements();

                // Check collisions (includes room transitions)
                checkCollisions();
            }
        };
        timer.start();
    }

    private void updateEntities() {
        // Player
        player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
        player.update();
        dashController.update();

        // Enemy
        if (enemy != null && enemy.shouldShoot(System.nanoTime())) {
            bullet.shoot(enemy.getX(), enemy.getY() + 20);
        }
        if (enemy != null) {
            enemy.update();
        }

        bullet.update();

        // Items
        for (Item item : gameView.getItems()) {
            item.update();
        }
    }

    @Override
    protected void checkCollisions() {
        // Original collision checks
        checkPlayerEnemyCollision();
        checkBulletPlayerCollision();
//        checkGoalCollision();
        checkPitCollision();
        checkItemCollection();

        // NEW: Metroidvania-specific checks
        checkRoomTransitions();
        checkAbilityPickups();
        checkSavePoints();

        // Check death
        if (player.isDead()) {
            handleDeath();
        }
    }

    /**
     * Kiểm tra room transitions dựa trên player position ở edge
     */
    private void checkRoomTransitions() {
        if (isTransitioning) return;

        RoomTransition transition = transitionDetector.checkTransition(player);

        if (transition != null) {
            Room targetRoom = worldMap.getRooms().get(transition.getTargetRoomId());

            // Check nếu cần ability để vào
            if (targetRoom.getRequiredAbility() != null) {
                if (!abilityManager.hasAbility(targetRoom.getRequiredAbility())) {
                    showAbilityRequiredMessage(targetRoom.getRequiredAbility());

                    // Push player back
                    if (player.getX() < 50) {
                        player.setX(50);
                    } else if (player.getX() > gameView.getScreenWidth() - 50) {
                        player.setX(gameView.getScreenWidth() - 50);
                    }

                    return;
                }
            }

            // Start transition
            System.out.println("Transitioning to: " + targetRoom.getName());
            initiateRoomTransition(transition);
        }
    }

    /**
     * Bắt đầu transition sang room mới
     */
    private void initiateRoomTransition(RoomTransition transition) {
        isTransitioning = true;
        pendingTransition = transition;

        // Fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), fadeOverlay);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> completeRoomTransition());
        fadeOut.play();
    }

    /**
     * Hoàn thành transition - load room mới
     */
    private void completeRoomTransition() {
        // Update world state
        worldMap.transitionToRoom(
                pendingTransition.getTargetRoomId(),
                pendingTransition.getSpawnX(),
                pendingTransition.getSpawnY()
        );

        Room newRoom = worldMap.getCurrentRoom();
        newRoom.setDiscovered(true);

        // Move player
        player.setX(pendingTransition.getSpawnX());
        player.setY(pendingTransition.getSpawnY());

        // Reload room content
        reloadCurrentRoom();

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), fadeOverlay);
        fadeIn.setFromValue(1);
        fadeIn.setToValue(0);
        fadeIn.setOnFinished(e -> {
            isTransitioning = false;
            pendingTransition = null;
        });
        fadeIn.play();

        System.out.println("Entered: " + newRoom.getName());
    }

    /**
     * Reload room mới (load map, enemies, items từ database)
     */
    private void reloadCurrentRoom() {
        Room currentRoom = worldMap.getCurrentRoom();
        int mapId = currentRoom.getMapId();

        // Clear old room content
        rootPane.getChildren().clear();

        // Load new map
        GameView newGameView = new GameView(mapId);
        rootPane.getChildren().add(newGameView.getRoot());

        // Re-add player
        rootPane.getChildren().add(player.getCurrentShape());

        // Re-add HUD and minimap
        rootPane.getChildren().add(hud.getRoot());
        rootPane.getChildren().add(minimap.getCanvas());
        rootPane.getChildren().add(fadeOverlay);

        // Reload pickups
        loadRoomPickups();

        // TODO: Reload enemies for this room
        // This requires more complex enemy management
    }

    /**
     * Kiểm tra ability pickups
     */
    private void checkAbilityPickups() {
        Iterator<AbilityPickup> iterator = abilityPickups.iterator();
        while (iterator.hasNext()) {
            AbilityPickup pickup = iterator.next();

            if (pickup.isCollected()) continue;

            double distance = Math.sqrt(
                    Math.pow(player.getX() - pickup.getX(), 2) +
                            Math.pow(player.getY() - pickup.getY(), 2)
            );

            if (distance < 30) {
                pickup.collect();
                showAbilityUnlockedMessage(pickup.getAbilityId());
                iterator.remove();

                // Auto-save khi nhận ability mới
                autoSave();
            }
        }
    }

    /**
     * Kiểm tra save points
     */
    private void checkSavePoints() {
        for (SavePoint savePoint : savePoints) {
            double distance = Math.sqrt(
                    Math.pow(player.getX() - savePoint.getX(), 2) +
                            Math.pow(player.getY() - savePoint.getY(), 2)
            );

            if (distance < 40) {
                // Show prompt để save
                if (!savePoint.isActivated()) {
                    showSavePrompt(savePoint);
                }
            }
        }
    }

    /**
     * Auto-save game state
     */
    private void autoSave() {
        SaveData data = createSaveData();
        MetroidvaniaSaveSystem.saveGame(data);
        System.out.println("Game auto-saved");
    }

    /**
     * Tạo save data từ game state hiện tại
     */
    private SaveData createSaveData() {
        SaveData data = new SaveData();

        // Player state
        data.setCurrentRoomId(worldMap.getCurrentRoomId());
        data.setPlayerX(player.getX());
        data.setPlayerY(player.getY());
        data.setHealth(player.getMaxHits() - player.getHitCount());

        // Discovered rooms
        for (Room room : worldMap.getRooms().values()) {
            if (room.isDiscovered()) {
                data.addDiscoveredRoom(room.getId());
            }
        }

        // Unlocked abilities
        for (String ability : abilityManager.getUnlockedAbilities()) {
            data.addUnlockedAbility(ability);
        }

        // Stats
        data.setTotalPlayTime((int) stats.getPlayTime());
        data.setCompletionPercentage(abilityManager.getCompletionPercentage());

        return data;
    }

    /**
     * Handle player death - respawn at last save point
     */
    private void handleDeath() {
        stop();

        gameData.incrementDeaths();

        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("Death");
            alert.setHeaderText("You Died");
            alert.setContentText("Respawning at last save point...");
            alert.showAndWait();

            // Reload from last save
            respawnAtLastSave();
        });
    }

    /**
     * Respawn tại save point cuối cùng
     */
    private void respawnAtLastSave() {
        SaveData saveData = MetroidvaniaSaveSystem.loadGame();

        if (saveData != null && saveData.getLastSavePointRoom() != null) {
            // Respawn at save point
            worldMap.transitionToRoom(
                    saveData.getLastSavePointRoom(),
                    saveData.getLastSavePointX(),
                    saveData.getLastSavePointY()
            );

            player.setX(saveData.getLastSavePointX());
            player.setY(saveData.getLastSavePointY());

            // Reset health
            while (player.getHitCount() > 0) {
                player.heal();
            }

            reloadCurrentRoom();
            start(); // Restart game loop
        } else {
            // No save point, restart from beginning
            // TODO: Implement restart logic
        }
    }

    // ===== UI Messages =====

    private void showAbilityRequiredMessage(String abilityId) {
        Ability ability = abilityManager.getAllAbilities().get(abilityId);
        String message = "Requires: " + (ability != null ? ability.getName() : abilityId);

        // TODO: Show in-game message instead of alert
        System.out.println(message);
    }

    private void showAbilityUnlockedMessage(String abilityId) {
        Ability ability = abilityManager.getAllAbilities().get(abilityId);

        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.INFORMATION
            );
            alert.setTitle("New Ability!");
            alert.setHeaderText(ability != null ? ability.getName() : abilityId);
            alert.setContentText(
                    (ability != null ? ability.getDescription() : "") + "\n\n" +
                            (ability != null ? ability.getUsage() : "")
            );
            alert.showAndWait();
        });
    }

    private void showSavePrompt(SavePoint savePoint) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.CONFIRMATION
            );
            alert.setTitle("Save Point");
            alert.setHeaderText("Save your progress?");
            alert.setContentText("Press OK to save the game");

            alert.showAndWait().ifPresent(response -> {
                if (response == javafx.scene.control.ButtonType.OK) {
                    savePoint.interact();

                    // Update save data
                    SaveData data = createSaveData();
                    data.setLastSavePointRoom(savePoint.getRoomId());
                    data.setLastSavePointX(savePoint.getX());
                    data.setLastSavePointY(savePoint.getY());
                    MetroidvaniaSaveSystem.saveGame(data);
                }
            });
        });
    }

    // ===== Original GameLoop collision methods (preserved) =====

    private void checkPlayerEnemyCollision() {
        if (enemy == null) return;

        if (dashController.checkCollision(enemy) && enemy.isActive()) {
            enemy.defeat();
            stats.killEnemy();
            gameData.incrementEnemiesKilled();

            if (enemy.isBoss()) {
                achievementManager.checkAchievement("boss_slayer", 1);
            } else {
                achievementManager.progressAchievement("dash_king", 1);
            }

            dashController.stopDash();
        }
    }

    private void checkBulletPlayerCollision() {
        if (bullet.intersects(player.getCurrentShape()) && bullet.isActive()) {
            if (player.getCurrentForm() == FormType.SQUARE) {
                bullet.deactivate();
            } else {
                player.takeDamage();
                bullet.deactivate();
            }
        }
    }

//    private void checkGoalCollision() {
//        if (player.getCurrentShape().getBoundsInParent()
//                .intersects(gameView.getGoal().getBoundsInParent())) {
//            // In Metroidvania, goals might be bosses or specific objectives
//            // For now, we can treat it as room completion
//            System.out.println("Objective completed in this room!");
//        }
//    }

    private void checkPitCollision() {
        for (Rectangle pit : gameView.getPits()) {
            double px = player.getX();
            double py = player.getY();
            double ph = player.getHeight();

            if (px >= pit.getX() && px <= pit.getX() + pit.getWidth() &&
                    py + ph >= pit.getY()) {
                handleDeath();
                return;
            }
        }
    }

    private void checkItemCollection() {
        Iterator<Item> itemIterator = gameView.getItems().iterator();
        while (itemIterator.hasNext()) {
            Item item = itemIterator.next();
            if (item.isActive() && item.intersects(player.getCurrentShape())) {
                handleItemCollection(item);
                item.collect();
            }
        }
    }

    private void handleItemCollection(Item item) {
        stats.collectItem(item.getItemType());
        gameData.incrementItemsCollected();
        achievementManager.progressAchievement("collector", 1);

        switch (item.getItemType()) {
            case HEALTH:
                player.heal();
                break;
            case DASH_BOOST:
                player.activateDashBoost(15);
                break;
            case SHIELD:
                player.activateShield(10);
                break;
            case SPEED_BOOST:
                player.activateSpeedBoost(10);
                break;
            case DOUBLE_JUMP:
                player.activateDoubleJump();
                break;
            case COIN:
                UpgradeShop.getInstance().addCoins(10);
                gameData.addCoins(10);
                achievementManager.progressAchievement("coin_collector", 1);
                break;
        }
    }

    // ===== Getters for access =====

    public Player getPlayer() {
        return player;
    }

    protected GameView getGameView() {
        return gameView;
    }
}