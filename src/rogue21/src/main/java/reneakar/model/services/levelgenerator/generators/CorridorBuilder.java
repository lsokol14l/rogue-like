package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.utils.RogueUtils.getDoorWallSide;
import static reneakar.model.utils.RogueUtils.toAbsolute;

import java.util.ArrayList;
import java.util.List;
import reneakar.model.entities.Corridor;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.enums.WallSide;
import reneakar.model.valueobjects.Position;
import reneakar.model.valueobjects.RoomConnection;

public class CorridorBuilder {

  public List<Corridor> buildCorridors(List<Sector> sectors, List<RoomConnection> connections) {
    List<Corridor> result = new ArrayList<>();
    for (RoomConnection c : connections) {
      WallSide sideA = getDoorWallSide(sectors, c.roomA(), c.roomB());
      WallSide sideB = opposite(sideA);

      Position localDoorA = getDoorPosition(c.roomA(), sideA);
      Position localDoorB = getDoorPosition(c.roomB(), sideB);

      Position absDoorA = toAbsolute(sectors, c.roomA(), localDoorA);
      Position absDoorB = toAbsolute(sectors, c.roomB(), localDoorB);

      // 2) порядок поворота: если дверь слева/справа — сначала по X, если сверху/снизу — сначала по
      // Y
      boolean horizontalFirst = (sideA == WallSide.WEST || sideA == WallSide.EAST);

      result.add(new Corridor(absDoorA, absDoorB, horizontalFirst));
    }
    return result;
  }

  private WallSide opposite(WallSide s) {
    return switch (s) {
      case WEST -> WallSide.EAST;
      case EAST -> WallSide.WEST;
      case NORTH -> WallSide.SOUTH;
      case SOUTH -> WallSide.NORTH;
    };
  }

  // выбираем уже поставленную дверь НА НУЖНОЙ СТЕНЕ, иначе вычисляем
  private Position getDoorPosition(Room room, WallSide side) {
    List<Position> doorPositions = room.getDoorPositions();
    if (doorPositions.size() == 1) return doorPositions.getFirst();

    int roomStartY = room.getRelativeTopLeft().y();
    int roomStartX = room.getRelativeTopLeft().x();
    int roomEndY = room.getRelativeTopLeft().y() + room.getHeight() - 1;
    int roomEndX = room.getRelativeTopLeft().x() + room.getWidth() - 1;

    Position result = doorPositions.getFirst();

    for (Position doorPosition : doorPositions) {
      if (doorPosition.y() == roomStartY && side == WallSide.NORTH
          || doorPosition.y() == roomEndY && side == WallSide.SOUTH
          || doorPosition.x() == roomStartX && side == WallSide.WEST
          || doorPosition.x() == roomEndX && side == WallSide.EAST) {
        result = doorPosition;
        break;
      }
    }

    return result;
  }
}
