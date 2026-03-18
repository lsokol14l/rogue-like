package reneakar.controller;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;
import reneakar.datalayer.SaveManager;
import reneakar.datalayer.Statistics;
import reneakar.model.GameSession;
import reneakar.model.enums.GameState;
import reneakar.presentation.Front;

public class MenuController {
  private final Scanner scanner = new Scanner(System.in); // Используется только для консольных меню
  private GameSession session;
  private boolean running = true;
  private Front front;

  public MenuController() {
    this.session = null;
    this.front = new Front();
  }

  public void showMainMenu() {
    front.showScreen(Front.ScreenType.START);

    while (running) {
      int selectedOption = front.waitForMenuInput();

      if (selectedOption >= 0) {
        handleMainMenuSelection(selectedOption);
      }
    }
  }

  private void handleMainMenuSelection(int option) {
    switch (option) {
      case 0 -> startNewGame();
      case 1 -> loadGame();
      case 2 -> showLeaderboard();
      case 3 -> exitGame();
      default -> {}
    }
  }

  private void startNewGame() {
    this.session = new GameSession();

    GameController gameController = new GameController(session);

    runGameLoop(gameController);

    // Возвращаемся в главное меню после выхода из игры
    front.showScreen(Front.ScreenType.START);
  }

  private void loadGame() {
    System.out.println("=== ЗАГРУЗКА ИГРЫ ===");

    if (!SaveManager.hasCurrentSession()) {
      System.out.println("Нет сохраненной игры.");
      System.out.println();
      return;
    }

    try {
      this.session = SaveManager.loadCurrentSession();
      GameController gameController = new GameController(session);
      System.out.println("Игра загружена! Уровень: " + session.getCurrentLevelNumber());
      System.out.println();

      runGameLoop(gameController);

      // Возвращаемся в главное меню после выхода из игры
      front.showScreen(Front.ScreenType.START);

    } catch (IOException e) {
      System.out.println("Ошибка загрузки: " + e.getMessage());
      System.out.println();
    }
  }

  private void showLeaderboard() {
    try {
      List<Statistics> leaderboard = SaveManager.getLeaderboard();
      
      // Показываем экран с таблицей лидеров
      front.showLeaderboard(leaderboard);
      
      // Ожидаем нажатия клавиши для выхода
      front.waitForExit();
      
      // Возвращаемся в главное меню
      front.showScreen(Front.ScreenType.START);
      
    } catch (IOException e) {
      System.out.println("Ошибка загрузки статистики: " + e.getMessage());
    }
  }

  private void runGameLoop(GameController gameController) {
    front.showScreen(Front.ScreenType.MAIN);
    gameController.startGame();

    // Первоначальная отрисовка игрового поля
    front.updateMainScreen(gameController.getSession());

    while (gameController.getSession().getGameState() == GameState.PLAYING
        || gameController.getSession().getGameState() == GameState.PAUSE) {

      // Ожидаем ввод через Lantern
      char input = front.waitForGameInput();

      if (input != '\0') {
        gameController.handleInput(input);

        // Обновляем экран после каждого действия игрока
        front.updateMainScreen(gameController.getSession());
      }

      if (gameController.getSession().getPlayer().getCurrentHealth() <= 0) {
        gameController.endGame(false);
        break;
      }
    }

    if (gameController.getSession().getGameState() == GameState.GAMEOVER
        || gameController.getSession().getGameState() == GameState.GAMEWON) {
      handleGameEnd(gameController);
    }
  }

  private void handleGameEnd(GameController gameController) {
    boolean won = gameController.getSession().getGameState() == GameState.GAMEWON;
    Statistics stats = gameController.getSession().getStatistics();

    // Показываем экран окончания игры
    front.showGameEnd(stats, won);

    // Ожидаем выбора пользователя
    while (true) {
      int choice = front.waitForGameEndInput();

      if (choice == 0) {
        // Начать заново
        startNewGame();
        break;
      } else if (choice == 1) {
        // Главное меню
        front.showScreen(Front.ScreenType.START);
        break;
      }
      // Если -1, продолжаем ждать ввода
    }
  }

  private void exitGame() {
    // Сохраняем прогресс если есть активная сессия
    if (session != null
        && (session.getGameState() == GameState.PLAYING
            || session.getGameState() == GameState.PAUSE)) {
      try {
        SaveManager.saveCurrentSession(session);
        System.out.println("Прогресс сохранен.");
      } catch (IOException e) {
        System.out.println("Не удалось сохранить прогресс: " + e.getMessage());
      }
    }

    // Показываем прощальный экран
    front.showScreen(Front.ScreenType.GOODBYE);

    try {
      // Даем пользователю время посмотреть на прощальное сообщение
      Thread.sleep(2500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }

    running = false;
    scanner.close();
    front.stopFront();
  }

  // debug
  public void run() {
    front.startFront();
    showMainMenu();
  }
}
