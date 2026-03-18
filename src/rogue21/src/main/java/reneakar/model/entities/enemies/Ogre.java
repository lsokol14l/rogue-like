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

// TODO: патерн преследования

public class Ogre extends Enemy implements AttackerCharacter, MoveableCharacter {
  private boolean canAttackThisTurn = false;

  public Ogre(Position position) {
    super(EnemyType.OGRE, position, CharScale.HIGH.getScale(), CharScale.LOW.getScale(), CharScale.HIGH.getScale(), CharScale.MEDIUM.getScale());
  }

  /**
   * Огр атакует через ход (медленный, но сильный).
   * Переключаем флаг только если враг рядом.
   */
  @Override
  public boolean canAttack(Character target) {
    boolean inRange = this.getPosition().isNeighborsWith(target.getPosition());
    if (inRange) {
      canAttackThisTurn = !canAttackThisTurn;
      return canAttackThisTurn;
    }
    return false;
  }

  /**
   * Вероятность попадания. Огр неуклюж, но если попал — больно.
   */
  @Override
  public boolean probabilityAttack(Character target) {
    int baseChance = 55; // ниже чем у других
    int agilityDiff = getAgility() - target.getAgility();
    int hitChance = baseChance + agilityDiff;
    hitChance = Math.max(10, Math.min(90, hitChance));
    return Math.random() * 100 < hitChance;
  }

  @Override
  public int calcDamage(Character target) {
    return probabilityAttack(target) ? getStrength() : 0;
  }

  /**
   * Огр наносит обычный урон по HP (а не по maxHP).
   */
  @Override
  public void attack(Character target) {
    target.setCurrentHealth(max(0, target.getCurrentHealth() - calcDamage(target)));
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

  @Override
  public Position followPattern(List<Cell> cells) {
    Directions newDirection = Directions.getRandom();
    Position newPosition = newDirection.move(newDirection.move(getPosition()));
    if (canMove(cells, newPosition)) {
      this.setDirection(newDirection);
      return newPosition;
    }
    return this.getPosition();
  }

  @Override
  public Treasure dropTreasure() {
    return new Treasure(getPosition(), ItemSubType.DIAMOND, 40);
  }
}
