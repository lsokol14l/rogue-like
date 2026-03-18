package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.utils.RogueUtils.getDoorWallSide;

import java.util.ArrayList;
import java.util.List;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.enums.WallSide;
import reneakar.model.services.levelgenerator.algorithms.connectors.RoomConnector;
import reneakar.model.valueobjects.Position;
import reneakar.model.valueobjects.RoomConnection;

public class DoorPositioner {

  public List<RoomConnection> placeDoors(List<Sector> sectors, RoomConnector connector) {
    List<Room> rooms = new ArrayList<>();
    for (Sector s : sectors) rooms.add(s.getRoom());

    List<RoomConnection> connections = connector.generateConnections(rooms);

    for (RoomConnection c : connections) {
      // Стороны стен по направлению к парной комнате
      WallSide sideA = getDoorWallSide(sectors, c.roomA(), c.roomB());
      WallSide sideB = sideA.opposition();

      // Локальные позиции дверей (в координатах Room)
      Position doorA = getDoorPosition(c.roomA(), sideA);
      Position doorB = getDoorPosition(c.roomB(), sideB);

      // Ставим двери на границе (Room сам пометит клетку DOOR)
      c.roomA().addDoor(doorA);
      c.roomB().addDoor(doorB);
    }

    return connections;
  }

  // Локальная позиция двери на выбранной стене (избегаем углов)
  private Position getDoorPosition(Room room, WallSide side) {
    int left = room.getRelativeTopLeft().x();
    int top = room.getRelativeTopLeft().y();
    int right = left + room.getWidth() - 1;
    int bottom = top + room.getHeight() - 1;

    int midX = left + room.getWidth() / 2;
    int midY = top + room.getHeight() / 2;

    int safeLeft = room.getWidth() >= 3 ? left + 1 : left;
    int safeRight = room.getWidth() >= 3 ? right - 1 : right;
    int safeTop = room.getHeight() >= 3 ? top + 1 : top;
    int safeBottom = room.getHeight() >= 3 ? bottom - 1 : bottom;

    return switch (side) {
      case WEST -> new Position(left, clamp(midY, safeTop, safeBottom));
      case EAST -> new Position(right, clamp(midY, safeTop, safeBottom));
      case NORTH -> new Position(clamp(midX, safeLeft, safeRight), top);
      case SOUTH -> new Position(clamp(midX, safeLeft, safeRight), bottom);
    };
  }

  private int clamp(int v, int a, int b) {
    if (a > b) return v;
    return Math.max(a, Math.min(v, b));
  }
}
