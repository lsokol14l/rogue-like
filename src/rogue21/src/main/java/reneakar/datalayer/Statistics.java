package reneakar.datalayer;

import reneakar.model.entities.items.Treasure;

public class Statistics {
    private int treasuresCollected;
    private int levelReached;
    private int enemiesDefeated;
    private int foodEaten;
    private int elixirsDrunk;
    private int scrollsRead;
    private int hitsLanded;      // количество нанесённых ударов
    private int hitsMissed;      // количество промахов игрока
    private int hitsReceived;    // количество пропущенных ударов (от врагов)
    private int cellsWalked;
    private long timestamp;

    // Характеристики игрока на момент окончания сессии
    private int finalStrength;
    private int finalAgility;
    private int finalMaxHealth;

    public Statistics() {
        this.timestamp = System.currentTimeMillis();
        this.levelReached = 1;
    }

    // === Геттеры и сеттеры ===

    public int getTreasuresCollected() { return treasuresCollected; }
    public void setTreasuresCollected(int treasuresCollected) { this.treasuresCollected = treasuresCollected; }

    public int getLevelReached() { return levelReached; }
    public void setLevelReached(int levelReached) { this.levelReached = levelReached; }

    public int getEnemiesDefeated() { return enemiesDefeated; }
    public void setEnemiesDefeated(int enemiesDefeated) { this.enemiesDefeated = enemiesDefeated; }

    public int getFoodEaten() { return foodEaten; }
    public void setFoodEaten(int foodEaten) { this.foodEaten = foodEaten; }

    public int getElixirsDrunk() { return elixirsDrunk; }
    public void setElixirsDrunk(int elixirsDrunk) { this.elixirsDrunk = elixirsDrunk; }

    public int getScrollsRead() { return scrollsRead; }
    public void setScrollsRead(int scrollsRead) { this.scrollsRead = scrollsRead; }

    public int getHitsLanded() { return hitsLanded; }
    public void setHitsLanded(int hitsLanded) { this.hitsLanded = hitsLanded; }

    public int getHitsMissed() { return hitsMissed; }
    public void setHitsMissed(int hitsMissed) { this.hitsMissed = hitsMissed; }

    public int getHitsReceived() { return hitsReceived; }
    public void setHitsReceived(int hitsReceived) { this.hitsReceived = hitsReceived; }

    public int getCellsWalked() { return cellsWalked; }
    public void setCellsWalked(int cellsWalked) { this.cellsWalked = cellsWalked; }

    public long getTimestamp() { return timestamp; }

    public int getFinalStrength() { return finalStrength; }
    public void setFinalStrength(int finalStrength) { this.finalStrength = finalStrength; }

    public int getFinalAgility() { return finalAgility; }
    public void setFinalAgility(int finalAgility) { this.finalAgility = finalAgility; }

    public int getFinalMaxHealth() { return finalMaxHealth; }
    public void setFinalMaxHealth(int finalMaxHealth) { this.finalMaxHealth = finalMaxHealth; }

    // === Инкремент-методы для удобного обновления статистики ===

    public void addTreasure(Treasure item) { this.treasuresCollected+= item.getValue(); }
    public void addEnemyDefeated() { this.enemiesDefeated++; }
    public void addFoodEaten() { this.foodEaten++; }
    public void addElixirDrunk() { this.elixirsDrunk++; }
    public void addScrollRead() { this.scrollsRead++; }
    public void addHitLanded() { this.hitsLanded++; }
    public void addHitMissed() { this.hitsMissed++; }
    public void addHitReceived() { this.hitsReceived++; }
    public void addCellWalked() { this.cellsWalked++; }
}
