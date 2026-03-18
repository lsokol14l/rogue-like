package reneakar.model.entities.items;

import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.UsableItem;
import reneakar.model.enums.CharScale;
import reneakar.model.enums.CharType;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.valueobjects.Position;

import java.util.concurrent.ThreadLocalRandom;

public class Elixir extends Item implements UsableItem {

    int tempMaxHealthEffect;
    int tempAgilityEffect;
    int tempStrengthEffect;

    CharType tempType;

    public CharType getTempType() {
        return tempType;
    }

    int timer;

    public Elixir(Position position, ItemSubType subType, int tempMaxHealthEffect, int tempAgilityEffect, int tempStrengthEffect, int duration, CharType tempType) {
        super(position, ItemType.ELIXIR, subType);
        this.tempMaxHealthEffect = tempMaxHealthEffect;
        this.tempAgilityEffect = tempAgilityEffect;
        this.tempStrengthEffect = tempStrengthEffect;
        this.timer = duration;
        this.tempType = tempType;
    }

    public int getTempMaxHealthEffect() {
        return tempMaxHealthEffect;
    }

    public int getTempAgilityEffect() {
        return tempAgilityEffect;
    }

    public int getTempStrengthEffect() {
        return tempStrengthEffect;
    }

    @Override
    public boolean canUse(Player player) {
        return this.timer > 0 && tempType != null;
    }

    @Override
    public void use(Player player) {
        switch (tempType) {
            case MAXHEALTH -> {
                player.setMaxHealth(player.getMaxHealth() + tempMaxHealthEffect);
                player.setCurrentHealth(player.getCurrentHealth() + tempMaxHealthEffect);
            }
            case AGILITY -> {
                player.setAgility(player.getAgility() + tempAgilityEffect);
            }
            case STRENGTH -> {
                player.setStrength(player.getStrength() + tempStrengthEffect);
            }
        }
        --this.timer; // т.к. эликстир действует какое-то время
    }

    public void cancelUse(Player player) {
        switch (tempType) {
            case MAXHEALTH: {
                player.setMaxHealth(player.getMaxHealth() - tempMaxHealthEffect);
                player.setCurrentHealth(player.getCurrentHealth() - tempMaxHealthEffect);
                if (player.getCurrentHealth() <= 0) {
                    player.setCurrentHealth(CharScale.LOW.getScale());
                }
                break;
            }
            case AGILITY: {
                player.setAgility(player.getAgility() - tempAgilityEffect);
                break;
            }
            case STRENGTH: {
                player.setStrength(player.getStrength() - tempStrengthEffect);
                break;
            }
        }
        tempType = null;
    }
}
