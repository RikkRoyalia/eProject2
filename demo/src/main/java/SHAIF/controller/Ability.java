package SHAIF.controller;

/**
 * Ability - Một ability/power cụ thể
 */
class Ability {
    private String id;
    private String name;
    private String description;
    private String usage;

    public Ability(String id, String name, String description, String usage) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.usage = usage;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUsage() {
        return usage;
    }
}
