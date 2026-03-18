package reneakar.model.entities;

import java.util.Random;

import reneakar.model.valueobjects.Position;

public class Sector {
  private final Position start; // левый верхний угол (включительно)
  private final Position end; // правый нижний угол (включительно)
  private Room room;

  public Sector(Position start, Position end) {
    this.start = start;
    this.end = end;

    generateRoom();
  }

  private void generateRoom() {
    // Учёт включаемых границ
    int sectorWidth = end.x() - start.x() + 1;
    int sectorHeight = end.y() - start.y() + 1;

    Random rd = new Random();

    // Минимальные размеры комнаты (стены по периметру + пол внутри)
    int minRoomW = Math.min(6, sectorWidth);
    int minRoomH = Math.min(4, sectorHeight);

    // Отступы от границ сектора, чтобы комната точно влезла
    int margin = 1;

    // Максимальные размеры комнаты с учётом отступов
    int maxRoomW = Math.max(minRoomW, sectorWidth - 2 * margin);
    int maxRoomH = Math.max(minRoomH, sectorHeight - 2 * margin);

    // Случайные размеры в допустимом диапазоне
    int width = rd.nextInt(Math.max(1, maxRoomW - minRoomW + 1)) + minRoomW;
    int height = rd.nextInt(Math.max(1, maxRoomH - minRoomH + 1)) + minRoomH;

    // Смещения внутри сектора (локальные координаты комнаты в секторе)
    int xShiftRange = Math.max(0, sectorWidth - width - 2 * margin);
    int yShiftRange = Math.max(0, sectorHeight - height - 2 * margin);

    int xShift = margin + (xShiftRange > 0 ? rd.nextInt(xShiftRange + 1) : 0);
    int yShift = margin + (yShiftRange > 0 ? rd.nextInt(yShiftRange + 1) : 0);

    // Локальная левая-верхняя точка комнаты относительно сектора
    Position roomTopLeftLocal = new Position(xShift, yShift);
    Position absoluteRoomPosition = new Position(xShift + start.x(), yShift + start.y());
    // ВАЖНО: Room хранит локальные координаты внутри сектора, абсолютные координаты
    // получаются в Level.updateGrid добавлением start сектора.
    this.room = new Room(absoluteRoomPosition, roomTopLeftLocal, width, height);
  }

  public Position getStart() {
    return start;
  }

  public Position getEnd() {
    return end;
  }

  public Room getRoom() {
    return room;
  }

  public void setRoom(Room room) {
    this.room = room;
  }

  public int getWidth() {
    return end.x() - start.x() + 1;
  }

  public int getHeight() {
    return end.y() - start.y() + 1;
  }

  public boolean contains(Position position) {
    return position.x() >= start.x()
            && position.x() <= end.x()
            && position.y() >= start.y()
            && position.y() <= end.y();
  }

  public Position getRandomPositionInside() {
    Random random = new Random();
    int x = start.x() + random.nextInt(getWidth());
    int y = start.y() + random.nextInt(getHeight());
    return new Position(x, y);
  }

  @Override
  public String toString() {
    return String.format(
            "Sector[%d,%d -> %d,%d]", start.x(), start.y(), end.x(), end.y());
  }
}
