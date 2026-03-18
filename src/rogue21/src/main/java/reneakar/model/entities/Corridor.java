package reneakar.model.entities;

import java.util.ArrayList;
import java.util.List;

import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

public class Corridor {
  private final List<Cell> cells = new ArrayList<>();

  public Corridor(Position start, Position end, boolean horizontalFirst) {
    createCorridor(start, end, horizontalFirst);
  }

  private void createCorridor(Position start, Position end, boolean horizontalFirst) {
    int x = start.x();
    int y = start.y();
    int dx = Integer.compare(end.x(), x);
    int dy = Integer.compare(end.y(), y);

    if (horizontalFirst) {
      int pathX = start.x() - end.x();

      while (x != end.x() + pathX / 2) {
        x += dx;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
      while (y != end.y()) {
        y += dy;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
      while (x != end.x()) {
        x += dx;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
    } else {
      int pathY = start.y() - end.y();

      while (y != end.y() + pathY / 2) {
        y += dy;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
      while (x != end.x()) {
        x += dx;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
      while (y != end.y()) {
        y += dy;
        Position p = new Position(x, y);
        if (!p.equals(start) && !p.equals(end)) {
          cells.add(new Cell(CellType.CORRIDOR, p));
        }
      }
    }
  }

  public List<Cell> getCells() {
    return new ArrayList<>(cells);
  }
}
