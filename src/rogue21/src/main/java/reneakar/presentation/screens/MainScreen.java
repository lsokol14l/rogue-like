package reneakar.presentation.screens;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import reneakar.model.GameSession;
import reneakar.model.entities.Backpack;
import reneakar.model.entities.Cell;
import reneakar.model.entities.Character;
import reneakar.model.entities.Player;
import reneakar.model.entities.enemies.*;
import reneakar.model.entities.items.*;
import reneakar.model.enums.*;
import reneakar.model.utils.VisibilityCalculator;
import reneakar.model.valueobjects.Position;

public class MainScreen extends GameScreen {
  // Размеры игрового поля (фиксированные)
  private static final int GAME_WIDTH = 80;
  private static final int GAME_HEIGHT = 24;

  // Вычисляем смещения для центрирования
  private static final int OFFSET_X = (WIDTH - GAME_WIDTH) / 2;
  private static final int OFFSET_Y = (HEIGHT - GAME_HEIGHT) / 2;

  private static final int STATUS_BAR_Y = OFFSET_Y + GAME_HEIGHT + 4;

  public MainScreen(Screen screen, TextGraphics tg) {
    super(screen, tg);
  }

  @Override
  public void render() {
    clearScreen();
    printText(0, STATUS_BAR_Y, COLOR_YELLOW, COLOR_BLACK, "Уровень:");
    printText(10, STATUS_BAR_Y, COLOR_YELLOW, COLOR_BLACK, "Здоровье:");
    printText(20, STATUS_BAR_Y, COLOR_YELLOW, COLOR_BLACK, "Золото:");
    printText(30, STATUS_BAR_Y, COLOR_YELLOW, COLOR_BLACK, "Броня:");
    printText(40, STATUS_BAR_Y, COLOR_YELLOW, COLOR_BLACK, "Опыт:");
    try {
      screen.refresh();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public void printEvent(String event) {
    printText(0, 0, COLOR_GREY, COLOR_BLACK, event);
  }

  public void render(GameSession session) {
    Cell[][] grid = session.getCurrentLevel().getGrid();
    Player player = session.getPlayer();

    clearScreen();
    drawInstructions();
    drawGameLogs(session);
    drawGrid(grid, player);
    drawStatusBar(session);
    drawInventory(player.getBackpack(), player);
    if (session.getGameState() == GameState.PAUSE) drawPause(grid);

    try {
      screen.refresh();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  private void drawInventory(Backpack backpack, Player player) {
    int invX = WIDTH - OFFSET_X + 2;
    int invY = OFFSET_Y;
    int invWidth = 24;
    int invHeight = GAME_HEIGHT;

    // Рисуем рамку инвентаря
    drawInventoryBorder(invX, invY, invWidth, invHeight);

    // Заголовок
    String title = "┤ ИНВЕНТАРЬ ├";
    printText(invX + (invWidth - title.length()) / 2, invY, COLOR_CYAN, COLOR_BLACK, title);

    // Счётчик предметов
    String counter = String.format("[%d/%d]", backpack.getSize(), backpack.getCapacity());
    printText(invX + invWidth - counter.length() - 1, invY, COLOR_YELLOW, COLOR_BLACK, counter);

    List<Item> items = backpack.getItems();

    if (items.isEmpty()) {
      printText(invX + 3, invY + invHeight / 2 - 1, COLOR_GREY, COLOR_BLACK, "Рюкзак пуст...");
      printText(invX + 2, invY + invHeight / 2, COLOR_GREY, COLOR_BLACK, "Как и мой желудок");
    } else {
      int currentY = invY + 2;

      for (int i = 0; i < items.size() && currentY < invY + invHeight - 2; i++) {
        Item item = items.get(i);

        // Проверяем, выбран ли этот предмет
        boolean isSelected = (i == player.getSelectedInventoryIndex());

        // Если выбран - рисуем маркер
        if (isSelected) {
          printText(invX + 1, currentY, COLOR_YELLOW, COLOR_BLACK, "►");
        }

        // Символ и цвет предмета
        char symbol = getItemSymbol(item.getType());
        TextColor color = getItemColor(item.getType());

        // Рисуем иконку
        printText(invX + 2, currentY, color, COLOR_BLACK, String.valueOf(symbol));

        // Название типа
        String itemName = getItemName(item);
        printText(invX + 4, currentY, COLOR_WHITE, COLOR_BLACK, itemName);

        // Дополнительная информация
        String details = getItemDetails(item);
        if (!details.isEmpty()) {
          printText(invX + 3, currentY + 1, COLOR_LIGHT_GRAY, COLOR_BLACK, details);
          currentY += 3;
        } else {
          currentY += 2;
        }
      }

      // Предупреждение о переполнении
      if (backpack.isFull()) {
        printText(invX + 2, invY + invHeight - 2, COLOR_RED, COLOR_BLACK, "! ПЕРЕПОЛНЕН !");
      }
    }
  }

  private void drawInventoryBorder(int x, int y, int width, int height) {
    // Верхняя граница
    printText(x, y, COLOR_CYAN, COLOR_BLACK, "┌" + "─".repeat(width - 2) + "┐");

    // Боковые границы
    for (int i = 1; i < height - 1; i++) {
      printText(x, y + i, COLOR_CYAN, COLOR_BLACK, "│");
      printText(x + width - 1, y + i, COLOR_CYAN, COLOR_BLACK, "│");
    }

    // Нижняя граница
    printText(x, y + height - 1, COLOR_CYAN, COLOR_BLACK, "└" + "─".repeat(width - 2) + "┘");
  }

  private String getItemName(Item item) {
    return switch (item.getSubType()) {
      // Treasure subtypes
      case COPPER_COIN -> "Медные монеты";
      case GOLD_COINS -> "Золотые монеты";
      case SILVER_BARS -> "Серебряные слитки";
      case DIAMOND -> "Алмаз";

      // Food subtypes
      case BREAD -> "Хлеб";
      case APPLE -> "Яблоко";
      case MEAT -> "Мясо";

      // Elixir subtypes
      case HEALTH_ELIXIR -> "Эликсир здоровья";
      case STRENGTH_ELIXIR -> "Эликсир силы";
      case AGILITY_ELIXIR -> "Эликсир ловкости";

      // Scroll subtypes
      case HEALTH_SCROLL -> "Свиток здоровья";
      case STRENGTH_SCROLL -> "Свиток силы";
      case AGILITY_SCROLL -> "Свиток ловкости";

      // Weapon subtypes
      case SWORD -> "Меч";
      case DAGGER -> "Кинжал";
      case STAFF -> "Посох";
    };
  }

  private String getItemDetails(Item item) {
    if (item instanceof Treasure treasure) {
      return String.format("+%d золота", treasure.getValue());
    } else if (item instanceof Weapon weapon) {
      return String.format(
          "+%d сила%s", weapon.getStrengthBonus(), weapon.isEquipped() ? " [E]" : "");
    } else if (item instanceof Food food) {
      return String.format("+%d HP", food.getHealthRestore());
    } else if (item instanceof Elixir elixir) {
      return switch (elixir.getTempType()) {
        case AGILITY -> "+" + elixir.getTempAgilityEffect() + " ловк.";
        case MAXHEALTH -> "+" + elixir.getTempMaxHealthEffect() + " макс.HP";
        case STRENGTH -> "+" + elixir.getTempStrengthEffect() + " сила";
      };
    } else if (item instanceof Scroll scroll) {
      return switch (scroll.getCharType()) {
        case AGILITY -> "+" + scroll.getAgilityEffect() + " ловк.";
        case MAXHEALTH -> "+" + scroll.getMaxHealthEffect() + " макс.HP";
        case STRENGTH -> "+" + scroll.getStrengthEffect() + " сила";
      };
    }
    return "";
  }

  private void drawInstructions() {
    String instructions =
        "WASD - движение | ↑↓ - выбор | E - использовать | X - выбросить | P - пауза | Q - выход";
    printText(OFFSET_X, 4, COLOR_WHITE, COLOR_BLACK, instructions);
  }

  private void drawGameLogs(GameSession session) {
    int logX = 2;
    int logY = OFFSET_Y;
    int logWidth = OFFSET_X - 4;
    int logHeight = GAME_HEIGHT;

    // Рисуем рамку логов
    drawLogsBorder(logX, logY, logWidth, logHeight);

    // Заголовок
    String title = "┤ СОБЫТИЯ ├";
    printText(logX + (logWidth - title.length()) / 2, logY, COLOR_GREEN, COLOR_BLACK, title);

    // Отрисовываем сообщения
    List<String> logs = session.getGameLogs();
    int currentY = logY + 2;

    for (int i = 0; i < logs.size() && currentY < logY + logHeight - 2; i++) {
      String log = logs.get(i);

      // Определяем цвет в зависимости от содержания
      TextColor logColor = getLogColor(log);

      // Разбиваем длинные строки
      List<String> wrappedLines = wrapText(log, logWidth - 4);
      for (String line : wrappedLines) {
        if (currentY >= logY + logHeight - 2) break;
        printText(logX + 2, currentY, logColor, COLOR_BLACK, line);
        currentY++;
      }
      currentY++; // Пустая строка между логами
    }
  }

  private void drawLogsBorder(int x, int y, int width, int height) {
    // Верхняя граница
    printText(x, y, COLOR_GREEN, COLOR_BLACK, "┌" + "─".repeat(width - 2) + "┐");

    // Боковые границы
    for (int i = 1; i < height - 1; i++) {
      printText(x, y + i, COLOR_GREEN, COLOR_BLACK, "│");
      printText(x + width - 1, y + i, COLOR_GREEN, COLOR_BLACK, "│");
    }

    // Нижняя граница
    printText(x, y + height - 1, COLOR_GREEN, COLOR_BLACK, "└" + "─".repeat(width - 2) + "┘");
  }

  private TextColor getLogColor(String log) {
    // Урон полученный от врага — красный
    if (log.contains("нанёс вам") || log.contains("урона!") && !log.contains("Вы нанесли")) {
      return COLOR_RED;
    }
    // Урон нанесённый игроком — белый
    if (log.contains("Вы нанесли") || log.contains("Вы промахнулись")) {
      return COLOR_WHITE;
    }
    // Убийство врага — зелёный
    if (log.contains("убили") || log.contains("победили") || log.contains("Вы убили")) {
      return COLOR_GREEN;
    }
    // Подбор предметов — жёлтый
    if (log.contains("подобрали") || log.contains("получили")) {
      return COLOR_YELLOW;
    }
    // Использование предметов — голубой
    if (log.contains("использовали")
        || log.contains("увеличено")
        || log.contains("восстановлено")) {
      return COLOR_CYAN;
    }
    // Смерть — красный
    if (log.contains("умер") || log.contains("погиб")) {
      return COLOR_RED;
    }
    return COLOR_WHITE;
  }

  private List<String> wrapText(String text, int maxWidth) {
    List<String> lines = new ArrayList<>();
    if (text.length() <= maxWidth) {
      lines.add(text);
      return lines;
    }

    String[] words = text.split(" ");
    StringBuilder currentLine = new StringBuilder();

    for (String word : words) {
      if (currentLine.length() + word.length() + 1 <= maxWidth) {
        if (currentLine.length() > 0) currentLine.append(" ");
        currentLine.append(word);
      } else {
        if (currentLine.length() > 0) {
          lines.add(currentLine.toString());
          currentLine = new StringBuilder(word);
        } else {
          lines.add(word.substring(0, maxWidth));
          currentLine = new StringBuilder(word.substring(maxWidth));
        }
      }
    }

    if (currentLine.length() > 0) {
      lines.add(currentLine.toString());
    }

    return lines;
  }

  private void drawPause(Cell[][] grid) {
    final int HEIGHT = grid.length;
    final int WIDTH = grid[0].length;

    final int PAUSE_LAYOUT = WIDTH / 5;
    final int PAUSE_GAP = 4;

    for (int y = PAUSE_GAP; y < HEIGHT - PAUSE_GAP; ++y) {
      for (int x = PAUSE_LAYOUT; x < PAUSE_LAYOUT * 2; ++x) {
        printText(x + OFFSET_X, y + OFFSET_Y, COLOR_WHITE, COLOR_WHITE, String.valueOf('#'));
      }
    }

    for (int y = PAUSE_GAP; y < HEIGHT - PAUSE_GAP; ++y) {
      for (int x = PAUSE_LAYOUT * 3; x < PAUSE_LAYOUT * 4; ++x) {
        printText(x + OFFSET_X, y + OFFSET_Y, COLOR_WHITE, COLOR_WHITE, String.valueOf('#'));
      }
    }
  }

  private void drawGrid(Cell[][] grid, Player player) {
    int height = Math.min(grid.length, GAME_HEIGHT);
    int width = Math.min(grid[0].length, GAME_WIDTH);

    Position playerPos = player.getPosition();
    Set<Position> visibleCells = VisibilityCalculator.calculateVisibleCells(grid, playerPos);

    for (int y = 0; y < height; y++) {
      for (int x = 0; x < width; x++) {
        Cell cell = grid[y][x];
        Position cellPos = cell.getPosition();

        // Проверяем, находится ли игрок на этой клетке
        if (player.getPosition().equals(cellPos)) {
          printText(x + OFFSET_X, y + OFFSET_Y, COLOR_YELLOW, COLOR_BLACK, "@");
          continue;
        }

        // Проверяем видимость клетки (туман войны)
        boolean isVisible = visibleCells.contains(cellPos);
        boolean isExploredWall = cell.isExplored() && cell.getType() == CellType.WALL;
        boolean isExploredDoor = cell.getType() == CellType.DOOR && cell.isExplored();
        boolean isExploredExit = cell.getType() == CellType.EXITLEVEL && cell.isExplored();

        // Рисуем только видимые клетки или исследованные стены
        if (!isVisible && !isExploredWall && !isExploredDoor && !isExploredExit) {
          continue; // Клетка в тумане войны - не рисуем
        }

        // Определяем что отрисовывать
        char symbol = ' ';
        TextColor color = COLOR_WHITE;

        // Специальная обработка выхода (только если видим)
        if (cell.getType() == CellType.EXITLEVEL && isVisible) {
          printText(x + OFFSET_X, y + OFFSET_Y, COLOR_BLUE, COLOR_BLACK, "e");
          continue;
        }

        // Приоритет: occupant (враги) > предметы > статичные объекты
        if (cell.hasOccupant() && isVisible) {
          Character occupant = cell.getOccupant();
          if (occupant instanceof Enemy) {
            Enemy enemy = (Enemy) occupant;
            symbol = getEnemySymbol(enemy.getType());
            color = getEnemyColor(enemy.getType());
          }
        } else if (cell.hasItem() && isVisible) {
          Item item = cell.getItem();
          symbol = getItemSymbol(item.getType());
          color = getItemColor(item.getType());
        } else {
          symbol = getCellTypeSymbol(cell.getType());
          color = getCellTypeColor(cell.getType());
        }

        printText(x + OFFSET_X, y + OFFSET_Y, color, COLOR_BLACK, String.valueOf(symbol));
      }
    }
  }

  private char getEnemySymbol(EnemyType type) {
    return switch (type) {
      case ZOMBIE -> 'Z';
      case VAMPIRE -> 'V';
      case GHOST -> 'G';
      case OGRE -> 'O';
      case SNAKEMAGE -> 'S';
    };
  }

  private TextColor getEnemyColor(EnemyType type) {
    return switch (type) {
      case ZOMBIE -> COLOR_GREEN;
      case VAMPIRE -> COLOR_RED;
      case GHOST -> COLOR_WHITE;
      case OGRE -> COLOR_YELLOW;
      case SNAKEMAGE -> COLOR_PURPLE;
    };
  }

  private char getItemSymbol(ItemType type) {
    return switch (type) {
      case TREASURE -> '$';
      case FOOD -> ':';
      case ELIXIR -> '!';
      case SCROLL -> '?';
      case WEAPON -> ')';
    };
  }

  private TextColor getItemColor(ItemType type) {
    return switch (type) {
      case TREASURE -> COLOR_YELLOW;
      case FOOD -> COLOR_GREEN;
      case ELIXIR -> COLOR_CYAN;
      case SCROLL -> COLOR_LIGHT_GRAY;
      case WEAPON -> COLOR_ORANGE;
    };
  }

  private char getCellTypeSymbol(CellType type) {
    return switch (type) {
      case WALL -> '#';
      case FLOOR, ROOMFLOOR -> '.';
      case DOOR -> '+';
      case CORRIDOR -> '*';
      case EXITLEVEL -> 'e';
      default -> ' ';
    };
  }

  private TextColor getCellTypeColor(CellType type) {
    return switch (type) {
      case WALL -> COLOR_GREY;
      case FLOOR, ROOMFLOOR -> COLOR_WHITE;
      case DOOR -> new TextColor.RGB(139, 69, 19); // Коричневый
      case CORRIDOR -> COLOR_LIGHT_GRAY;
      case EXITLEVEL -> COLOR_GREEN;
      default -> COLOR_BLACK;
    };
  }

  private void drawStatusBar(GameSession session) {
    Player player = session.getPlayer();
    int levelNum = session.getCurrentLevelNumber();

    // Подсчитываем общее количество золота из сокровищ в рюкзаке
    int totalGold = 0;
    for (Item item : player.getBackpack().getItems()) {
      if (item instanceof Treasure) {
        totalGold += ((Treasure) item).getValue();
      }
    }

    Weapon currentWeapon = player.getCurrentWeapon();
    int currentWeaponBonus = currentWeapon == null ? 0 : currentWeapon.getStrengthBonus();
    // Формируем строку статуса
    String status =
        String.format(
            "Уровень: %d | HP: %d/%d | Золото: %d | Сила: %d / %d | Ловкость: %d",
            levelNum,
            player.getCurrentHealth(),
            player.getMaxHealth(),
            totalGold,
            player.getStrength(),
            currentWeaponBonus,
            player.getAgility());

    // Отрисовываем статус-бар
    printText(OFFSET_X, STATUS_BAR_Y, COLOR_WHITE, COLOR_BLACK, status);

    // Отрисовываем HP с цветом в зависимости от процента здоровья
    double healthPercent = (double) player.getCurrentHealth() / player.getMaxHealth();
    TextColor hpColor;
    if (healthPercent < 0.3) {
      hpColor = COLOR_RED;
    } else if (healthPercent < 0.6) {
      hpColor = COLOR_YELLOW;
    } else {
      hpColor = COLOR_GREEN;
    }

    String hpText = String.format("HP: %d/%d", player.getCurrentHealth(), player.getMaxHealth());
    printText(OFFSET_X + 13, STATUS_BAR_Y, hpColor, COLOR_BLACK, hpText);
  }

  /**
   * Ожидает ввод с клавиатуры и возвращает нажатую клавишу
   *
   * @return KeyStroke или null при ошибке
   */
  public KeyStroke waitForInput() {
    try {
      return screen.readInput();
    } catch (IOException e) {
      System.out.println("Ошибка чтения ввода: " + e.getMessage());
      return null;
    }
  }
}
