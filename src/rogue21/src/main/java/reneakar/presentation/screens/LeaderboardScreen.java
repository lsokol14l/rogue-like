package reneakar.presentation.screens;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;
import java.util.List;

import reneakar.datalayer.Statistics;

public class LeaderboardScreen extends GameScreen {
  private List<Statistics> leaderboard;

  public LeaderboardScreen(Screen screen, TextGraphics tg) {
    super(screen, tg);
  }

  public void setLeaderboard(List<Statistics> leaderboard) {
    this.leaderboard = leaderboard;
  }

  @Override
  public void render() {
    clearScreen();
    drawBorder(COLOR_CYAN);

    // Заголовок
    printTextCentered(
            2,
            COLOR_YELLOW,
            COLOR_BLACK,
            "╔═══════════════════════════════════════════════════════════════════════════╗");
    printTextCentered(
            3,
            COLOR_YELLOW,
            COLOR_BLACK,
            "║                         ТАБЛИЦА ЛИДЕРОВ                                   ║");
    printTextCentered(
            4,
            COLOR_YELLOW,
            COLOR_BLACK,
            "╚═══════════════════════════════════════════════════════════════════════════╝");

    if (leaderboard == null || leaderboard.isEmpty()) {
      printTextCentered(HEIGHT / 2, COLOR_GREY, COLOR_BLACK, "Статистика пока пуста");
      printTextCentered(
              HEIGHT - 2, COLOR_GREY, COLOR_BLACK, "Нажмите любую клавишу для возврата в меню");
    } else {
      // Заголовки колонок
      int startY = 6;
      printTextCentered(
              startY,
              COLOR_GREEN,
              COLOR_BLACK,
              "┌───┬───────┬──────┬───────┬─────┬───────┬───────┬───────┬─────────┬────────┐");
      printTextCentered(
              startY + 1,
              COLOR_GREEN,
              COLOR_BLACK,
              "│ № │Уровень│Сокров│Убийств│ Еда │Эликсир│Свитков│Попад. │Пропущен.│ Клеток │");
      printTextCentered(
              startY + 2,
              COLOR_GREEN,
              COLOR_BLACK,
              "├───┼───────┼──────┼───────┼─────┼───────┼───────┼───────┼─────────┼────────┤");

      // Данные (максимум 10 записей)
      int displayCount = Math.min(10, leaderboard.size());
      for (int i = 0; i < displayCount; i++) {
        Statistics s = leaderboard.get(i);

        // Определяем цвет в зависимости от места
        TextColor rowColor = COLOR_WHITE;
        if (i == 0) {
          rowColor = COLOR_YELLOW; // Золото для первого места
        } else if (i == 1) {
          rowColor = COLOR_LIGHT_GRAY; // Серебро для второго
        } else if (i == 2) {
          rowColor = COLOR_ORANGE; // Бронза для третьего
        }

        String row =
                String.format(
                        "│%2d │  %4d   │ %4d │  %3d  │ %3d │  %3d  │  %3d  │  %3d  │   %3d   │ %6d │",
                        i + 1,
                        s.getLevelReached(),
                        s.getTreasuresCollected(),
                        s.getEnemiesDefeated(),
                        s.getFoodEaten(),
                        s.getElixirsDrunk(),
                        s.getScrollsRead(),
                        s.getHitsLanded(),
                        s.getHitsReceived(),
                        s.getCellsWalked());

        printTextCentered(startY + 3 + i, rowColor, COLOR_BLACK, row);
      }

      // Нижняя граница таблицы
      printTextCentered(
              startY + 3 + displayCount,
              COLOR_GREEN,
              COLOR_BLACK,
              "└───┴───────┴──────┴───────┴─────┴───────┴───────┴───────┴─────────┴────────┘");

      // Дополнительная статистика внизу
      if (displayCount < 10) {
        int statsY = startY + 5 + displayCount;
        printTextCentered(
                statsY, COLOR_GREY, COLOR_BLACK, "Всего завершенных игр: " + leaderboard.size());
      }

      // Подсказка для выхода
      printTextCentered(
              HEIGHT - 2, COLOR_GREY, COLOR_BLACK, "Нажмите ESC или пробел для возврата в меню");
    }

    try {
      screen.refresh();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
