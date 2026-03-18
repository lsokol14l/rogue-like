package reneakar.model.entities.enemies;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Character;
import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.AttackerCharacter;
import reneakar.model.entities.behaviors.MoveableCharacter;
import reneakar.model.entities.enemies.algorithms.Graph;
import reneakar.model.entities.items.Treasure;
import reneakar.model.enums.CharScale;
import reneakar.model.enums.Directions;
import reneakar.model.enums.EnemyType;
import reneakar.model.enums.ItemSubType;
import reneakar.model.valueobjects.Position;

import java.util.List;

import static java.lang.Math.max;

public class SnakeMage extends Enemy implements AttackerCharacter, MoveableCharacter {

  public SnakeMage(Position position) {
    super(
        EnemyType.SNAKEMAGE,
        position,
        CharScale.MEDIUM.getScale(),
        CharScale.HIGH.getScale(),
        CharScale.MEDIUM.getScale(),
        CharScale.HIGH.getScale());
  }

  @Override
  public boolean canAttack(Character target) {
    return this.getPosition().isNeighborsWith(target.getPosition());
  }

  /** Вероятность попадания зависит от разницы ловкости. */
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

  /** Атака с 20% шансом усыпления игрока. */
  @Override
  public void attack(Character target) {
    target.setCurrentHealth(max(0, target.getCurrentHealth() - calcDamage(target)));
    // 20% шанс усыпления (вместо 50%)
    if (target instanceof Player player && Math.random() < 0.20) {
      putPlayerToSleep(player);
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

  public Position checkNewDirections(
      List<Cell> cells, Directions newDirection1, Directions newDirection2) {
    Position newPosition = newDirection1.move(this.getPosition());
    if (canMove(cells, newPosition)) {
      this.setDirection(newDirection1);
      return newPosition;
    }
    if (newDirection2 == null) {
      return null;
    }
    newPosition = newDirection2.move(this.getPosition());
    if (canMove(cells, newPosition)) {
      this.setDirection(newDirection2);
      return newPosition;
    }
    return this.getPosition();
  }

  @Override
  public Position followPattern(List<Cell> cells) {
    switch (this.getDirection()) {
      case Directions.LEFT_UP:
        {
          return checkNewDirections(cells, Directions.RIGHT_UP, Directions.LEFT_DOWN);
        }
      case Directions.RIGHT_UP:
        {
          return checkNewDirections(cells, Directions.LEFT_UP, Directions.RIGHT_DOWN);
        }
      case Directions.RIGHT_DOWN:
        {
          return checkNewDirections(cells, Directions.LEFT_DOWN, Directions.RIGHT_UP);
        }
      case Directions.LEFT_DOWN:
        {
          return checkNewDirections(cells, Directions.RIGHT_DOWN, Directions.LEFT_UP);
        }
      case Directions.CURRENT:
        {
          for (Directions curDirection : Directions.values()) {
            Position newPosition = checkNewDirections(cells, curDirection, null);
            if (newPosition != null) {
              return newPosition;
            }
          }
        }
    }
    return this.getPosition();
  }

  public void putPlayerToSleep(Player player) {
    player.sleep();
  }

  @Override
  public Treasure dropTreasure() {
    return new Treasure(getPosition(), ItemSubType.GOLD_COINS, 30);
  }
}
