package reneakar.model.entities.enemies;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Character;
import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.AttackerCharacter;
import reneakar.model.entities.behaviors.MoveableCharacter;
import reneakar.model.entities.items.Treasure;
import reneakar.model.enums.CharScale;
import reneakar.model.enums.Directions;
import reneakar.model.enums.EnemyType;
import reneakar.model.enums.ItemSubType;
import reneakar.model.valueobjects.Position;

import java.util.List;

import static java.lang.Math.max;

// TODO: патерн преследования?

public class Vampire extends Enemy implements AttackerCharacter, MoveableCharacter {
  private boolean firstAttack = true;

  //Вампир (отображение: красная v): высокая ловкость, враждебность и здоровье; средняя сила. Отнимает некоторое количество максимального уровня здоровья игроку при успешной атаке.
  public Vampire(Position position) {
    super(EnemyType.VAMPIRE, position, CharScale.HIGH.getScale(), CharScale.HIGH.getScale(), CharScale.MEDIUM.getScale(), CharScale.HIGH.getScale());
  }

  @Override
  public boolean canAttack(Character target) {
    return this.getPosition().isNeighborsWith(target.getPosition());
  }

  /**
   * Вероятность попадания зависит от разницы ловкости.
   */
  @Override
  public boolean probabilityAttack(Character target) {
    int baseChance = 60;
    int agilityDiff = getAgility() - target.getAgility();
    int hitChance = baseChance + agilityDiff;
    hitChance = Math.max(10, Math.min(95, hitChance));
    return Math.random() * 100 < hitChance;
  }

  @Override
  public int calcDamage(Character target) {
    return probabilityAttack(target) ? getStrength() : 0;
  }

  @Override
  public Treasure dropTreasure() {
    return new Treasure(getPosition(), ItemSubType.SILVER_BARS, 20);
  }

  /**
   * Вампир наносит обычный урон + небольшой дебафф maxHP (5 единиц).
   * Первая атака вампира пропускается (ритуал пробуждения).
   */
  @Override
  public void attack(Character target) {
    if (firstAttack) {
      setFirstAttack();
      return;
    }
    int damage = calcDamage(target);
    // Основной урон по HP
    target.setCurrentHealth(max(0, target.getCurrentHealth() - damage));
    // Дополнительно снимает 5 maxHP (уникальная способность вампира)
    if (damage > 0 && target instanceof Player player) {
      player.setMaxHealth(max(10, player.getMaxHealth() - 5));
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

  /*
      Паттерн: U -> R -> D -> L -> U и т.д. по кругу
      Если не получается сдвинуть, то просто меняем направление
      (по паттерну)
  */
  @Override
  public Position followPattern(List<Cell> cells) {
    Position newPosition = null;
    Directions newDirection = this.getDirection();
    for (int i = 0; i < 4; ++i) {
      switch (newDirection) {
        case Directions.UP -> newDirection = Directions.RIGHT;
        case Directions.RIGHT -> newDirection = Directions.DOWN;
        case Directions.DOWN -> newDirection = Directions.LEFT;
        case Directions.LEFT -> newDirection = Directions.UP;
      }
      newPosition = newDirection.move(getPosition());
      if (!canMove(cells, newPosition)) {
        break;
      }
    }
    if (canMove(cells, newPosition)) {
      this.setDirection(newDirection);
      return newPosition;
    }
    return this.getPosition();
  }

  public boolean isFirstAttack() {
    return firstAttack;
  }

  public void setFirstAttack() {
    this.firstAttack = false;
  }
}
