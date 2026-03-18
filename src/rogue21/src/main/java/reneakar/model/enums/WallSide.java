package reneakar.model.enums;

public enum WallSide {
  NORTH,
  SOUTH,
  WEST,
  EAST;

  public WallSide opposition() {
    return switch (this) {
      case NORTH -> SOUTH;
      case SOUTH -> NORTH;
      case WEST -> EAST;
      case EAST -> WEST;
    };
  }
}
