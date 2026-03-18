package reneakar.presentation;

import static reneakar.presentation.screens.GameScreen.HEIGHT;
import static reneakar.presentation.screens.GameScreen.WIDTH;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;

import reneakar.model.GameSession;
import reneakar.presentation.screens.GameEndScreen;
import reneakar.presentation.screens.GameScreen;
import reneakar.presentation.screens.GoodbyeScreen;
import reneakar.presentation.screens.LeaderboardScreen;
import reneakar.presentation.screens.MainScreen;
import reneakar.presentation.screens.StartScreen;

public class Front {
  private Terminal terminal;
  private Screen screen;
  private TextGraphics tg;
  private StartScreen startScreen;
  private MainScreen mainScreen;
  private GoodbyeScreen goodbyeScreen;
  private LeaderboardScreen leaderboardScreen;
  private GameEndScreen gameEndScreen;
  private GameScreen currentScreen;

  public void startFront() {
    try {
      DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
      terminalFactory.setInitialTerminalSize(new TerminalSize(WIDTH, HEIGHT));

      terminal = terminalFactory.createTerminal();
      screen = new TerminalScreen(terminal);
      screen.startScreen();
      tg = screen.newTextGraphics();
      initScreens();

      currentScreen = startScreen;
      currentScreen.render();

    } catch (IOException e) {
      e.printStackTrace();
      // Если инициализация не удалась, пытаемся корректно закрыть
      stopFront();
    }
  }

  public void stopFront() {
    try {
      if (screen != null) {
        screen.stopScreen();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    try {
      if (terminal != null) {
        terminal.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void initScreens() {
    startScreen = new StartScreen(screen, tg);
    mainScreen = new MainScreen(screen, tg);
    goodbyeScreen = new GoodbyeScreen(screen, tg);
    leaderboardScreen = new LeaderboardScreen(screen, tg);
    gameEndScreen = new GameEndScreen(screen, tg);
  }

  public void showScreen(ScreenType screenType) {
    switch (screenType) {
      case START:
        currentScreen = startScreen;
        break;
      case MAIN:
        currentScreen = mainScreen;
        break;
      case GOODBYE:
        currentScreen = goodbyeScreen;
        break;
      case LEADERBOARD:
        currentScreen = leaderboardScreen;
        break;
      case GAME_END:
        currentScreen = gameEndScreen;
    }
    currentScreen.render();
  }

  public int waitForMenuInput() {
    try {
      com.googlecode.lanterna.input.KeyStroke keyStroke = screen.readInput();

      if (keyStroke == null) {
        return -1;
      }

      int currentSelected = startScreen.getSelectedButton();
      int buttonsCount = startScreen.getButtonsCount();

      switch (keyStroke.getKeyType()) {
        case ArrowLeft:
          currentSelected = (currentSelected - 1 + buttonsCount) % buttonsCount;
          startScreen.setSelectedButton(currentSelected);
          startScreen.render();
          return -1;

        case ArrowRight:
          currentSelected = (currentSelected + 1) % buttonsCount;
          startScreen.setSelectedButton(currentSelected);
          startScreen.render();
          return -1;

        case Enter:
          return startScreen.getSelectedButton();

        case Escape:
          return 3; // Выход

        default:
          return -1;
      }

    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

  public void updateMainScreen(GameSession session) {
    if (currentScreen instanceof MainScreen) {
      ((MainScreen) currentScreen).render(session);
    }
  }

  /**
   * Показывает таблицу лидеров с данными
   *
   * @param leaderboard список статистики для отображения
   */
  public void showLeaderboard(java.util.List<reneakar.datalayer.Statistics> leaderboard) {
    leaderboardScreen.setLeaderboard(leaderboard);
    showScreen(ScreenType.LEADERBOARD);
  }

  /**
   * Показывает экран окончания игры с статистикой
   *
   * @param statistics статистика игры
   * @param won        true если игрок победил, false если проиграл
   */
  public void showGameEnd(reneakar.datalayer.Statistics statistics, boolean won) {
    gameEndScreen.setGameEndData(statistics, won);
    showScreen(ScreenType.GAME_END);
  }

  /**
   * Ожидает ввода на экране окончания игры
   *
   * @return выбранная опция: 0 = начать заново, 1 = главное меню, -1 = ничего
   */
  public int waitForGameEndInput() {
    try {
      com.googlecode.lanterna.input.KeyStroke keyStroke = screen.readInput();

      if (keyStroke == null) {
        return -1;
      }

      switch (keyStroke.getKeyType()) {
        case ArrowUp:
          gameEndScreen.selectPrevious();
          gameEndScreen.render();
          return -1;

        case ArrowDown:
          gameEndScreen.selectNext();
          gameEndScreen.render();
          return -1;

        case Enter:
          return gameEndScreen.getSelectedOption();

        default:
          return -1;
      }

    } catch (IOException e) {
      e.printStackTrace();
      return -1;
    }
  }

  /**
   * Ожидает нажатия клавиши для выхода из информационного экрана (leaderboard, goodbye и т.д.)
   *
   * @return true если пользователь хочет выйти
   */
  public boolean waitForExit() {
    try {
      com.googlecode.lanterna.input.KeyStroke keyStroke = screen.readInput();
      // Любая клавиша возвращает true (выход из экрана)
      return keyStroke != null;
    } catch (IOException e) {
      e.printStackTrace();
      return true;
    }
  }

  /**
   * Ожидает ввод игрока и преобразует KeyStroke в символ команды
   *
   * @return символ команды (w, a, s, d, p, i, h, j, k, e, q) или '\0' при ошибке
   */
  public char waitForGameInput() {
    if (!(currentScreen instanceof MainScreen)) {
      return '\0';
    }

    MainScreen mainScreen = (MainScreen) currentScreen;
    com.googlecode.lanterna.input.KeyStroke keyStroke = mainScreen.waitForInput();

    if (keyStroke == null) {
      return '\0';
    }

    // Преобразуем KeyStroke в символ команды
    switch (keyStroke.getKeyType()) {
      case ArrowUp:
        return '^';  // символ для "предыдущий предмет"
      case ArrowDown:
        return 'v';  // символ для "следующий предмет"
      case Character:
        char ch = keyStroke.getCharacter();
        // Возвращаем символ в нижнем регистре для единообразия
        return Character.toLowerCase(ch);
      case Escape:
        return 'q'; // Escape = выход
      default:
        return '\0'; // Неизвестная клавиша
    }
  }

  public enum ScreenType {
    START, MAIN, INVENTORY, DEATH, GOODBYE, LEADERBOARD, GAME_END
  }
}
