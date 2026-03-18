package reneakar.model.services.levelgenerator.strategies;

import reneakar.model.services.levelgenerator.algorithms.connectors.KruskalConnector;
import reneakar.model.services.levelgenerator.algorithms.connectors.PrimConnector;
import reneakar.model.services.levelgenerator.algorithms.connectors.RoomConnector;
import reneakar.model.services.levelgenerator.algorithms.connectors.SimpleConnector;

public class ConnectorFactory {
  public static RoomConnector createConnector(String algorithm) {
    return switch (algorithm.toUpperCase()) {
      case "PRIM" -> new PrimConnector();
      case "KRUSKAL" -> new KruskalConnector();
      default -> new SimpleConnector();
    };
  }
}
