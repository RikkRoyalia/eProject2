package SHAIF.model;

/**
 * Data class để chứa thông tin item từ database
 */
public class ItemData {
    private double x;
    private double y;
    private String itemType;

    public ItemData(double x, double y, String itemType) {
        this.x = x;
        this.y = y;
        this.itemType = itemType;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public String getItemType() {
        return itemType;
    }

    /**
     * Convert string type từ DB sang ItemType enum
     */
    public ItemType getItemTypeEnum() {
        try {
            return ItemType.valueOf(itemType);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid item type: " + itemType);
            return ItemType.COIN; // default
        }
    }
}