package reneakar.presentation.screens;

import java.io.IOException;

import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;

public class StartScreen extends GameScreen {

  private final String[] menuButtons = {
          "Новая игра",
          "Продолжить игру",
          "Таблица лидеров",
          "Выйти"
  };
  private int selectedButton = 0;

  public StartScreen(Screen screen, TextGraphics tg) {
    super(screen, tg);
  }

  public void setSelectedButton(int index) {
    if (index >= 0 && index < menuButtons.length) {
      this.selectedButton = index;
    }
  }

  public int getSelectedButton() {
    return selectedButton;
  }

  public int getButtonsCount() {
    return menuButtons.length;
  }

  @Override
  public void render() {
    clearScreen();
    drawBorder(COLOR_ORANGE);

    int buttonsY = HEIGHT - 2;
    int borderY = buttonsY - 1;
    tg.setForegroundColor(COLOR_ORANGE);
    for (int x = 1; x < WIDTH - 1; x++) {
      tg.putString(x, borderY, "─");
    }

    int totalButtons = menuButtons.length;
    int[] buttonWidths = new int[totalButtons];
    int totalTextLen = 0;
    for (int i = 0; i < totalButtons; i++) {
      String text = (i == selectedButton) ? menuButtons[i] + " ←" : menuButtons[i] + "  ";
      buttonWidths[i] = text.length();
      totalTextLen += buttonWidths[i];
    }
    int spaces = WIDTH - totalTextLen;
    int gap = spaces / (totalButtons + 1);
    int x = gap;
    for (int i = 0; i < totalButtons; i++) {
      String text = (i == selectedButton) ? menuButtons[i] + " ←" : menuButtons[i] + "  ";
      com.googlecode.lanterna.TextColor textColor = (i == selectedButton) ? COLOR_YELLOW : COLOR_WHITE;
      com.googlecode.lanterna.TextColor bgColor = COLOR_BLACK;
      printText(x, buttonsY, textColor, bgColor, text);
      x += buttonWidths[i] + gap;
    }
    printTextCentered(2, COLOR_BLACK, COLOR_GREY, "Rogue21: Какая-то шляпа, а не игра");
    printTextCentered(4, COLOR_PURPLE, COLOR_BLACK, "Кого винить в этой трагедии:");
    printTextCentered(6, COLOR_WHITE, COLOR_BLACK, "reneakar");
    printTextCentered(7, COLOR_LIGHT_GRAY, COLOR_BLACK, "и");
    printTextCentered(8, COLOR_WHITE, COLOR_BLACK, "boilbrit");
    printTextCentered(9, COLOR_LIGHT_GRAY, COLOR_BLACK, "и");
    printTextCentered(10, COLOR_WHITE, COLOR_BLACK, "spinnetc");
    printTextCentered(12, COLOR_PURPLE, COLOR_BLACK, "Счастлив что удалил jcurses:");
    printTextCentered(14, COLOR_WHITE, COLOR_BLACK, "tamarilt");
    printTextCentered(16, COLOR_YELLOW, COLOR_BLACK, "(Java) Copyright chatGPT");
    printTextCentered(17, COLOR_WHITE, COLOR_BLACK, "Название команды в разработке");
    printTextCentered(18, COLOR_YELLOW, COLOR_BLACK, "Даже стартовый экран украли");


    try {
      screen.refresh();
    } catch (IOException e) {
      System.out.println(e);
    }
  }
}
