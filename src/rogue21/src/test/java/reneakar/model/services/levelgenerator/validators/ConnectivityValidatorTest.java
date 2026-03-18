package reneakar.model.services.levelgenerator.validators;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.Position;
import reneakar.model.valueobjects.RoomConnection;

/** Тесты для проверки валидатора связности графа комнат */
class ConnectivityValidatorTest {

  private ConnectivityValidator validator;

  @BeforeEach
  void setUp() {
    validator = new ConnectivityValidator();
  }

  /** Тест: Граф из одной комнаты связен */
  @Test
  void testSingleRoomIsConnected() {
    Room room = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    List<Room> rooms = List.of(room);
    List<RoomConnection> connections = new ArrayList<>();

    assertTrue(validator.isGraphConnected(rooms, connections), "Одна комната должна быть связной");
  }

  /** Тест: Две комнаты с одним соединением связны */
  @Test
  void testTwoRoomsConnected() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);

    List<Room> rooms = List.of(room1, room2);
    List<RoomConnection> connections = List.of(new RoomConnection(room1, room2));

    assertTrue(
        validator.isGraphConnected(rooms, connections),
        "Две соединенные комнаты должны быть связными");
  }

  /** Тест: Три комнаты в линию связны */
  @Test
  void testThreeRoomsInLine() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);
    Room room3 = new Room(new Position(0, 0), new Position(40, 0), 10, 10);

    List<Room> rooms = List.of(room1, room2, room3);
    List<RoomConnection> connections =
        List.of(new RoomConnection(room1, room2), new RoomConnection(room2, room3));

    assertTrue(
        validator.isGraphConnected(rooms, connections), "Три комнаты в линию должны быть связными");
  }

  /** Тест: Несвязный граф (две изолированные группы) */
  @Test
  void testDisconnectedGraph() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);
    Room room3 = new Room(new Position(0, 0), new Position(40, 0), 10, 10);
    Room room4 = new Room(new Position(0, 0), new Position(60, 0), 10, 10);

    List<Room> rooms = List.of(room1, room2, room3, room4);
    // room1-room2 связаны, room3-room4 связаны, но нет связи между группами
    List<RoomConnection> connections =
        List.of(new RoomConnection(room1, room2), new RoomConnection(room3, room4));

    assertFalse(
        validator.isGraphConnected(rooms, connections),
        "Граф с изолированными группами не должен быть связным");
  }

  /** Тест: Граф в форме треугольника (цикл) */
  @Test
  void testTriangleGraph() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);
    Room room3 = new Room(new Position(0, 0), new Position(10, 20), 10, 10);

    List<Room> rooms = List.of(room1, room2, room3);
    List<RoomConnection> connections =
        List.of(
            new RoomConnection(room1, room2),
            new RoomConnection(room2, room3),
            new RoomConnection(room3, room1));

    assertTrue(
        validator.isGraphConnected(rooms, connections), "Треугольный граф должен быть связным");
  }

  /** Тест: Валидация соединений - нет самосвязей */
  @Test
  void testNoSelfLoop() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);

    List<RoomConnection> connections =
        List.of(
            new RoomConnection(room1, room1) // Самосвязь!
            );

    assertFalse(
        validator.validateConnections(connections),
        "Соединение комнаты с самой собой должно быть невалидным");
  }

  /** Тест: Валидация соединений - нет дублей */
  @Test
  void testNoDuplicateConnections() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);

    List<RoomConnection> connections =
        List.of(
            new RoomConnection(room1, room2), new RoomConnection(room1, room2) // Дубль!
            );

    assertFalse(
        validator.validateConnections(connections),
        "Дублирующиеся соединения должны быть невалидными");
  }

  /** Тест: Полная валидация - связный граф без ошибок */
  @Test
  void testFullValidationSuccess() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);
    Room room3 = new Room(new Position(0, 0), new Position(40, 0), 10, 10);

    List<Room> rooms = List.of(room1, room2, room3);
    List<RoomConnection> connections =
        List.of(new RoomConnection(room1, room2), new RoomConnection(room2, room3));

    assertTrue(
        validator.validateGraph(rooms, connections),
        "Валидный связный граф должен проходить полную проверку");
  }

  /** Тест: Полная валидация - несвязный граф */
  @Test
  void testFullValidationFailDisconnected() {
    Room room1 = new Room(new Position(0, 0), new Position(0, 0), 10, 10);
    Room room2 = new Room(new Position(0, 0), new Position(20, 0), 10, 10);

    List<Room> rooms = List.of(room1, room2);
    List<RoomConnection> connections = new ArrayList<>(); // Нет соединений!

    assertFalse(
        validator.validateGraph(rooms, connections),
        "Несвязный граф не должен проходить полную проверку");
  }

  /** Тест: Пустой список комнат выбрасывает исключение */
  @Test
  void testEmptyRoomsThrowsException() {
    List<Room> rooms = new ArrayList<>();
    List<RoomConnection> connections = new ArrayList<>();

    assertThrows(
        IllegalArgumentException.class,
        () -> validator.isGraphConnected(rooms, connections),
        "Пустой список комнат должен выбрасывать исключение");
  }

  /** Тест: Граф из 9 комнат (как в задании) с MST связен */
  @Test
  void testNineRoomsMSTGraph() {
    // Создаем 9 комнат (сетка 3x3)
    List<Room> rooms = new ArrayList<>();
    for (int i = 0; i < 9; i++) {
      int x = (i % 3) * 20;
      int y = (i / 3) * 20;
      rooms.add(new Room(new Position(0, 0), new Position(x, y), 10, 10));
    }

    // Создаем минимальное остовное дерево (8 соединений для 9 комнат)
    List<RoomConnection> connections =
        List.of(
            new RoomConnection(rooms.get(0), rooms.get(1)),
            new RoomConnection(rooms.get(1), rooms.get(2)),
            new RoomConnection(rooms.get(0), rooms.get(3)),
            new RoomConnection(rooms.get(3), rooms.get(4)),
            new RoomConnection(rooms.get(4), rooms.get(5)),
            new RoomConnection(rooms.get(3), rooms.get(6)),
            new RoomConnection(rooms.get(6), rooms.get(7)),
            new RoomConnection(rooms.get(7), rooms.get(8)));

    assertTrue(
        validator.validateGraph(rooms, connections), "Граф из 9 комнат с MST должен быть связным");
  }
}
