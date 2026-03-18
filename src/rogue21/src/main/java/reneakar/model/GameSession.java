package reneakar.model;

import reneakar.datalayer.Statistics;
import reneakar.model.entities.Level;
import reneakar.model.entities.Player;
import reneakar.model.entities.enemies.Enemy;
import reneakar.model.enums.GameState;

import java.util.ArrayList;
import java.util.List;

public class GameSession {
  private final Player player;
  private ArrayList<Enemy> enemies; // не final, чтобы обновлять при переходе на уровень
  private Level currentLevel;
  private int currentLevelNumber;
  private GameState gameState;
  private Statistics statistics;
  private final List<String> gameLogs;
  private static final int MAX_LOGS = 10;

  public GameSession() {
    this.currentLevelNumber = 1;
    this.currentLevel = new Level(80, 24, currentLevelNumber);
    this.player = currentLevel.getPlayer();
    this.enemies = currentLevel.getEnemies();
    this.gameState = GameState.PLAYING;
    this.statistics = new Statistics();
    this.gameLogs = new ArrayList<>();
    addLog("Игра началась! Удачи!");
  }

  public Level getCurrentLevel() {
    return currentLevel;
  }

  public Player getPlayer() {
    return player;
  }

  public int getCurrentLevelNumber() {
    return currentLevelNumber;
  }

  public GameState getGameState() {
    return gameState;
  }

  public void setGameState(GameState state) {
    this.gameState = state;
  }

  public Statistics getStatistics() {
    return statistics;
  }

  public void nextLevel() {
    currentLevelNumber++;
    currentLevel = new Level(80, 24, currentLevelNumber, player);
    enemies = currentLevel.getEnemies(); // обновляем список врагов для нового уровня
    statistics.setLevelReached(currentLevelNumber);
    addLog("Вы перешли на уровень " + currentLevelNumber);
  }

  public void addLog(String message) {
    gameLogs.add(0, message); // Добавляем в начало списка
    if (gameLogs.size() > MAX_LOGS) {
      gameLogs.remove(gameLogs.size() - 1); // Удаляем самый старый
    }
  }

  public ArrayList<Enemy> getEnemies() {
    return enemies;
  }

  public List<String> getGameLogs() {
    return new ArrayList<>(gameLogs);
  }


}
