package reneakar.model.enums;

import reneakar.model.valueobjects.Position;

import java.util.Random;

public enum Directions {
  CURRENT(0, 0),
  DOWN(0, 1),
  RIGHT(1, 0),
  UP(0, -1),
  LEFT(-1, 0),
  RIGHT_DOWN(1, 1),
  LEFT_UP(-1, -1),
  RIGHT_UP(1, -1),
  LEFT_DOWN(-1, 1);

  private final int dx;
  private final int dy;

  private static final Random RANDOM = new Random();
  private static final Directions[] VALUES = values();

  Directions(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  public int getDx() {
    return dx;
  }

  public int getDy() {
    return dy;
  }

  public static Directions getRandom() {
    return VALUES[RANDOM.nextInt(VALUES.length)];
  }

  public Position move(Position position) {
    return new Position(position.x() + dx, position.y() + dy);
  }
}