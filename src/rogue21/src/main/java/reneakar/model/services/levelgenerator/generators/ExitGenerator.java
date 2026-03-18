package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.utils.RogueUtils.getRandomEmptyCellPosition;
import static reneakar.model.utils.RogueUtils.toAbsolute;

import java.util.List;
import reneakar.model.entities.Cell;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.enums.CellType;
import reneakar.model.valueobjects.Position;

public class ExitGenerator {
  public void generateExit(Cell[][] grid, List<Sector> sectors, Room exitRoom) {
    Position exitPos = getRandomEmptyCellPosition(exitRoom);
    Position absExitPos = toAbsolute(sectors, exitRoom, exitPos);
    int y = absExitPos.y();
    int x = absExitPos.x();
    grid[y][x].setType(CellType.EXITLEVEL);
  }
}
