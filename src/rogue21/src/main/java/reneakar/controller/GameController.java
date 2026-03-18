package reneakar.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import reneakar.datalayer.SaveManager;
import reneakar.datalayer.Statistics;
import reneakar.model.GameSession;
import reneakar.model.entities.Cell;
import reneakar.model.entities.Level;
import reneakar.model.entities.Player;
import reneakar.model.entities.behaviors.AttackerCharacter;
import reneakar.model.entities.behaviors.MoveableCharacter;
import reneakar.model.entities.enemies.*;
import reneakar.model.entities.enemies.algorithms.Graph;
import reneakar.model.entities.items.*;
import reneakar.model.enums.CellType;
import reneakar.model.enums.Directions;
import reneakar.model.enums.EnemyType;
import reneakar.model.enums.GameState;
import reneakar.model.valueobjects.Position;

import java.util.Set;

import reneakar.model.utils.VisibilityCalculator;

public class GameController {
  private GameSession session;
  private boolean gameFinished = false;

  public GameController(GameSession session) {
    this.session = session;
  }

  public GameSession getSession() {
    return session;
  }

  public void startGame() {
    if (session != null) {
      session.setGameState(GameState.PLAYING);
      updateVisibility(session.getCurrentLevel().getGrid(), session.getPlayer().getPosition());
    }
  }

  public void nextLevel() {
    final int MAX_LEVEL = 21;

    if (session.getCurrentLevelNumber() >= MAX_LEVEL) {
      endGame(true);
      return;
    }

    session.nextLevel();
    updateVisibility(session.getCurrentLevel().getGrid(), session.getPlayer().getPosition());
    saveProgress();
  }

  public void pauseGame() {
    session.setGameState(GameState.PAUSE);
  }

  public void resumeGame() {
    session.setGameState(GameState.PLAYING);
  }

  public void finishGame() {
    if (gameFinished) {
      return; // Статистика уже сохранена
    }
    gameFinished = true;
    
    try {
      // Сохраняем финальные характеристики игрока в статистику
      Statistics stats = session.getStatistics();
      Player player = session.getPlayer();
      stats.setFinalStrength(player.getStrength());
      stats.setFinalAgility(player.getAgility());
      stats.setFinalMaxHealth(player.getMaxHealth());

      SaveManager.saveStatistics(stats);
      SaveManager.deleteCurrentSession();
    } catch (IOException e) {
      System.out.println("Ошибка сохранения статистики: " + e.getMessage());
    }
  }

  public void endGame(boolean won) {
    session.setGameState(won ? GameState.GAMEWON : GameState.GAMEOVER);
    finishGame();
  }

  private void restartGame() {
    this.session = new GameSession();
    startGame();
  }

  public void saveProgress() {
    try {
      SaveManager.saveCurrentSession(session);
      session.addLog("Прогресс сохранен!");
    } catch (IOException e) {
      session.addLog("Ошибка сохранения: " + e.getMessage());
    }
  }

  private void exitToMainMenu() {
    session.addLog("Выход в главное меню...");
    session.setGameState(GameState.MENU);
    saveProgress();
  }

  private void movePlayer(Directions direction) {
    List<Cell> cells = new ArrayList<>();
    Cell[][] grid = session.getCurrentLevel().getGrid();
    for (Cell[] row : grid) {
      cells.addAll(Arrays.asList(row));
    }

    Player player = session.getPlayer();
    Position newPosition = direction.move(player.getPosition());

    // Проверяем, есть ли враг на целевой клетке
    Enemy enemyAtTarget = findEnemyAtPosition(newPosition);
    if (enemyAtTarget != null) {
      // Атакуем врага вместо движения
      attackEnemy(player, enemyAtTarget, grid);
      return;
    }

    if (player.canMove(cells, newPosition)) {
      Cell newCell = grid[newPosition.y()][newPosition.x()];
      if (newCell.hasItem() && player.canTakeItem()) {
        Item item = newCell.getItem();
        player.getBackpack().addItem(item);
        String itemName = getItemDisplayName(item);
        session.addLog("Вы подобрали: " + itemName);
        
        // Статистика: подбор сокровища
        if (item instanceof Treasure) {
          session.getStatistics().addTreasure((Treasure) item);
        }

        newCell.setItem(null);
      }
      player.move(newPosition);
      session.getStatistics().addCellWalked(); // Статистика: пройденная клетка
    }
  }

  /** Поиск врага на заданной позиции. */
  private Enemy findEnemyAtPosition(Position position) {
    for (Enemy enemy : session.getEnemies()) {
      if (enemy.getPosition().equals(position) && enemy.getCurrentHealth() > 0) {
        return enemy;
      }
    }
    return null;
  }

  /** Атака игрока по врагу с логированием урона. */
  private void attackEnemy(Player player, Enemy enemy, Cell[][] grid) {
    int enemyHealthBefore = enemy.getCurrentHealth();
    player.attack(enemy);
    int damage = enemyHealthBefore - enemy.getCurrentHealth();

    String enemyName = getEnemyName(enemy.getType());
    if (damage > 0) {
      session.addLog("Вы нанесли " + damage + " урона " + enemyName + "!");
      session.getStatistics().addHitLanded(); // Статистика: попадание
    } else {
      session.addLog("Вы промахнулись по " + enemyName + "!");
      session.getStatistics().addHitMissed(); // Статистика: промах
    }

    // Проверяем, убит ли враг
    if (enemy.getCurrentHealth() <= 0) {
      session.addLog("Вы убили " + enemyName + "!");
      session.getStatistics().addEnemyDefeated(); // Статистика: убийство
      // Очищаем клетку врага (только occupant, тип клетки не меняем)
      Position enemyPos = enemy.getPosition();
      Treasure treasure = enemy.dropTreasure();

      if (treasure != null) {
        grid[enemyPos.y()][enemyPos.x()].setItem(treasure);
        session.addLog(enemyName + " уронил сокровище!");
      }

      grid[enemyPos.y()][enemyPos.x()].setOccupant(null);
      session.getEnemies().remove(enemy);
    }
  }

  /** Получить название врага по типу. */
  private String getEnemyName(EnemyType type) {
    return switch (type) {
      case ZOMBIE -> "Зомби";
      case VAMPIRE -> "Вампиру";
      case GHOST -> "Призраку";
      case OGRE -> "Огру";
      case SNAKEMAGE -> "Змею-магу";
    };
  }

  private void actionEnemies() {
    // Получаем нужные объекты
    Player player = session.getPlayer();
    ArrayList<Enemy> enemies =
        new ArrayList<>(session.getEnemies()); // копия для безопасной итерации
    List<Cell> cells = new ArrayList<>();
    Cell[][] grid = session.getCurrentLevel().getGrid();
    for (Cell[] row : grid) {
      cells.addAll(Arrays.asList(row));
    }

    // Рассматриваем отдельно каждого монстра
    for (Enemy enemy : enemies) {
      // Пропускаем мёртвых врагов
      if (enemy.getCurrentHealth() <= 0) continue;

      Position enemyOldPos = enemy.getPosition();
      Position playerPos = player.getPosition();

      // Проверяем, может ли враг атаковать игрока (находится рядом)
      if (enemy instanceof AttackerCharacter attacker && attacker.canAttack(player)) {
        // Враг атакует вместо движения
        int playerHealthBefore = player.getCurrentHealth();
        attacker.attack(player);
        int damage = playerHealthBefore - player.getCurrentHealth();

        String enemyName = getEnemyNameNominative(enemy.getType());
        if (damage > 0) {
          session.addLog(enemyName + " нанёс вам " + damage + " урона!");
          session.getStatistics().addHitReceived(); // Статистика: пропущенный удар
        } else {
          session.addLog(enemyName + " промахнулся!");
        }
        continue; // Враг атаковал — не двигается в этот ход
      }

      // Враг не рядом — пытаемся подойти к игроку
      Cell playerCell = null;
      Cell thisCell = null;

      for (Cell cell : cells) {
        if (cell.getPosition().equals(playerPos)) {
          playerCell = cell;
        }
        if (cell.getPosition().equals(enemy.getPosition())) {
          thisCell = cell;
        }
      }

      Position newEnemyPos = enemy.followPattern(cells); // дефолтное движение

      // Пробуем построить путь к игроку (преследование)
      Graph graph = new Graph(cells, thisCell, playerCell);
      List<Cell> path = graph.getPath(thisCell, playerCell);

      // Враг замечает игрока если путь существует и достаточно короткий
      int visionRange = enemy.getHostility() + 3; // базовый радиус обнаружения
      if (!path.isEmpty() && path.size() <= visionRange) {
        // Враг преследует игрока
        if (path.size() > 1) {
          Position nextStep = path.get(1).getPosition();
          // Проверяем, что следующая клетка НЕ является клеткой игрока
          if (!nextStep.equals(playerPos)) {
            newEnemyPos = new Position(nextStep.x(), nextStep.y());
          }
          // Если следующая клетка — игрок, остаёмся на месте (атакуем в следующий ход)
        }
      }

      // Проверяем, что новая позиция НЕ совпадает с игроком и враг может туда двигаться
      if (!newEnemyPos.equals(playerPos) && canEnemyMoveTo(enemy, cells, newEnemyPos)) {
        if (enemy instanceof MoveableCharacter moveable) {
          moveable.move(newEnemyPos);
        }

        // Обновляем сетку (только occupant, тип клетки не меняем)
        grid[enemyOldPos.y()][enemyOldPos.x()].setOccupant(null);
        grid[newEnemyPos.y()][newEnemyPos.x()].setOccupant(enemy);
      }
    }
  }

  /** Проверка, может ли враг двигаться на позицию. */
  private boolean canEnemyMoveTo(Enemy enemy, List<Cell> cells, Position position) {
    for (Cell cell : cells) {
      if (cell.getPosition().equals(position)) {
        return cell.isPassable() && cell.getOccupant() == null;
      }
    }
    return false;
  }

  /** Получить название врага в именительном падеже. */
  private String getEnemyNameNominative(EnemyType type) {
    return switch (type) {
      case ZOMBIE -> "Зомби";
      case VAMPIRE -> "Вампир";
      case GHOST -> "Призрак";
      case OGRE -> "Огр";
      case SNAKEMAGE -> "Змей-маг";
    };
  }

  private void showGameOverScreen() {
    boolean won = session.getGameState() == GameState.GAMEWON;
  }

  private void handlePauseMenuInput(char input) {
    switch (input) {
      case 'p' -> resumeGame();
      case 'r' -> restartGame();
      default -> {}
    }
  }

  private void handleGameOverInput(char input) {
    switch (input) {
      case 'r' -> restartGame();
      case 'q', 'm' -> exitToMainMenu();
      default -> showGameOverScreen();
    }
  }

  public void handleInput(char input) {
    if (session.getGameState() == GameState.PAUSE) {
      handlePauseMenuInput(input);
      return;
    }

    if (session.getGameState() == GameState.GAMEOVER
        || session.getGameState() == GameState.GAMEWON) {
      handleGameOverInput(input);
      return;
    }

    switch (input) {
      case 'w' -> movePlayer(Directions.UP);
      case 'a' -> movePlayer(Directions.LEFT);
      case 's' -> movePlayer(Directions.DOWN);
      case 'd' -> movePlayer(Directions.RIGHT);
      case 'p' -> pauseGame();
      case '^' -> session.getPlayer().selectPreviousItem();
      case 'v' -> session.getPlayer().selectNextItem();
      case 'e' -> useSelectedItem();
      case 'x' -> dropSelectedItem();
      case 'q' -> exitToMainMenu();

      default -> {}
    }

    switch (input) {
      case 'w':
      case 'a':
      case 's':
      case 'd':
        {
          actionEnemies();
          break;
        }
      default:
        {
          break;
        }
    }

    updateVisibility(session.getCurrentLevel().getGrid(), session.getPlayer().getPosition());
    checkLevelCompletion();

    if (session.getPlayer().getCurrentHealth() <= 0) {
      endGame(false);
    }
  }

  private void useSelectedItem() {
    Item item = session.getPlayer().getSelectedItem();

    if (item == null) {
      session.addLog("Инвентарь пуст!");
      return;
    }

    // Используем предмет в зависимости от типа
    switch (item.getType()) {
      case WEAPON -> {
        Weapon weapon = (Weapon) item;

        if (weapon.isEquipped()) {
          // Если оружие уже экипировано - снимаем его
          weapon.unequip(
              Arrays.stream(session.getCurrentLevel().getGrid()).flatMap(Arrays::stream).toList(),
              session.getPlayer());
          session.addLog("Снято: " + getItemDisplayName(weapon));
        } else {
          // Иначе экипируем
          session.getPlayer().equip(weapon, session.getCurrentLevel().getGrid());
          session.addLog("Экипировано: " + getItemDisplayName(weapon));
        }
      }
      case FOOD -> {
        Food food = (Food) item;
        int oldHealth = session.getPlayer().getCurrentHealth();
        food.use(session.getPlayer());
        int healthGain = session.getPlayer().getCurrentHealth() - oldHealth;
        session.addLog("Использовано: " + getItemDisplayName(food));
        if (healthGain > 0) {
          session.addLog("Здоровье восстановлено: +" + healthGain);
        }
        session.getStatistics().addFoodEaten(); // Статистика: еда
        session.getPlayer().getBackpack().removeItem(item);
      }
      case ELIXIR -> {
        Elixir elixir = (Elixir) item;
        int oldHealth = session.getPlayer().getCurrentHealth();
        int oldMaxHealth = session.getPlayer().getMaxHealth();
        int oldStrength = session.getPlayer().getStrength();
        int oldAgility = session.getPlayer().getAgility();

        elixir.use(session.getPlayer());

        session.addLog("Использовано: " + getItemDisplayName(elixir));

        // Логируем изменения
        int healthDiff = session.getPlayer().getCurrentHealth() - oldHealth;
        int maxHealthDiff = session.getPlayer().getMaxHealth() - oldMaxHealth;
        int strengthDiff = session.getPlayer().getStrength() - oldStrength;
        int agilityDiff = session.getPlayer().getAgility() - oldAgility;

        if (healthDiff > 0) session.addLog("Здоровье: +" + healthDiff);
        if (maxHealthDiff > 0) session.addLog("Макс. HP: +" + maxHealthDiff);
        if (strengthDiff > 0) session.addLog("Сила: +" + strengthDiff);
        if (agilityDiff > 0) session.addLog("Ловкость: +" + agilityDiff);

        session.getStatistics().addElixirDrunk(); // Статистика: эликсир
        session.getPlayer().getBackpack().removeItem(item);
      }
      case SCROLL -> {
        Scroll scroll = (Scroll) item;
        int oldMaxHealth = session.getPlayer().getMaxHealth();
        int oldStrength = session.getPlayer().getStrength();
        int oldAgility = session.getPlayer().getAgility();

        scroll.use(session.getPlayer());

        session.addLog("Использовано: " + getItemDisplayName(scroll));

        // Логируем изменения
        int maxHealthDiff = session.getPlayer().getMaxHealth() - oldMaxHealth;
        int strengthDiff = session.getPlayer().getStrength() - oldStrength;
        int agilityDiff = session.getPlayer().getAgility() - oldAgility;

        if (maxHealthDiff > 0) session.addLog("Макс. HP: +" + maxHealthDiff);
        if (strengthDiff > 0) session.addLog("Сила: +" + strengthDiff);
        if (agilityDiff > 0) session.addLog("Ловкость: +" + agilityDiff);

        session.getStatistics().addScrollRead(); // Статистика: свиток
        session.getPlayer().getBackpack().removeItem(item);
      }
      case TREASURE -> session.addLog("Сокровище нельзя использовать");
    }
  }

  public void checkLevelCompletion() {
    Position playerPos = session.getPlayer().getPosition();
    Cell currentCell = session.getCurrentLevel().getGrid()[playerPos.y()][playerPos.x()];

    if (currentCell.getType() == CellType.EXITLEVEL) {
      session.addLog("Вы прошли уровень!");
      nextLevel();
    }
  }

  private String getItemDisplayName(Item item) {
    return switch (item.getSubType()) {
      case COPPER_COIN -> "Медная монета";
      case GOLD_COINS -> "Золотые монеты";
      case SILVER_BARS -> "Серебряные слитки";
      case DIAMOND -> "Алмаз";
      case BREAD -> "Хлеб";
      case APPLE -> "Яблоко";
      case MEAT -> "Мясо";
      case HEALTH_ELIXIR -> "Эликсир здоровья";
      case STRENGTH_ELIXIR -> "Эликсир силы";
      case AGILITY_ELIXIR -> "Эликсир ловкости";
      case HEALTH_SCROLL -> "Свиток здоровья";
      case STRENGTH_SCROLL -> "Свиток силы";
      case AGILITY_SCROLL -> "Свиток ловкости";
      case SWORD -> "Меч";
      case DAGGER -> "Кинжал";
      case STAFF -> "Посох";
    };
  }

  private void dropSelectedItem() {
    Item item = session.getPlayer().getSelectedItem();

    if (item == null) {
      session.addLog("Нечего выбрасывать!");
      return;
    }

    Position dropPos;
    Cell[][] grid = session.getCurrentLevel().getGrid();

    if (item instanceof Weapon weapon) {
      // Для оружия используем специальный метод
      dropPos =
          weapon.dropToGround(
              Arrays.stream(grid).flatMap(Arrays::stream).toList(), session.getPlayer());

      // Оружие уже удалено из рюкзака в dropToGround()
    } else {
      // Для остальных предметов - просто ищем свободную клетку
      dropPos = findNearestFreeCell();

      if (dropPos != null) {
        // Удаляем из рюкзака
        session.getPlayer().getBackpack().removeItem(item);
      }
    }

    if (dropPos != null) {
      // Размещаем на карте
      grid[dropPos.y()][dropPos.x()].setItem(item);
      session.addLog("Выброшено: " + getItemDisplayName(item));
    } else {
      session.addLog("Нет места для выбрасывания!");
    }
  }

  private Position findNearestFreeCell() {
    Player player = session.getPlayer();
    Cell[][] grid = session.getCurrentLevel().getGrid();

    // Проверяем соседние клетки
    int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};

    for (int[] dir : directions) {
      int newX = player.getPosition().x() + dir[0];
      int newY = player.getPosition().y() + dir[1];

      if (newX >= 0 && newX < grid[0].length && newY >= 0 && newY < grid.length) {
        Cell cell = grid[newY][newX];
        if (!cell.hasItem() && cell.isPassable()) {
          return new Position(newX, newY);
        }
      }
    }

    // Если нет свободной - под игроком
    Cell playerCell = grid[player.getPosition().y()][player.getPosition().x()];
    if (!playerCell.hasItem()) {
      return player.getPosition();
    }

    return null;
  }

  private void updateVisibility(Cell[][] grid, Position playerPos) {
    for (Cell[] row : grid) {
      for (Cell cell : row) {
        cell.setVisible(false); // Сбрасываем видимость
      }
    }

    Set<Position> visiblePositions;

    if (VisibilityCalculator.isInRoom(grid, playerPos)) {
      visiblePositions = VisibilityCalculator.getRoomCells(grid, playerPos);
    } else {
      visiblePositions = VisibilityCalculator.calculateVisibleCells(grid, playerPos);
    }

    for (Position pos : visiblePositions) {
      Cell cell = grid[pos.y()][pos.x()];
      cell.setVisible(true);
      cell.setExplored(true); // Помечаем как исследованную
    }
  }
}
