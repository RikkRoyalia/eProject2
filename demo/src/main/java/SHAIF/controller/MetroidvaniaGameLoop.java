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
 * FIXED MetroidvaniaGameLoop
 * Key fixes:
 * 1. Properly reload room content
 * 2. Update player state after transition
 * 3. Handle GameView player reference
 */
public class MetroidvaniaGameLoop extends GameLoop {

    private final Minimap minimap;
    private final WorldMap worldMap;
    private final AbilityManager abilityManager;
    private final Stage primaryStage;
    private GameView gameView; // Changed from final
    private RoomTransitionDetector transitionDetector;

    // Transition state
    private boolean isTransitioning = false;
    private RoomTransition pendingTransition = null;

    // Room content
    private List<AbilityPickup> abilityPickups;
    private List<SavePoint> savePoints;

    // UI
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

        // Set player reference in GameView
        gameView.setPlayer(player);

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

    private void loadRoomPickups() {
        abilityPickups = new java.util.ArrayList<>();
        savePoints = new java.util.ArrayList<>();

        Room currentRoom = worldMap.getCurrentRoom();
        if (currentRoom != null) {
            // TODO: Load from database
        }
    }

    @Override
    public void start() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (isPaused || isTransitioning) return;

                updateEntities();
                hud.update();
                minimap.render();
                checkAchievements();
                checkCollisions();
            }
        };
        timer.start();
    }

    private void updateEntities() {
        player.applyGravityWithPlatforms(gameView.getPlatforms(), gameView.getGroundLevel());
        player.update();
        dashController.update();

        if (enemy != null && enemy.shouldShoot(System.nanoTime())) {
            bullet.shoot(enemy.getX(), enemy.getY() + 20);
        }
        if (enemy != null) {
            enemy.update();
        }

        bullet.update();

        for (Item item : gameView.getItems()) {
            item.update();
        }
    }

    @Override
    protected void checkCollisions() {
        checkPlayerEnemyCollision();
        checkBulletPlayerCollision();
        checkPitCollision();
        checkItemCollection();

        // Metroidvania-specific
        checkRoomTransitions();
        checkAbilityPickups();
        checkSavePoints();

        if (player.isDead()) {
            handleDeath();
        }
    }

    /**
     * FIX: Check room transitions
     */
    private void checkRoomTransitions() {
        if (isTransitioning) return;

        RoomTransition transition = transitionDetector.checkTransition(player);

        if (transition != null) {
            Room targetRoom = worldMap.getRooms().get(transition.getTargetRoomId());

            // Check required ability
            if (targetRoom.getRequiredAbility() != null) {
                if (!abilityManager.hasAbility(targetRoom.getRequiredAbility())) {
                    showAbilityRequiredMessage(targetRoom.getRequiredAbility());
                    pushPlayerBack();
                    return;
                }
            }

            System.out.println("🚪 Transitioning to: " + targetRoom.getName());
            initiateRoomTransition(transition);
        }
    }

    /**
     * FIX: Push player back from edge
     */
    private void pushPlayerBack() {
        double screenWidth = gameView.getScreenWidth();

        if (player.getX() < 50) {
            player.setX(50);
        } else if (player.getX() > screenWidth - 50) {
            player.setX(screenWidth - 50);
        }

        if (player.getY() < 50) {
            player.setY(50);
        }
    }

    /**
     * FIX: Initiate room transition with fade
     */
    private void initiateRoomTransition(RoomTransition transition) {
        isTransitioning = true;
        pendingTransition = transition;

        FadeTransition fadeOut = new FadeTransition(Duration.millis(500), fadeOverlay);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> completeRoomTransition());
        fadeOut.play();
    }

    /**
     * FIX: Complete room transition - properly reload everything
     */
    private void completeRoomTransition() {
        System.out.println("\n=== Completing Room Transition ===");

        // Update world state
        worldMap.transitionToRoom(
                pendingTransition.getTargetRoomId(),
                pendingTransition.getSpawnX(),
                pendingTransition.getSpawnY()
        );

        Room newRoom = worldMap.getCurrentRoom();
        newRoom.setDiscovered(true);

        System.out.println("Target room: " + newRoom.getName());
        System.out.println("Spawn at: (" + pendingTransition.getSpawnX() + ", " + pendingTransition.getSpawnY() + ")");

        // Move player BEFORE reloading room
        player.setX(pendingTransition.getSpawnX());
        player.setY(pendingTransition.getSpawnY());
        player.setVelY(0); // Stop falling
        player.setDashing(false); // Stop dashing

        System.out.println("Player moved to: (" + player.getX() + ", " + player.getY() + ")");

        // Reload room content
        reloadCurrentRoom();

        // Fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(500), fadeOverlay);
        fadeIn.setFromValue(1);
        fadeIn.setToValue(0);
        fadeIn.setOnFinished(e -> {
            isTransitioning = false;
            pendingTransition = null;
            System.out.println("Transition complete!\n");
        });
        fadeIn.play();

        System.out.println("Entered: " + newRoom.getName());
    }

    /**
     * FIX: Properly reload current room
     */
    private void reloadCurrentRoom() {
        System.out.println("\n--- Reloading Room ---");

        Room currentRoom = worldMap.getCurrentRoom();
        int mapId = currentRoom.getMapId();

        System.out.println("Loading map ID: " + mapId);

        // Clear old content (except HUD, minimap, fade overlay)
        rootPane.getChildren().clear();

        // Reload GameView with new map
        gameView.reloadMap(mapId);

        // CRITICAL: Set player reference in new GameView
        gameView.setPlayer(player);

        // Add GameView root
        rootPane.getChildren().add(gameView.getRoot());

        // Re-add player shape
        if (!rootPane.getChildren().contains(player.getCurrentShape())) {
            rootPane.getChildren().add(player.getCurrentShape());
        }

        // Re-add HUD
        if (!rootPane.getChildren().contains(hud.getRoot())) {
            rootPane.getChildren().add(hud.getRoot());
        }

        // Re-add minimap
        if (!rootPane.getChildren().contains(minimap.getCanvas())) {
            rootPane.getChildren().add(minimap.getCanvas());
        }

        // Re-add fade overlay (must be on top)
        if (!rootPane.getChildren().contains(fadeOverlay)) {
            rootPane.getChildren().add(fadeOverlay);
        }

        // Reload pickups
        loadRoomPickups();

        // Update transition detector
        transitionDetector = new RoomTransitionDetector(
                worldMap,
                gameView.getScreenWidth(),
                gameView.getScreenHeight()
        );

        System.out.println("✓ Room reloaded");
        System.out.println("  Scene children: " + rootPane.getChildren().size());
        System.out.println("  Platforms: " + gameView.getPlatforms().size());
        System.out.println("  Ground level: " + gameView.getGroundLevel());
        System.out.println("  Player at: (" + player.getX() + ", " + player.getY() + ")");
        System.out.println("----------------------\n");
    }

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
                autoSave();
            }
        }
    }

    private void checkSavePoints() {
        for (SavePoint savePoint : savePoints) {
            double distance = Math.sqrt(
                    Math.pow(player.getX() - savePoint.getX(), 2) +
                            Math.pow(player.getY() - savePoint.getY(), 2)
            );

            if (distance < 40 && !savePoint.isActivated()) {
                showSavePrompt(savePoint);
            }
        }
    }

    private void autoSave() {
        SaveData data = createSaveData();
        MetroidvaniaSaveSystem.saveGame(data);
        System.out.println("💾 Game auto-saved");
    }

    private SaveData createSaveData() {
        SaveData data = new SaveData();
        data.setCurrentRoomId(worldMap.getCurrentRoomId());
        data.setPlayerX(player.getX());
        data.setPlayerY(player.getY());
        data.setHealth(player.getMaxHits() - player.getHitCount());

        for (Room room : worldMap.getRooms().values()) {
            if (room.isDiscovered()) {
                data.addDiscoveredRoom(room.getId());
            }
        }

        for (String ability : abilityManager.getUnlockedAbilities()) {
            data.addUnlockedAbility(ability);
        }

        data.setTotalPlayTime((int) stats.getPlayTime());
        data.setCompletionPercentage(abilityManager.getCompletionPercentage());

        return data;
    }

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

            respawnAtLastSave();
        });
    }

    private void respawnAtLastSave() {
        SaveData saveData = MetroidvaniaSaveSystem.loadGame();

        if (saveData != null && saveData.getLastSavePointRoom() != null) {
            worldMap.transitionToRoom(
                    saveData.getLastSavePointRoom(),
                    saveData.getLastSavePointX(),
                    saveData.getLastSavePointY()
            );

            player.setX(saveData.getLastSavePointX());
            player.setY(saveData.getLastSavePointY());

            while (player.getHitCount() > 0) {
                player.heal();
            }

            reloadCurrentRoom();
            start();
        }
    }

    // UI Messages
    private void showAbilityRequiredMessage(String abilityId) {
        Ability ability = abilityManager.getAllAbilities().get(abilityId);
        String message = "🔒 Requires: " + (ability != null ? ability.getName() : abilityId);
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

                    SaveData data = createSaveData();
                    data.setLastSavePointRoom(savePoint.getRoomId());
                    data.setLastSavePointX(savePoint.getX());
                    data.setLastSavePointY(savePoint.getY());
                    MetroidvaniaSaveSystem.saveGame(data);
                }
            });
        });
    }

    // Original collision methods
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

    public Player getPlayer() {
        return player;
    }

    protected GameView getGameView() {
        return gameView;
    }
}