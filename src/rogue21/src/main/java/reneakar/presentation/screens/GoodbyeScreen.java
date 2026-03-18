package reneakar.presentation.screens;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class GoodbyeScreen extends GameScreen {

  private static final String[] ROGUE_ASCII = {
          "          _____                   _______                   _____                    _____                    _____          ",
          "         /\\    \\                 /::\\    \\                 /\\    \\                  /\\    \\                  /\\    \\         ",
          "        /::\\    \\               /::::\\    \\               /::\\    \\                /::\\____\\                /::\\    \\        ",
          "       /::::\\    \\             /::::::\\    \\             /::::\\    \\              /:::/    /               /::::\\    \\       ",
          "      /::::::\\    \\           /::::::::\\    \\           /::::::\\    \\            /:::/    /               /::::::\\    \\      ",
          "     /:::/\\:::\\    \\         /:::/~~\\:::\\    \\         /:::/\\:::\\    \\          /:::/    /               /:::/\\:::\\    \\     ",
          "    /:::/__\\:::\\    \\       /:::/    \\:::\\    \\       /:::/  \\:::\\    \\        /:::/    /               /:::/__\\:::\\    \\    ",
          "   /::::\\   \\:::\\    \\     /:::/    / \\:::\\    \\     /:::/    \\:::\\    \\      /:::/    /               /::::\\   \\:::\\    \\   ",
          "  /::::::\\   \\:::\\    \\   /:::/____/   \\:::\\____\\   /:::/    / \\:::\\    \\    /:::/    /      _____    /::::::\\   \\:::\\    \\  ",
          " /:::/\\:::\\   \\:::\\____\\ |:::|    |     |:::|    | /:::/    /   \\:::\\ ___\\  /:::/____/      /\\    \\  /:::/\\:::\\   \\:::\\    \\ ",
          "/:::/  \\:::\\   \\:::|    ||:::|____|     |:::|    |/:::/____/  ___\\:::|    ||:::|    /      /::\\____\\/:::/__\\:::\\   \\:::\\____\\",
          "\\::/   |::::\\  /:::|____| \\:::\\    \\   /:::/    / \\:::\\    \\ /\\  /:::|____||:::|____\\     /:::/    /\\:::\\   \\:::\\   \\::/    /",
          " \\/____|:::::\\/:::/    /   \\:::\\    \\ /:::/    /   \\:::\\    /::\\ \\::/    /  \\:::\\    \\   /:::/    /  \\:::\\   \\:::\\   \\/____/ ",
          "       |:::::::::/    /     \\:::\\    /:::/    /     \\:::\\   \\:::\\ \\/____/    \\:::\\    \\ /:::/    /    \\:::\\   \\:::\\    \\     ",
          "       |::|\\::::/    /       \\:::\\__/:::/    /       \\:::\\   \\:::\\____\\       \\:::\\    /:::/    /      \\:::\\   \\:::\\____\\    ",
          "       |::| \\::/____/         \\::::::::/    /         \\:::\\  /:::/    /        \\:::\\__/:::/    /        \\:::\\   \\::/    /    ",
          "       |::|  ~|                \\::::::/    /           \\:::\\/:::/    /          \\::::::::/    /          \\:::\\   \\/____/     ",
          "       |::|   |                 \\::::/    /             \\::::::/    /            \\::::::/    /            \\:::\\    \\         ",
          "       \\::|   |                  \\::/____/               \\::::/    /              \\::::/    /              \\:::\\____\\        ",
          "        \\:|   |                   ~~                      \\::/____/                \\::/____/                \\::/    /        ",
          "         \\|___|                                                                     ~~                       \\/____/         "
  };

  public GoodbyeScreen(Screen screen, TextGraphics tg) {
    super(screen, tg);
  }

  @Override
  public void render() {
    clearScreen();

    // Получаем имя пользователя из системы
    String username = System.getProperty("user.name");

    // Вычисляем начальную позицию для центрирования ASCII-арта
    int artWidth = ROGUE_ASCII[0].length();
    int artHeight = ROGUE_ASCII.length;
    int startX = (WIDTH - artWidth) / 2;
    int startY = (HEIGHT - artHeight) / 2 - 3; // Сдвигаем вверх для сообщения внизу

    // Рисуем ASCII-арт ROGUE
    tg.setForegroundColor(COLOR_GREEN);
    for (int i = 0; i < artHeight; i++) {
      if (startY + i >= 0 && startY + i < HEIGHT) {
        String line = ROGUE_ASCII[i];
        // Обрезаем строку если она выходит за границы
        int drawX = Math.max(0, startX);
        int skipChars = drawX - startX;
        if (skipChars < line.length()) {
          String visiblePart = line.substring(skipChars, Math.min(line.length(), skipChars + WIDTH - drawX));
          printText(drawX, startY + i, COLOR_GREEN, COLOR_BLACK, visiblePart);
        }
      }
    }

    // Рисуем прощальное сообщение снизу
    String farewell = "Хорошего дня, " + username + "!";
    int farewellX = (WIDTH - farewell.length()) / 2;
    int farewellY = startY + artHeight + 2;

    if (farewellY < HEIGHT) {
      printText(farewellX, farewellY, COLOR_YELLOW, COLOR_BLACK, farewell);
    }

    // Обновляем экран
    try {
      screen.refresh();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
