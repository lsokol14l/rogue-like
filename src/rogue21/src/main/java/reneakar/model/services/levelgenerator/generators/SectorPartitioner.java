package reneakar.model.services.levelgenerator.generators;

import java.util.ArrayList;
import java.util.List;
import reneakar.model.entities.Sector;
import reneakar.model.valueobjects.Position;

public class SectorPartitioner {
  public List<Sector> partition(int width, int height) {
    List<Sector> sectors = new ArrayList<>();

    int sectorWidth = width / 3;
    int sectorHeight = height / 3;
    int remainderX = width % 3;
    int remainderY = height % 3;

    for (int row = 0; row < 3; row++) {
      for (int col = 0; col < 3; col++) {
        int startX = col * sectorWidth;
        int startY = row * sectorHeight;

        int endX = startX + sectorWidth - 1;
        int endY = startY + sectorHeight - 1;

        if (col == 2) endX += remainderX;
        if (row == 2) endY += remainderY;

        sectors.add(new Sector(new Position(startX, startY), new Position(endX, endY)));
      }
    }
    return sectors;
  }
}
