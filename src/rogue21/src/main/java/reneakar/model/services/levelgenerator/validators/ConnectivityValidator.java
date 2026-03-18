package reneakar.model.services.levelgenerator.validators;

import java.util.*;
import reneakar.model.entities.Room;
import reneakar.model.valueobjects.RoomConnection;

/**
 * Валидатор для проверки связности графа комнат.
 * Использует алгоритм обхода в ширину (BFS) или глубину (DFS).
 */
public class ConnectivityValidator {

    /**
     * Проверяет, что все комнаты связаны между собой через коридоры.
     * 
     * @param rooms список всех комнат на уровне
     * @param connections список соединений между комнатами
     * @return true если граф связный, false иначе
     */
    public boolean isGraphConnected(List<Room> rooms, List<RoomConnection> connections) {
        if (rooms == null || rooms.isEmpty()) {
            throw new IllegalArgumentException("Список комнат не может быть пустым");
        }

        if (connections == null || connections.isEmpty()) {
            // Если одна комната - она связна сама с собой
            return rooms.size() == 1;
        }

        // Строим граф смежности
        Map<Room, List<Room>> adjacencyList = buildAdjacencyList(rooms, connections);

        // Обходим граф с помощью BFS начиная с первой комнаты
        Set<Room> visited = new HashSet<>();
        bfs(rooms.get(0), adjacencyList, visited);

        // Граф связен, если все комнаты посещены
        boolean isConnected = visited.size() == rooms.size();

        if (!isConnected) {
            System.err.println("ОШИБКА: Граф комнат не связен!");
            System.err.println("Всего комнат: " + rooms.size());
            System.err.println("Посещено комнат: " + visited.size());
            System.err.println("Непосещенные комнаты: " + (rooms.size() - visited.size()));
        }

        return isConnected;
    }

    /**
     * Строит список смежности для графа комнат
     */
    private Map<Room, List<Room>> buildAdjacencyList(List<Room> rooms, List<RoomConnection> connections) {
        Map<Room, List<Room>> adjacencyList = new HashMap<>();

        // Инициализируем пустые списки для всех комнат
        for (Room room : rooms) {
            adjacencyList.put(room, new ArrayList<>());
        }

        // Добавляем рёбра (связи между комнатами)
        for (RoomConnection connection : connections) {
            Room roomA = connection.roomA();
            Room roomB = connection.roomB();

            adjacencyList.get(roomA).add(roomB);
            adjacencyList.get(roomB).add(roomA);
        }

        return adjacencyList;
    }

    /**
     * Обход в ширину (BFS) для проверки достижимости всех комнат
     */
    private void bfs(Room start, Map<Room, List<Room>> adjacencyList, Set<Room> visited) {
        Queue<Room> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            Room current = queue.poll();

            for (Room neighbor : adjacencyList.get(current)) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }
    }

    /**
     * Проверяет валидность соединений (нет дублей, нет самосвязей)
     * 
     * @param connections список соединений
     * @return true если соединения валидны
     */
    public boolean validateConnections(List<RoomConnection> connections) {
        if (connections == null) {
            return true;
        }

        Set<String> uniqueConnections = new HashSet<>();

        for (RoomConnection connection : connections) {
            Room roomA = connection.roomA();
            Room roomB = connection.roomB();

            // Проверка на самосвязь
            if (roomA == roomB) {
                System.err.println("ОШИБКА: Комната связана сама с собой!");
                return false;
            }

            // Проверка на дубли (A-B и B-A считаются одинаковыми)
            String key = createConnectionKey(roomA, roomB);
            if (uniqueConnections.contains(key)) {
                System.err.println("ОШИБКА: Дублирующееся соединение между комнатами!");
                return false;
            }
            uniqueConnections.add(key);
        }

        return true;
    }

    /**
     * Создает уникальный ключ для соединения (порядок не важен)
     */
    private String createConnectionKey(Room roomA, Room roomB) {
        int idA = System.identityHashCode(roomA);
        int idB = System.identityHashCode(roomB);
        
        // Гарантируем, что ключ не зависит от порядка комнат
        if (idA < idB) {
            return idA + "-" + idB;
        } else {
            return idB + "-" + idA;
        }
    }

    /**
     * Полная проверка графа: связность + валидность
     */
    public boolean validateGraph(List<Room> rooms, List<RoomConnection> connections) {
        boolean connectionsValid = validateConnections(connections);
        boolean graphConnected = isGraphConnected(rooms, connections);

        return connectionsValid && graphConnected;
    }
}
