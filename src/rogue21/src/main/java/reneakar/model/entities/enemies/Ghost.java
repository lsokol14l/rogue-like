package reneakar.model.entities.enemies;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Character;
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

public class Ghost extends Enemy implements AttackerCharacter, MoveableCharacter {
  private boolean isVisible = true;

  public Ghost(Position position) {
    super(
        EnemyType.GHOST,
        position,
        CharScale.LOW.getScale(),
        CharScale.HIGH.getScale(),
        CharScale.LOW.getScale(),
        CharScale.LOW.getScale());
  }

  @Override
  public boolean canAttack(Character target) {
    return this.getPosition().isNeighborsWith(target.getPosition());
  }

  /**
   * Вероятность попадания зависит от разницы ловкости. Базовый шанс 60% + бонус за превосходство в
   * ловкости.
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

  /*
    Паттерн: выбирается рандомное направление движения,
    в том числе и отсутствие изменения позиции (направление CURRENT).
    Далее происходит несколько шагов в этом направлении так как призрак телепортируется по комнате.
  */
  @Override
  public Position followPattern(List<Cell> cells) {
    Directions newDirection = Directions.getRandom();
    Position newPosition = getPosition();
    int STEP = 4;
    for (int j = 0; j < STEP; ++j) {
      newPosition = newDirection.move(getPosition());
    }
    if (canMove(cells, newPosition)) {
      this.setDirection(newDirection);
      return newPosition;
    }
    return this.getPosition();
  }

  public void changeVisibility() {
    this.isVisible = !this.isVisible;
  }

  public boolean isVisible() {
    return this.isVisible;
  }

  @Override
  public Treasure dropTreasure() {
    return new Treasure(getPosition(), ItemSubType.COPPER_COIN, 10);
  }
}
