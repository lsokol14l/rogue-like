package reneakar.model.entities.enemies.algorithms;

import reneakar.model.entities.Cell;
import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Graph {
  private final List<Cell> cells;
  private final Hashtable<Cell, ArrayList<Cell>> adjList = new Hashtable<>();
  ;

  public Graph(List<Cell> cells, Cell start, Cell end) {
    this.cells = cells;
    for (Cell cell1 : cells) {
      if (cell1.getType() == CellType.FLOOR || cell1.equals(start) || cell1.equals(end)) {
        adjList.put(cell1, new ArrayList<>());
        for (Cell cell2 : cells) {
          if ((cell2.getType() == CellType.FLOOR || cell2.equals(start) || cell2.equals(end)) && cell1.isNeighborsWith(cell2)) {
            adjList.get(cell1).add(cell2);
          }
        }
      }
    }
  }

  public record BFSResult(Hashtable<Cell, Integer> distance, Hashtable<Cell, Cell> parents) {
  }

  public BFSResult BFSFrom(Cell from) {
    Hashtable<Cell, Integer> distance = new Hashtable<>();
    Hashtable<Cell, Cell> parents = new Hashtable<>();
    for (Cell cell : cells) {
      distance.put(cell, Integer.MAX_VALUE);
      Cell first = new Cell(CellType.NULL, new Position(-1, -1));
      parents.put(cell, first);
    }
    distance.put(from, 0);
    Queue<Cell> queue = new LinkedList<>();
    queue.add(from);

    while (!queue.isEmpty()) {
      Cell current = queue.poll();
      for (Cell neighbor : adjList.get(current)) {
        if (distance.get(neighbor) > distance.get(current) + 1) {
          distance.put(neighbor, distance.get(current) + 1);
          parents.put(neighbor, current);
          queue.add(neighbor);
        }
      }
    }

    return new BFSResult(distance, parents);
  }

  public int shortestPathFromTo(Cell from, Cell to) {
    BFSResult result = BFSFrom(from);
    return result.distance.get(to);
  }

  public List<Cell> getPath(Cell from, Cell to) {
    BFSResult result = BFSFrom(from);
    //System.out.println(result.distance);
    List<Cell> path = new ArrayList<>();
    if (result.distance.get(to) == Integer.MAX_VALUE) {
      return path; // путь не найден
    }
    for (Cell at = to; at.getType() != CellType.NULL; at = result.parents.get(at)) {
      path.add(at);
    }
    Collections.reverse(path);
    return path;
  }
}

