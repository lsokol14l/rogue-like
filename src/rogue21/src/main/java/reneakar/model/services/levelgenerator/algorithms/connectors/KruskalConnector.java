package reneakar.model.services.levelgenerator.algorithms.connectors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.Position;
import reneakar.model.valueobjects.RoomConnection;

/**
 * Builds a Minimum Spanning Tree (MST) over rooms using Kruskal's algorithm. Weight = Manhattan
 * distance between absolute room centers.
 */
public class KruskalConnector implements RoomConnector {
  private static Position absoluteCenter(Room r) {
    Position absTL = r.getAbsoluteTopLeft();
    Position relTL = r.getRelativeTopLeft();
    Position cRel = r.getCenter();
    int dx = cRel.x() - relTL.x();
    int dy = cRel.y() - relTL.y();
    return new Position(absTL.x() + dx, absTL.y() + dy);
  }

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

    List<Edge> edges = new ArrayList<>();
    for (int i = 0; i < n; i++) {
      for (int j = i + 1; j < n; j++) {
        int w = manhattan(centers[i], centers[j]);
        edges.add(new Edge(i, j, w));
      }
    }
    Collections.sort(edges, Comparator.comparingInt(e -> e.w));

    DSU dsu = new DSU(n);
    for (Edge e : edges) {
      if (dsu.union(e.u, e.v)) {
        result.add(new RoomConnection(rooms.get(e.u), rooms.get(e.v)));
        if (result.size() == n - 1) break;
      }
    }

    return result;
  }

  private static class Edge {
    final int u, v, w;

    Edge(int u, int v, int w) {
      this.u = u;
      this.v = v;
      this.w = w;
    }
  }

  private static class DSU {
    final int[] p, r;

    DSU(int n) {
      p = new int[n];
      r = new int[n];
      for (int i = 0; i < n; i++) {
        p[i] = i;
        r[i] = 0;
      }
    }

    int find(int x) {
      return p[x] == x ? x : (p[x] = find(p[x]));
    }

    boolean union(int a, int b) {
      a = find(a);
      b = find(b);
      if (a == b) return false;
      if (r[a] < r[b]) {
        int t = a;
        a = b;
        b = t;
      }
      p[b] = a;
      if (r[a] == r[b]) r[a]++;
      return true;
    }
  }
}
