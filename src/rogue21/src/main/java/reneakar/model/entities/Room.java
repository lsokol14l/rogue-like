package reneakar.model.entities;

import java.util.ArrayList;
import java.util.List;

import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

public class Room {
  private Position relativeTopLeft;
  private Position absoluteTopLeft;
  private int width;
  private int height;
  private List<Cell> cells;
  private List<Position> doorPositions;

  public Room(Position absoluteTopLeft, Position relativeTopLeft, int width, int height) {
    this.absoluteTopLeft = absoluteTopLeft;
    this.relativeTopLeft = relativeTopLeft;
    this.width = width;
    this.height = height;
    this.cells = new ArrayList<>();
    this.doorPositions = new ArrayList<>();

    createRoom();
  }

  public Position getAbsoluteTopLeft() {
    return absoluteTopLeft;
  }

  public int getWidth() {
    return width;
  }

  public int getHeight() {
    return height;
  }

  public Position getRelativeTopLeft() {
    return relativeTopLeft;
  }

  public void createRoom() {
    for (int x = relativeTopLeft.x(); x < relativeTopLeft.x() + width; x++) {
      for (int y = relativeTopLeft.y(); y < relativeTopLeft.y() + height; y++) {

        CellType cellType;

        if (x == relativeTopLeft.x()
                || x == relativeTopLeft.x() + width - 1
                || y == relativeTopLeft.y()
                || y == relativeTopLeft.y() + height - 1) {
          cellType = CellType.WALL;
        } else {
          cellType = CellType.FLOOR;
        }

        cells.add(new Cell(cellType, new Position(x, y)));
      }
    }
  }

  public List<Cell> getCells() {
    return new ArrayList<>(cells);
  }

  public List<Position> getDoorPositions() {
    return new ArrayList<>(doorPositions);
  }

  public Position getCenter() {
    return new Position(relativeTopLeft.x() + width / 2, relativeTopLeft.y() + height / 2);
  }

  public boolean containsPosition(Position position) {
    return position.x() >= relativeTopLeft.x()
            && position.x() < relativeTopLeft.x() + width
            && position.y() >= relativeTopLeft.y()
            && position.y() < relativeTopLeft.y() + height;
  }

  public boolean isOnBorder(Position position) {
    if (!containsPosition(position)) return false;
    int left = relativeTopLeft.x();
    int top = relativeTopLeft.y();
    int right = left + width - 1;
    int bottom = top + height - 1;
    return position.x() == left
            || position.x() == right
            || position.y() == top
            || position.y() == bottom;
  }

  public void addDoor(Position doorPosition) {
    if (isOnBorder(doorPosition)) {
      doorPositions.add(doorPosition);
    }

    for (Cell cell : cells) {
      if (cell.getPosition().equals(doorPosition)) {
        cell.setType(CellType.DOOR);
      }
    }
  }
}
