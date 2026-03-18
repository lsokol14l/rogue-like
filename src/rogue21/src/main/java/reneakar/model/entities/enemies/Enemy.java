package reneakar.model.entities.enemies;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Character;
import reneakar.model.entities.items.Treasure;
import reneakar.model.enums.Directions;
import reneakar.model.enums.EnemyType;
import reneakar.model.valueobjects.Position;
import reneakar.model.entities.Cell;

import java.util.List;


// TODO: нужен рефакторинг поскольку некоторые функции имеют одинаковую логику по всех дочерних классах
//      а может и не нужен так как это будет пересекаться с игроком
public abstract class Enemy extends Character {
  private EnemyType type;
  private int hostility;
  private Directions direction;
  private boolean chasing;

  public static final int MAX_MOVEMENTS = 10;

  public Enemy(EnemyType type, Position position, int currentHealth, int agility, int strength, int hostility) {
    super(position, currentHealth, agility, strength);
    this.type = type;
    this.hostility = hostility;
    this.direction = Directions.CURRENT;
  }

  public int getHostility() {
    return hostility;
  }

  public void setHostility(int newHostility) {
    this.hostility = newHostility;
  }

  public EnemyType getType() {
    return type;
  }

  public void setType(EnemyType newType) {
    this.type = newType;
  }

  public Directions getDirection() {
    return this.direction;
  }

  public void setDirection(Directions direction) {
    this.direction = direction;
  }

  public Position followPattern(List<Cell> cells) {
    return this.getPosition();
  }

  public abstract Treasure dropTreasure();
}
