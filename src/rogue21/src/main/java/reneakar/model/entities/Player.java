package reneakar.model.entities;

import java.util.Arrays;
import java.util.List;

import reneakar.model.entities.behaviors.AttackerCharacter;
import reneakar.model.entities.behaviors.EquippableItem;
import reneakar.model.entities.behaviors.MoveableCharacter;
import reneakar.model.entities.behaviors.UsableItem;
import reneakar.model.entities.items.Item;
import reneakar.model.entities.items.Weapon;
import reneakar.model.valueobjects.Position;

public class Player extends Character implements AttackerCharacter, MoveableCharacter {
  private final Backpack backpack;
  private int maxHealth;
  private Weapon currentWeapon;
  private boolean sleeping = false; // змей-маг может усыпить игрока
  private int selectedInventoryIndex = 0;

  @SuppressWarnings("unused")
  private Player() {
    super(new Position(0, 0), 0, 0, 0);
    this.backpack = new Backpack(9);
    this.maxHealth = 0;
    this.currentWeapon = null;
  }

  public Player(Position position, int maxHealth, int currentHealth, int agility, int strength) {
    super(position, currentHealth, agility, strength);
    this.maxHealth = maxHealth;
    this.currentWeapon = null;
    this.backpack = new Backpack(9);
  }

  public int getSelectedInventoryIndex() {
    return selectedInventoryIndex;
  }

  public void setSelectedInventoryIndex(int selectedInventoryIndex) {
    this.selectedInventoryIndex = selectedInventoryIndex;
  }

  public void selectNextItem() {
    int size = backpack.getItems().size();
    if (size > 0) {
      selectedInventoryIndex = (selectedInventoryIndex + 1) % size;
    }
  }

  public void selectPreviousItem() {
    int size = backpack.getItems().size();
    if (size > 0) {
      selectedInventoryIndex = (selectedInventoryIndex - 1 + size) % size;
    }
  }

  public Item getSelectedItem() {
    List<Item> items = backpack.getItems();
    if (items.isEmpty() || selectedInventoryIndex >= items.size()) {
      return null;
    }
    return items.get(selectedInventoryIndex);
  }

  public Weapon getCurrentWeapon() {
    return this.currentWeapon;
  }

  public void setCurrentWeapon(Weapon weapon) {
    this.currentWeapon = weapon;
  }

  // Метод для экипировки оружия
  public void equip(Weapon weapon, Cell[][] grid) {
    // 1. Проверяем, есть ли уже экипированное оружие
    Weapon currentWeapon = getCurrentWeapon();

    if (currentWeapon != null && currentWeapon != weapon) {
      // Снимаем текущее оружие
      currentWeapon.unequip(
              java.util.Arrays.stream(grid).flatMap(java.util.Arrays::stream).toList(),
              this
      );
    }

    // 2. Экипируем новое оружие (если оно не то же самое)
    if (currentWeapon != weapon) {
      weapon.equip(this);
    }
  }

  public int getMaxHealth() {
    return maxHealth;
  }

  public void setMaxHealth(int newMaxHealth) {
    this.maxHealth = newMaxHealth;
  }

  public Backpack getBackpack() {
    return this.backpack;
  }

  public boolean pickItem(Item item) {
    return this.backpack.addItem(item);
  }

  @Override
  public boolean canAttack(Character target) {
    return this.getPosition().isNeighborsWith(target.getPosition());
  }

  /**
   * Вероятность попадания игрока. Базовый шанс 65% + бонус за ловкость.
   */
  @Override
  public boolean probabilityAttack(Character target) {
    int baseChance = 65;
    int agilityDiff = getAgility() - target.getAgility();
    int hitChance = baseChance + agilityDiff;
    hitChance = Math.max(15, Math.min(95, hitChance));
    return Math.random() * 100 < hitChance;
  }

  /**
   * Расчёт урона игрока. Урон зависит от силы и вероятности попадания.
   */
  @Override
  public int calcDamage(Character target) {
    if (!probabilityAttack(target)) {
      return 0; // промах
    }
    // Бонус оружия уже включен в getStrength() после экипировки
    return getStrength();
  }

  @Override
  public void attack(Character target) {
    if (canAttack(target)) {
      target.setCurrentHealth(Math.max(0, target.getCurrentHealth() - calcDamage(target)));
    }
  }

  @Override
  public boolean canMove(List<Cell> cells, Position position) {
    for (Cell cell : cells) {
      if (cell.getPosition().equals(position)) {
        return cell.isPassable();
      }
    }
    return false;
  }

  @Override
  public void move(Position position) {
    setPosition(position);
  }

  public void sleep() {
    this.sleeping = true;
  }

  public void unsleep() {
    this.sleeping = false;
  }

  public boolean canTakeItem() {
    return backpack.getSize() != backpack.getCapacity();
  }

  public void equip(EquippableItem item, Cell[][] grid) {
    if (item.canEquip(this)) item.equip(this);
    else item.unequip(Arrays.stream(grid).flatMap(Arrays::stream).toList(), this);
  }

  public void use(UsableItem item) {
    item.use(this);
  }
}
