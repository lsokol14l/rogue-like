package reneakar.model.entities.behaviors;

import reneakar.model.entities.Cell;
import reneakar.model.valueobjects.Position;

import java.util.List;

public interface MoveableCharacter {
  boolean canMove(List<Cell> cells, Position position);

  void move(Position position);
  // только для монстров
  // TODO: возможно стоит сделать отдельный интерфейс movements pattern
  // TODO: может быть стоит заранее создавать паттерн и возвращать его ввиде List
}
