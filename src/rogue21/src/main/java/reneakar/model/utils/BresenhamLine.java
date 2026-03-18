package reneakar.model.utils;

import java.util.ArrayList;
import java.util.List;

import reneakar.model.valueobjects.Position;

/**
 * Реализация алгоритма Брезенхэма для построения прямых линий на сетке.
 * Используется для Ray Casting и тумана войны (Fog of War).
 * Использует только целочисленную арифметику для максимальной производительности.
 */
public class BresenhamLine {

  /**
   * Возвращает список всех клеток на прямой линии от from до to (включительно).
   * Использует алгоритм Брезенхэма для точной трассировки.
   *
   * @param from начальная позиция (обычно позиция игрока)
   * @param to   конечная позиция (край видимости)
   * @return список позиций, через которые проходит луч
   */
  public static List<Position> getLine(Position from, Position to) {
    List<Position> result = new ArrayList<>();

    int x0 = from.x(), y0 = from.y();
    int x1 = to.x(), y1 = to.y();

    int dx = Math.abs(x1 - x0);
    int dy = Math.abs(y1 - y0);

    int sx = x0 < x1 ? 1 : -1;  // Направление шага по X
    int sy = y0 < y1 ? 1 : -1;  // Направление шага по Y

    int err = dx - dy;

    while (true) {
      result.add(new Position(x0, y0));

      // Достигли конца
      if (x0 == x1 && y0 == y1) break;

      int e2 = 2 * err;

      if (e2 > -dy) {
        err -= dy;
        x0 += sx;
      }
      if (e2 < dx) {
        err += dx;
        y0 += sy;
      }
    }

    return result;
  }
}