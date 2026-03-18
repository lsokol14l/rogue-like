package reneakar.model.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import reneakar.model.entities.Cell;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.enums.CellType;
import reneakar.model.enums.WallSide;
import reneakar.model.valueobjects.Position;

public class RogueUtils {
  private RogueUtils() {
  }

  public static WallSide getDoorWallSide(List<Sector> sectors, Room roomA, Room roomB) {
    // Находим секторы для комнат
    Sector sectorA = findSectorForRoom(sectors, roomA);
    Sector sectorB = findSectorForRoom(sectors, roomB);

    // Получаем абсолютные позиции центров секторов
    Position centerA =
            new Position(
                    sectorA.getStart().x() + sectorA.getWidth() / 2,
                    sectorA.getStart().y() + sectorA.getHeight() / 2);

    Position centerB =
            new Position(
                    sectorB.getStart().x() + sectorB.getWidth() / 2,
                    sectorB.getStart().y() + sectorB.getHeight() / 2);

    // Вычисляем разницу между центрами
    int deltaX = centerB.x() - centerA.x();
    int deltaY = centerB.y() - centerA.y();

    // Определяем главное направление (по большей разнице)
    if (Math.abs(deltaX) > Math.abs(deltaY)) {
      // Горизонтальное расположение
      return deltaX > 0 ? WallSide.EAST : WallSide.WEST;
    } else {
      // Вертикальное расположение
      return deltaY > 0 ? WallSide.SOUTH : WallSide.NORTH;
    }
  }

  public static Position getRandomEmptyCellPosition(Room room) {
    List<Cell> cells = room.getCells();
    List<Cell> emptyCells = new ArrayList<>();

    for (Cell cell : cells)
      if (cell.isPassable() && cell.getType() == CellType.FLOOR && !cell.hasItem())
        emptyCells.add(cell);

    if (emptyCells.isEmpty()) {
      // Fallback: возвращаем центр комнаты
      return room.getCenter();
    }

    Random rd = new Random();
    return emptyCells.get(rd.nextInt(emptyCells.size())).getPosition();
  }

  public static Position toAbsolute(List<Sector> sectors, Room room, Position local) {
    Sector s = findSectorForRoom(sectors, room);
    if (s == null) return local;
    return new Position(s.getStart().x() + local.x(), s.getStart().y() + local.y());
  }

  public static Sector findSectorForRoom(List<Sector> sectors, Room room) {
    for (Sector s : sectors) if (s.getRoom() == room) return s;
    return null;
  }
}
