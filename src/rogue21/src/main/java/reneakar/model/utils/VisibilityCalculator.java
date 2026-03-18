package reneakar.model.utils;

import reneakar.model.entities.Cell;
import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Утилитный класс для расчета видимых клеток с помощью алгоритма Ray Casting.
 * Используется для реализации тумана войны (Fog of War).
 */
public class VisibilityCalculator {
  private static final int VISION_RADIUS = 8; // Радиус видимости в клетках

  /**
   * Вычисляет все клетки, видимые из заданной позиции (обычно позиция игрока).
   * Использует Ray Casting: бросает лучи в 360 градусов с шагом 2° и определяет
   * видимые клетки с помощью алгоритма Брезенхэма.
   *
   * @param grid      сетка уровня (двумерный массив клеток)
   * @param playerPos позиция наблюдателя (игрока)
   * @return набор видимых позиций
   */
  public static Set<Position> calculateVisibleCells(Cell[][] grid, Position playerPos) {
    Set<Position> visible = new HashSet<>();

    // Игрок всегда видит свою клетку
    visible.add(playerPos);

    // Бросаем лучи в 360 градусов с шагом 2°
    for (int angle = 0; angle < 360; angle += 2) {
      castRayWithBresenham(grid, playerPos, angle, VISION_RADIUS, visible);
    }

    return visible;
  }

  /**
   * Бросает луч под определенным углом, используя алгоритм Брезенхэма для
   * точной трассировки линии. Луч останавливается при столкновении со стеной.
   *
   * @param grid        сетка уровня
   * @param start       начальная позиция (игрок)
   * @param angle       угол луча в градусах (0-360)
   * @param maxDistance максимальная дальность видимости
   * @param visible     набор для накопления видимых клеток
   */
  private static void castRayWithBresenham(Cell[][] grid, Position start, int angle,
                                           int maxDistance, Set<Position> visible) {
    // Вычисляем конечную точку луча на основе угла и максимальной дистанции
    double radians = Math.toRadians(angle);
    int endX = (int) Math.round(start.x() + Math.cos(radians) * maxDistance);
    int endY = (int) Math.round(start.y() + Math.sin(radians) * maxDistance);
    Position end = new Position(endX, endY);

    // Используем Брезенхэм для получения всех клеток на луче
    List<Position> line = BresenhamLine.getLine(start, end);

    // Проходим по каждой клетке на луче
    for (Position pos : line) {
      // Проверка границ карты
      if (pos.x() < 0 || pos.x() >= grid[0].length ||
              pos.y() < 0 || pos.y() >= grid.length) {
        break; // Луч вышел за границы
      }

      // Добавляем текущую клетку в видимые
      visible.add(pos);

      // Проверка препятствия (стена)
      if (!grid[pos.y()][pos.x()].isPassable()) {
        // Луч ударился в стену - ВИДИМ стену, но не идём дальше
        break;
      }
    }
  }

  /**
   * Проверяет, находится ли позиция внутри комнаты.
   * Комната - это клетка типа ROOM.
   *
   * @param grid сетка уровня
   * @param pos  проверяемая позиция
   * @return true если позиция в комнате, иначе false
   */
  public static boolean isInRoom(Cell[][] grid, Position pos) {
    if (pos.x() < 0 || pos.x() >= grid[0].length ||
            pos.y() < 0 || pos.y() >= grid.length) {
      return false;
    }
    return grid[pos.y()][pos.x()].getType() == CellType.ROOMFLOOR;
  }

  /**
   * Возвращает все клетки комнаты, в которой находится указанная позиция.
   * Использует BFS (поиск в ширину) для обхода всех связных клеток типа ROOM.
   *
   * @param grid  сетка уровня
   * @param start начальная позиция (должна быть в комнате)
   * @return набор всех позиций комнаты
   */
  public static Set<Position> getRoomCells(Cell[][] grid, Position start) {
    Set<Position> roomCells = new HashSet<>();

    // Если стартовая позиция не в комнате, возвращаем пустой набор
    if (!isInRoom(grid, start)) {
      return roomCells;
    }

    // BFS для обхода всех клеток комнаты
    Queue<Position> queue = new LinkedList<>();
    Set<Position> visited = new HashSet<>();

    queue.add(start);
    visited.add(start);

    // 4 направления: вверх, вниз, влево, вправо
    int[][] directions = {{0, -1}, {0, 1}, {-1, 0}, {1, 0}};

    while (!queue.isEmpty()) {
      Position current = queue.poll();
      roomCells.add(current);

      // Проверяем соседние клетки
      for (int[] dir : directions) {
        int newX = current.x() + dir[0];
        int newY = current.y() + dir[1];
        Position neighbor = new Position(newX, newY);

        // Проверка границ и что клетка не была посещена
        if (newX >= 0 && newX < grid[0].length &&
                newY >= 0 && newY < grid.length &&
                !visited.contains(neighbor)) {

          visited.add(neighbor);

          // Добавляем только клетки комнаты
          if (grid[newY][newX].getType() == CellType.ROOMFLOOR) {
            queue.add(neighbor);
          }
        }
      }
    }

    return roomCells;
  }
}