package reneakar.model.valueobjects;

import java.util.Objects;

public record Position(int x, int y) {
  public boolean isNeighborsWith(Position other) {
    int subX = x() - other.x();
    int subY = y() - other.y();
    return -1 <= subX && subX <= 1 && -1 <= subY && subY <= 1;
  }
}
