package reneakar.model.services.levelgenerator.algorithms.connectors;

import java.util.List;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.RoomConnection;

public interface RoomConnector {
  List<RoomConnection> generateConnections(List<Room> rooms);
}
