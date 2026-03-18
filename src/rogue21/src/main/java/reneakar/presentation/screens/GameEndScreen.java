package reneakar.presentation.screens;

import java.io.IOException;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import reneakar.datalayer.Statistics;

public class GameEndScreen extends GameScreen {
  private Statistics statistics;
  private boolean won;
  private int selectedOption = 0; // 0 = начать заново, 1 = главное меню

  public GameEndScreen(Screen screen, TextGraphics tg) {
    super(screen, tg);
  }

  public void setGameEndData(Statistics statistics, boolean won) {
    this.statistics = statistics;
    this.won = won;
    this.selectedOption = 0; // Сбрасываем выбор
  }

  public int getSelectedOption() {
    return selectedOption;
  }

  public void selectNext() {
    selectedOption = (selectedOption + 1) % 2;
  }

  public void selectPrevious() {
    selectedOption = (selectedOption - 1 + 2) % 2;
  }

  @Override
  public void render() {
    clearScreen();
    drawBorder(won ? COLOR_GREEN : COLOR_RED);

    // Заголовок
    String title = won ? "╔════════════════════════ ПОБЕДА! ════════════════════════╗"
            : "╔══════════════════ ИГРА ОКОНЧЕНА ═══════════════════╗";
    TextColor titleColor = won ? COLOR_YELLOW : COLOR_RED;
    printTextCentered(3, titleColor, COLOR_BLACK, title);

    if (won) {
      printTextCentered(4, titleColor, COLOR_BLACK,
              "╚═════════════════════════════════════════════════════════╝");

      // Кубок победителя
      printTextCentered(6, COLOR_YELLOW, COLOR_BLACK, "          ___________          ");
      printTextCentered(7, COLOR_YELLOW, COLOR_BLACK, "         '._==_==_=_.'         ");
      printTextCentered(8, COLOR_YELLOW, COLOR_BLACK, "         .-\\:      /-.         ");
      printTextCentered(9, COLOR_YELLOW, COLOR_BLACK, "        | (|:.     |) |        ");
      printTextCentered(10, COLOR_YELLOW, COLOR_BLACK, "         '-|:.     |-'         ");
      printTextCentered(11, COLOR_YELLOW, COLOR_BLACK, "           \\::.    /           ");
      printTextCentered(12, COLOR_YELLOW, COLOR_BLACK, "            '::. .'            ");
      printTextCentered(13, COLOR_ORANGE, COLOR_BLACK, "              ) (              ");
      printTextCentered(14, COLOR_ORANGE, COLOR_BLACK, "            _.' '._            ");
      printTextCentered(15, COLOR_ORANGE, COLOR_BLACK, "           '-------'           ");

      printTextCentered(17, COLOR_GREEN, COLOR_BLACK, "🎉 Поздравляем! Вы прошли все уровни! 🎉");
    } else {
      printTextCentered(4, titleColor, COLOR_BLACK,
              "╚═════════════════════════════════════════════════════════╝");

      // Гробик (как в DeathScreen)
      printTextCentered(6, COLOR_ORANGE, COLOR_BLACK, "┌───────────────────────────┐");
      printTextCentered(7, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(8, COLOR_ORANGE, COLOR_BLACK, "|      Покойся с миром      |");
      printTextCentered(9, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(10, COLOR_ORANGE, COLOR_BLACK, "|         R.I.P.            |");
      printTextCentered(11, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(12, COLOR_GREY, COLOR_BLACK, "|      Вы сражались         |");
      printTextCentered(13, COLOR_GREY, COLOR_BLACK, "|        храбро...          |");
      printTextCentered(14, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(15, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(16, COLOR_RED, COLOR_BLACK, "|    *      *      *        |");
      printTextCentered(17, COLOR_ORANGE, COLOR_BLACK, "|                           |");
      printTextCentered(18, COLOR_ORANGE, COLOR_BLACK, "└───────────────────────────┘");
      printTextCentered(19, COLOR_GREEN, COLOR_BLACK, "___\\/(\\/)/(\\/ \\\\(//)\\)\\/(//)\\\\)//(\\__");
    }

    // Статистика (сдвигаем ниже визуальных элементов)
    if (statistics != null) {
      int statsY = won ? 19 : 21; // Позиция после визуальных элементов
      printTextCentered(statsY, COLOR_CYAN, COLOR_BLACK, "═══════════════ ВАША СТАТИСТИКА ═══════════════");

      statsY += 1;
      printTextCentered(statsY++, COLOR_WHITE, COLOR_BLACK,
              String.format("Уровень: %d  |  Сокровища: %d  |  Враги: %d",
                      statistics.getLevelReached(),
                      statistics.getTreasuresCollected(),
                      statistics.getEnemiesDefeated()));

      printTextCentered(statsY++, COLOR_WHITE, COLOR_BLACK,
              String.format("Еда: %d  |  Эликсиры: %d  |  Свитки: %d",
                      statistics.getFoodEaten(),
                      statistics.getElixirsDrunk(),
                      statistics.getScrollsRead()));

      printTextCentered(statsY++, COLOR_WHITE, COLOR_BLACK,
              String.format("Попаданий: %d  |  Пропущено ударов: %d  |  Клеток: %d",
                      statistics.getHitsLanded(),
                      statistics.getHitsReceived(),
                      statistics.getCellsWalked()));

      printTextCentered(statsY++, COLOR_GREEN, COLOR_BLACK,
              String.format("Сила: %d  |  Ловкость: %d  |  Max HP: %d",
                      statistics.getFinalStrength(),
                      statistics.getFinalAgility(),
                      statistics.getFinalMaxHealth()));
    }

    // Опции выбора
    int optionsY = HEIGHT - 10;
    printTextCentered(optionsY, COLOR_ORANGE, COLOR_BLACK, "═══════════════════════════════════════════════");
    optionsY += 2;

    // Опция 1: Начать заново
    TextColor option1Color = (selectedOption == 0) ? COLOR_YELLOW : COLOR_WHITE;
    String option1Text = (selectedOption == 0) ? " ▶ Начать заново ◀" : "   Начать заново  ";
    printTextCentered(optionsY++, option1Color, COLOR_BLACK, option1Text);

    optionsY++;

    // Опция 2: Главное меню
    TextColor option2Color = (selectedOption == 1) ? COLOR_YELLOW : COLOR_WHITE;
    String option2Text = (selectedOption == 1) ? "▶ Главное меню ◀" : "  Главное меню  ";
    printTextCentered(optionsY++, option2Color, COLOR_BLACK, option2Text);

    // Подсказка
    printTextCentered(HEIGHT - 3, COLOR_GREY, COLOR_BLACK,
            "↑/↓ - выбор | Enter - подтвердить");

    try {
      screen.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
