package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.utils.RogueUtils.toAbsolute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.entities.enemies.*;
import reneakar.model.enums.EnemyType;
import reneakar.model.utils.RogueUtils;
import reneakar.model.valueobjects.Position;

/**
 * Генератор врагов для комнат уровня. Размещает 1-2 врагов в каждой комнате (кроме стартовой).
 * Сложность врагов зависит от индекса комнаты и уровня подземелья.
 */
public class EnemyGenerator {
  private final Random random = new Random();

  /**
   * Генерирует врагов во всех комнатах кроме стартовой
   *
   * @param sectors список секторов с комнатами
   * @param startRoom стартовая комната (где появляется игрок)
   * @param dungeonLevel уровень подземелья (для балансировки сложности)
   * @return список всех сгенерированных врагов
   */
  public List<Enemy> generateEnemies(List<Sector> sectors, Room startRoom, int dungeonLevel) {
    List<Enemy> allEnemies = new ArrayList<>();

    for (int i = 0; i < sectors.size(); i++) {
      Sector sector = sectors.get(i);
      Room room = sector.getRoom();

      // ПРОПУСКАЕМ стартовую комнату - там игрок!
      if (room == startRoom) continue;

      // 50% шанс, что в комнате будут враги (уменьшаем количество)
      if (random.nextBoolean()) continue;

      // Генерируем 1 врага на комнату
      int enemiesCount = 1;

      for (int j = 0; j < enemiesCount; j++) {
        // Получаем случайную свободную позицию
        Position enemyPosition = RogueUtils.getRandomEmptyCellPosition(room);
        Position absEnemyPosition = toAbsolute(sectors, room, enemyPosition);
        // Определяем тип врага по сложности
        EnemyType enemyType = selectEnemyByDifficulty(dungeonLevel);

        // Создаем врага соответствующего типа
        Enemy enemy = createEnemy(enemyType, absEnemyPosition);
        allEnemies.add(enemy);
      }
    }

    return allEnemies;
  }

  /**
   * Определяет тип врага на основе индекса комнаты и уровня подземелья. Чем дальше комната и выше
   * уровень - тем сильнее враг. Логика из code-sample/rogue_sample/domain/generation/generation.c
   */
  /**
   * Выбор типа врага по сложности уровня.
   * Порядок появления (от слабых к сильным):
   * 1-2: Zombie (слабый)
   * 3-4: + Ghost (слабый, но вёрткий)
   * 5-6: + Ogre (танк)
   * 7-8: + SnakeMage (средний, может усыпить)
   * 9+:  + Vampire (сильный, снимает maxHP)
   */
  private EnemyType selectEnemyByDifficulty(int dungeonLevel) {
    Random rd = new Random();

    // Балансировка: слабые мобы появляются рано, сильные — поздно
    if (dungeonLevel <= 2) {
      return EnemyType.ZOMBIE;
    }
    
    int maxEnemyTier;
    if (dungeonLevel <= 4) {
      maxEnemyTier = 2; // Zombie, Ghost
    } else if (dungeonLevel <= 6) {
      maxEnemyTier = 3; // + Ogre
    } else if (dungeonLevel <= 8) {
      maxEnemyTier = 4; // + SnakeMage
    } else {
      maxEnemyTier = 5; // + Vampire
    }

    int enemyTier = rd.nextInt(maxEnemyTier) + 1;
    return getEnemyType(enemyTier);
  }

  /**
   * Маппинг тира на тип врага (от слабого к сильному).
   */
  private EnemyType getEnemyType(int tier) {
    return switch (tier) {
      case 2 -> EnemyType.GHOST;     // слабый, но вёрткий
      case 3 -> EnemyType.OGRE;      // танк, бьёт через раз
      case 4 -> EnemyType.SNAKEMAGE; // может усыпить
      case 5 -> EnemyType.VAMPIRE;   // самый сильный
      default -> EnemyType.ZOMBIE;   // базовый враг
    };
  }

  /** Создает конкретный экземпляр врага по типу. Маппинг EnemyType → класс врага */
  private Enemy createEnemy(EnemyType type, Position position) {
    return switch (type) {
      case ZOMBIE -> new Zombie(position);
      case VAMPIRE -> new Vampire(position);
      case GHOST -> new Ghost(position);
      case OGRE -> new Ogre(position);
      case SNAKEMAGE -> new SnakeMage(position);
    };
  }
}
