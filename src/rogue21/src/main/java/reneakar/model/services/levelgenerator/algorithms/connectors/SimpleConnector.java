package reneakar.model.services.levelgenerator.algorithms.connectors;

import java.util.ArrayList;
import java.util.List;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.RoomConnection;

public class SimpleConnector implements RoomConnector {
  @Override
  public List<RoomConnection> generateConnections(List<Room> rooms) {
    List<RoomConnection> result = new ArrayList<>();

    for (int i = 0; i < rooms.size() - 1; i++)
      result.add(new RoomConnection(rooms.get(i), rooms.get(i + 1)));

    return result;
  }
}
