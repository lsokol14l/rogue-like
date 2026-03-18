package reneakar.datalayer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import reneakar.model.GameSession;
import reneakar.model.entities.Character;
import reneakar.model.entities.Player;
import reneakar.model.entities.enemies.*;
import reneakar.model.entities.items.*;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SaveManager {
    private static final String SAVE_DIR = "saves/";
    private static final String CURRENT_SAVE_FILE = "current_session.json";
    private static final String STATISTICS_FILE = "statistics.json";
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(reneakar.model.entities.Character.class, "characterType")
                            .registerSubtype(Player.class, "PLAYER")
                            .registerSubtype(Zombie.class, "ZOMBIE")
                            .registerSubtype(Vampire.class, "VAMPIRE")
                            .registerSubtype(Ghost.class, "GHOST")
                            .registerSubtype(Ogre.class, "OGRE")
                            .registerSubtype(SnakeMage.class, "SNAKEMAGE")
            )
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(Enemy.class, "enemyType")
                            .registerSubtype(Zombie.class, "ZOMBIE")
                            .registerSubtype(Vampire.class, "VAMPIRE")
                            .registerSubtype(Ghost.class, "GHOST")
                            .registerSubtype(Ogre.class, "OGRE")
                            .registerSubtype(SnakeMage.class, "SNAKEMAGE")
            )
            .registerTypeAdapterFactory(
                    RuntimeTypeAdapterFactory.of(Item.class, "itemClass")
                            .registerSubtype(Treasure.class, "TREASURE")
                            .registerSubtype(Food.class, "FOOD")
                            .registerSubtype(Elixir.class, "ELIXIR")
                            .registerSubtype(Scroll.class, "SCROLL")
                            .registerSubtype(Weapon.class, "WEAPON")
            )

            .create();


    static {
        try {
            Files.createDirectories(Paths.get(SAVE_DIR));
        } catch (IOException e) {
            System.err.println("Не удалось создать директорию сохранений: " + e.getMessage());
        }
    }

    public static void saveCurrentSession(GameSession session) throws IOException {
        String json = gson.toJson(session);
        Files.writeString(Path.of(SAVE_DIR + CURRENT_SAVE_FILE), json);
    }

    public static GameSession loadCurrentSession() throws IOException {
        String json = Files.readString(Path.of(SAVE_DIR + CURRENT_SAVE_FILE));
        return gson.fromJson(json, GameSession.class);
    }

    public static boolean hasCurrentSession() {
        return Files.exists(Path.of(SAVE_DIR + CURRENT_SAVE_FILE));
    }

    public static void saveStatistics(Statistics stats) throws IOException {
        List<Statistics> allStats = loadAllStatistics();
        allStats.add(stats);

        String json = gson.toJson(allStats);
        Files.writeString(Path.of(SAVE_DIR + STATISTICS_FILE), json);
    }

    public static List<Statistics> loadAllStatistics() throws IOException {
        Path path = Path.of(SAVE_DIR + STATISTICS_FILE);
        if (!Files.exists(path)) {
            return new ArrayList<>();
        }

        String json = Files.readString(path);
        Statistics[] statsArray = gson.fromJson(json, Statistics[].class);
        return statsArray != null ? new ArrayList<>(List.of(statsArray)) : new ArrayList<>();
    }

    public static List<Statistics> getLeaderboard() throws IOException {
        List<Statistics> stats = loadAllStatistics();
        stats.sort(Comparator.comparingInt(Statistics::getTreasuresCollected).reversed());
        return stats;
    }

    public static void deleteCurrentSession() {
        try {
            Files.deleteIfExists(Path.of(SAVE_DIR + CURRENT_SAVE_FILE));
        } catch (IOException e) {
            System.err.println("Не удалось удалить сохранение: " + e.getMessage());
        }
    }


}
