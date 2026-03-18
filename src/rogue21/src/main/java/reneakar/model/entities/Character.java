package reneakar.model.entities;

import reneakar.model.valueobjects.Position;

public abstract class Character {
  private Position position;
  private int currentHealth;
  private int agility; // ловкость
  private int strength;

  @SuppressWarnings("unused")
  protected Character() {
    this.position = new Position(0, 0);
    this.currentHealth = 0;
    this.agility = 0;
    this.strength = 0;
  }

  public Character(Position position, int currentHealth, int agility, int strength) {
    this.position = position;
    this.currentHealth = currentHealth;
    this.agility = agility;
    this.strength = strength;
  }

  public Position getPosition() {
    return position;
  }

  public void setPosition(Position newPosition) {
    this.position = newPosition;
  }

  public int getCurrentHealth() {
    return currentHealth;
  }

  public void setCurrentHealth(int newCurrentHealth) {
    this.currentHealth = newCurrentHealth;
  }

  public int getAgility() {
    return agility;
  }

  public void setAgility(int newAgility) {
    this.agility = newAgility;
  }

  public int getStrength() {
    return strength;
  }

  public void setStrength(int newStrength) {
    this.strength = newStrength;
  }
}
