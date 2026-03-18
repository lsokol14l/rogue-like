package reneakar.model.services.levelgenerator.algorithms.connectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.Position;
import reneakar.model.valueobjects.RoomConnection;

/**
 * Builds a Minimum Spanning Tree (MST) over rooms using Prim's algorithm. Weight = Manhattan
 * distance between absolute room centers.
 */
public class PrimConnector implements RoomConnector {
  private static Position absoluteCenter(Room r) {
    // absCenter = absTopLeft + (centerRel - relTopLeft)
    Position absTL = r.getAbsoluteTopLeft();
    Position relTL = r.getRelativeTopLeft();
    Position cRel = r.getCenter();
    int dx = cRel.x() - relTL.x();
    int dy = cRel.y() - relTL.y();
    return new Position(absTL.x() + dx, absTL.y() + dy);
  }

  // манхеттеское расстояние - это
  // “минимальное число клеток”, которое нужно пройти от A до B без диагоналей.
  private static int manhattan(Position a, Position b) {
    return Math.abs(a.x() - b.x()) + Math.abs(a.y() - b.y());
  }

  @Override
  public List<RoomConnection> generateConnections(List<Room> rooms) {
    List<RoomConnection> result = new ArrayList<>();
    int n = rooms.size();
    if (n <= 1) return result;

    Position[] centers = new Position[n];
    for (int i = 0; i < n; i++) centers[i] = absoluteCenter(rooms.get(i));

    boolean[] inMST = new boolean[n];
    int[] minDist = new int[n];
    int[] parent = new int[n];
    Arrays.fill(minDist, Integer.MAX_VALUE);
    Arrays.fill(parent, -1);

    // start from 0
    minDist[0] = 0;

    for (int iter = 0; iter < n; iter++) {
      int u = -1;
      int best = Integer.MAX_VALUE;
      for (int i = 0; i < n; i++) {
        if (!inMST[i] && minDist[i] < best) {
          best = minDist[i];
          u = i;
        }
      }
      if (u == -1) break;
      inMST[u] = true;

      // relax edges u->v
      for (int v = 0; v < n; v++) {
        if (inMST[v] || v == u) continue;
        int w = manhattan(centers[u], centers[v]);
        if (w < minDist[v]) {
          minDist[v] = w;
          parent[v] = u;
        }
      }
    }

    for (int v = 1; v < n; v++) {
      if (parent[v] != -1) {
        result.add(new RoomConnection(rooms.get(parent[v]), rooms.get(v)));
      }
    }
    return result;
  }
}
