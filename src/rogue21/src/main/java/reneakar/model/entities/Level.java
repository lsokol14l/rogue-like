package reneakar.model.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import reneakar.model.entities.enemies.Enemy;
import reneakar.model.entities.items.Item;
import reneakar.model.services.levelgenerator.algorithms.connectors.RoomConnector;
import reneakar.model.services.levelgenerator.generators.*;
import reneakar.model.services.levelgenerator.strategies.ConnectorFactory;
import reneakar.model.services.levelgenerator.validators.ConnectivityValidator;
import reneakar.model.valueobjects.RoomConnection;

public class Level {

  private final int WIDTH;
  private final int HEIGHT;
  private final int level;

  // поля, отвечающие за генерацию подземелья
  private ArrayList<Sector> sectors = new ArrayList<>();
  private ArrayList<Corridor> corridors = new ArrayList<>();
  // поля, отвечающие за генерацию сущностей
  private ArrayList<Enemy> enemies = new ArrayList<>();
  private ArrayList<Item> items = new ArrayList<>();
  private Player player;
  private Room startRoom;

  private Cell[][] grid; // grid[y][x]

  public Level(int WIDTH, int HEIGHT, int level) {
    // конструктор делегирует другой конструктор передавая ему KRUSKAL
    this(WIDTH, HEIGHT, level, "KRUSKAL");
  }

  // позволяет выбрать стратегию связности ("PRIM" | "KRUSKAL" | "SIMPLE")
  public Level(int WIDTH, int HEIGHT, int level, String connectorName) {
    this.WIDTH = WIDTH;
    this.HEIGHT = HEIGHT;
    this.level = level;
    buildLevel(connectorName);
  }

  public Level(int WIDTH, int HEIGHT, int level, Player player) {
    this.WIDTH = WIDTH;
    this.HEIGHT = HEIGHT;
    this.level = level;
    this.player = player;
    buildLevel("KRUSKAL");
  }

  public Player getPlayer() {
    return player;
  }

  public ArrayList<Enemy> getEnemies() {
    return enemies;
  }

  public Cell[][] getGrid() {
    return grid;
  }

  private void buildLevel(String connectorName) {
    // 1) Пустая сетка
    GridRasterizer rasterizer = new GridRasterizer();
    this.grid = rasterizer.initializeGrid(WIDTH, HEIGHT);

    // 2) Делим на секторы (каждый Sector генерирует Room локально)
    SectorPartitioner partitioner = new SectorPartitioner();
    this.sectors = new ArrayList<>(partitioner.partition(WIDTH, HEIGHT));

    // 3) Соединяем комнаты: считаем пары, ставим двери и строим коридоры
    RoomConnector connector = ConnectorFactory.createConnector(connectorName);
    DoorPositioner doorPositioner = new DoorPositioner();
    List<RoomConnection> connections = doorPositioner.placeDoors(this.sectors, connector);

    // 3.1) ПРОВЕРКА СВЯЗНОСТИ ГРАФА КОМНАТ
    List<Room> rooms = sectors.stream().map(Sector::getRoom).toList();
    ConnectivityValidator validator = new ConnectivityValidator();

    if (!validator.validateGraph(rooms, connections)) {
      throw new IllegalStateException(
              "Ошибка генерации уровня: граф комнат не связен или содержит ошибки! " +
                      "Уровень: " + level + ", Стратегия: " + connectorName
      );
    }

    CorridorBuilder corridorBuilder = new CorridorBuilder();
    this.corridors = new ArrayList<>(corridorBuilder.buildCorridors(this.sectors, connections));

    // 4) генерируем юнитов
    this.startRoom = getStartRoom(rooms);

    // 4.1 Размещаем игрока (определяет стартовую комнату)
    PlayerSpawner playerSpawner = new PlayerSpawner(player);
    this.player = playerSpawner.spawnPlayer(sectors, startRoom);

    // 4.2 Генерируем предметы (во всех комнатах)
    ItemGenerator itemGenerator = new ItemGenerator();
    this.items = new ArrayList<>(itemGenerator.generateItems(sectors));

    // 4.3 Генерируем врагов (во всех комнатах кроме стартовой)
    EnemyGenerator enemyGenerator = new EnemyGenerator();
    this.enemies = new ArrayList<>(enemyGenerator.generateEnemies(sectors, startRoom, level));

    // 5) Проецируем все в сетку
    assert this.grid != null;
    rasterizer.applyRoomsAndDoors(this.grid, this.sectors);
    rasterizer.applyCorridors(this.grid, this.corridors);
    rasterizer.applyItems(this.grid, this.items);
    rasterizer.applyEnemies(this.grid, this.enemies);

    // 6) Генерируем переход на след уровень
    ExitGenerator exitGenerator = new ExitGenerator();
    exitGenerator.generateExit(grid, sectors, getExitRoom(rooms));
  }

  private Room getExitRoom(List<Room> rooms) {
    Random rd = new Random();
    Room exitRoom;

    do {
      exitRoom = rooms.get(rd.nextInt(rooms.size())); // исправлено: было size()-1
    } while (exitRoom == startRoom);

    return exitRoom;
  }

  private Room getStartRoom(List<Room> rooms) {
    Random rd = new Random();
    return rooms.get(rd.nextInt(rooms.size())); // исправлено: было size()-1
  }
}
