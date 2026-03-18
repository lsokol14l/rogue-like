package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.enums.CellType.*;

import java.util.ArrayList;
import java.util.List;
import reneakar.model.entities.Cell;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.entities.enemies.Enemy;
import reneakar.model.entities.items.Item;
import reneakar.model.valueobjects.Position;

public class GridRasterizer {

  public Cell[][] initializeGrid(int width, int height) {
    Cell[][] grid = new Cell[height][width];
    for (int y = 0; y < height; ++y) {
      for (int x = 0; x < width; ++x) {
        grid[y][x] = new Cell(NULL, new Position(x, y));
      }
    }
    return grid;
  }

  public void applyRoomsAndDoors(Cell[][] grid, List<Sector> sectors) {
    int H = grid.length;
    int W = grid[0].length;

    for (Sector sec : sectors) {
      int startX = sec.getStart().x();
      int startY = sec.getStart().y();

      Room room = sec.getRoom();
      if (room == null) continue;

      List<Cell> cells = room.getCells();
      for (Cell cell : cells) {
        int x = startX + cell.getPosition().x();
        int y = startY + cell.getPosition().y();
        if (x >= 0 && x < W && y >= 0 && y < H) {
          grid[y][x].setType(cell.getType());
        }
      }
    }
  }

  public void applyCorridors(
      Cell[][] grid, java.util.List<reneakar.model.entities.Corridor> corridors) {
    int H = grid.length, W = grid[0].length;
    for (var c : corridors) {
      for (var corridorCell : c.getCells()) {
        int x = corridorCell.getPosition().x();
        int y = corridorCell.getPosition().y();
        if (x < 0 || x >= W || y < 0 || y >= H) continue;

        var cur = grid[y][x].getType();
        // не затираем двери и пол комнат
        if (cur == DOOR || cur == FLOOR || cur == WALL) continue;

        // разрешаем вырезать коридор из пустоты и стены
        if (cur == reneakar.model.enums.CellType.NULL) {
          grid[y][x].setType(CORRIDOR);
        }
      }
    }
  }

  public void applyItems(Cell[][] grid, ArrayList<Item> items) {
    final int HEIGHT = grid.length;
    final int WIDTH = grid[0].length;

    // Просто проходим по списку предметов и устанавливаем их в клетки
    for (Item item : items) {
      Position pos = item.getPosition();
      int x = pos.x();
      int y = pos.y();

      // Проверяем границы
      if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
        grid[y][x].setItem(item);
      }
    }
  }

  public void applyEnemies(Cell[][] grid, ArrayList<Enemy> enemies) {
    final int WIDTH = grid[0].length;
    final int HEIGHT = grid.length;

    for (Enemy enemy : enemies) {
      Position pos = enemy.getPosition();
      int x = pos.x();
      int y = pos.y();

      // Проверяем границы
      if (x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT) {
        grid[y][x].setOccupant(enemy);
      }
    }
  }
}
