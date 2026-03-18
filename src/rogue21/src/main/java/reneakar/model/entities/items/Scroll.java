package reneakar.model.entities.items;

import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.UsableItem;
import reneakar.model.enums.CharType;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

public class Scroll extends Item implements UsableItem {
    CharType charType;
    private int maxHealthEffect;
    private int agilityEffect;
    private int strengthEffect;

    public Scroll(Position position, ItemSubType subType, int maxHealthEffect, int agilityEffect, int strengthEffect, CharType type) {
        super(position, ItemType.SCROLL, subType);
        this.maxHealthEffect = maxHealthEffect;
        this.agilityEffect = agilityEffect;
        this.strengthEffect = strengthEffect;
        this.charType = type;
    }

    public CharType getCharType() {
        return charType;
    }

    public int getMaxHealthEffect() {
        return maxHealthEffect;
    }

    public int getAgilityEffect() {
        return agilityEffect;
    }

    public int getStrengthEffect() {
        return strengthEffect;
    }

    @Override
    public boolean canUse(Player player) {
        return true;
    }

    @Override
    public void use(Player player) {
        switch (charType) {
            case MAXHEALTH -> {
                player.setMaxHealth(player.getMaxHealth() + maxHealthEffect);
                player.setCurrentHealth(player.getCurrentHealth() + maxHealthEffect);
            }
            case AGILITY ->
                player.setAgility(player.getAgility() + agilityEffect);
            case STRENGTH ->
                player.setStrength(player.getStrength() + strengthEffect);
        }
    }
}
