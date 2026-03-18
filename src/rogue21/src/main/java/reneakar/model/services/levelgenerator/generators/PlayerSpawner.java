package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.utils.RogueUtils.getRandomEmptyCellPosition;
import static reneakar.model.utils.RogueUtils.toAbsolute;

import java.util.List;
import reneakar.model.entities.Player;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.valueobjects.Position;

public class PlayerSpawner {
  private Player player;

  public PlayerSpawner(Player player) {
    this.player = player;
  }

  public Player spawnPlayer(List<Sector> sectors, Room startRoom) {
    Position spawnPosition = getRandomEmptyCellPosition(startRoom);

    Position absSpawnPosition = toAbsolute(sectors, startRoom, spawnPosition);

    if (player == null) {
      // maxHealth=150, currentHealth=150, agility=35, strength=30
      return new Player(absSpawnPosition, 150, 150, 35, 30);
    } else {
      player.setPosition(absSpawnPosition);
      return player;
    }
  }
}
