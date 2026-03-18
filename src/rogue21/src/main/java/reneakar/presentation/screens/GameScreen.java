package reneakar.presentation.screens;

import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;


public abstract class GameScreen {
  protected static final TextColor COLOR_WHITE = new TextColor.RGB(255, 255, 255);
  protected static final TextColor COLOR_BLACK = new TextColor.RGB(0, 0, 0);
  protected static final TextColor COLOR_GREY = new TextColor.RGB(150, 150, 150);
  protected static final TextColor COLOR_PURPLE = new TextColor.RGB(255, 0, 255);
  protected static final TextColor COLOR_YELLOW = new TextColor.RGB(255, 255, 0);
  protected static final TextColor COLOR_LIGHT_GRAY = new TextColor.RGB(192, 192, 192);
  protected static final TextColor COLOR_RED = new TextColor.RGB(255, 0, 0);
  protected static final TextColor COLOR_GREEN = new TextColor.RGB(0, 255, 0);
  protected static final TextColor COLOR_BLUE = new TextColor.RGB(0, 0, 255);
  protected static final TextColor COLOR_CYAN = new TextColor.RGB(0, 255, 255);
  protected static final TextColor COLOR_ORANGE = new TextColor.RGB(255, 140, 0);

  public static final int WIDTH = 136;
  public static final int HEIGHT = 36;

  protected Screen screen;
  protected TextGraphics tg;

  public GameScreen(Screen screen, TextGraphics tg) {
    this.screen = screen;
    this.tg = tg;
  }

  public abstract void render();

  protected void printText(int x, int y, TextColor textColor, TextColor bgColor, String text) {
    tg.setForegroundColor(textColor);
    tg.setBackgroundColor(bgColor);
    tg.putString(x, y, text);
  }

  protected void printTextCentered(int y, TextColor textColor, TextColor bgColor, String text) {
    int x = (WIDTH - text.length()) / 2;
    printText(x, y, textColor, bgColor, text);
  }


  protected void drawBorder(TextColor borderColor) {
    tg.setForegroundColor(borderColor);
    tg.putString(0, 0, "┌");
    tg.putString(WIDTH - 1, 0, "┐");
    tg.putString(0, HEIGHT - 1, "└");
    tg.putString(WIDTH - 1, HEIGHT - 1, "┘");

    for (int x = 1; x < WIDTH - 1; x++) {
      tg.putString(x, 0, "─");
      tg.putString(x, HEIGHT - 1, "─");
    }

    for (int y = 1; y < HEIGHT - 1; y++) {
      tg.putString(0, y, "│");
      tg.putString(WIDTH - 1, y, "│");
    }
  }

  protected void clearScreen() {
    tg.setBackgroundColor(COLOR_BLACK);
    for (int y = 0; y < HEIGHT; y++) {
      for (int x = 0; x < WIDTH; x++) {
        tg.putString(x, y, " ");
      }
    }
  }
}
