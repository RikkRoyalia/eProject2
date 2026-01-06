package SHAIF.controller;

/**
 * AbilityPickup - Item trong game để nhận ability
 */
public class AbilityPickup {
    private String abilityId;
    private double x, y;
    private boolean collected;

    public AbilityPickup(String abilityId, double x, double y) {
        this.abilityId = abilityId;
        this.x = x;
        this.y = y;
        this.collected = false;
    }

    public void collect() {
        if (!collected) {
            collected = true;
            AbilityManager.getInstance().unlockAbility(abilityId);
        }
    }

    // Getters
    public String getAbilityId() { return abilityId; }
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isCollected() { return collected; }
}
