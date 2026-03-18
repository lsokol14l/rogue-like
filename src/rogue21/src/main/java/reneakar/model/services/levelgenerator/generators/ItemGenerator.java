package reneakar.model.services.levelgenerator.generators;

import static reneakar.model.enums.ItemSubType.getSubtypesFor;
import static reneakar.model.enums.ItemType.*;
import static reneakar.model.utils.RogueUtils.toAbsolute;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import reneakar.model.entities.Room;
import reneakar.model.entities.Sector;
import reneakar.model.entities.items.*;
import reneakar.model.enums.CharType;
import reneakar.model.enums.ItemSubType;
import reneakar.model.enums.ItemType;
import reneakar.model.utils.RogueUtils;
import reneakar.model.valueobjects.Position;

/**
 * Генератор предметов для комнат уровня. Размещает 0-3 предмета случайного типа в каждой комнате.
 */
public class ItemGenerator {
  private final Random random = new Random();

  /**
   * Генерирует предметы во всех комнатах уровня
   *
   * @param sectors список секторов с комнатами
   * @return список всех сгенерированных предметов
   */
  public List<Item> generateItems(List<Sector> sectors) {
    List<Item> allItems = new ArrayList<>();

    for (Sector sector : sectors) {
      Room room = sector.getRoom();

      // Генерируем 0-3 предмета на комнату (из code-sample)
      int itemsCount = random.nextInt(4);

      for (int i = 0; i < itemsCount; i++) {
        Position itemPosition = RogueUtils.getRandomEmptyCellPosition(room);

        Position absItemPosition = toAbsolute(sectors, room, itemPosition);

        // Создаем случайный предмет
        Item item = createRandomItem(absItemPosition);
        allItems.add(item);
      }
    }

    return allItems;
  }

  /** Создает случайный предмет на указанной позиции */
  private Item createRandomItem(Position position) {
    // Получаем все типы предметов, кроме TREASURE
    ItemType[] allTypes = ItemType.values();
    List<ItemType> spawnableTypes = new ArrayList<>();

    for (ItemType type : allTypes) if (type != TREASURE) spawnableTypes.add(type);

    // Выбираем случайный тип из доступных
    ItemType randomType = spawnableTypes.get(random.nextInt(spawnableTypes.size()));

    return switch (randomType) {
      case ELIXIR -> createRandomElixir(position);
      case FOOD -> createRandomFood(position);
      case SCROLL -> createRandomScroll(position);
      case WEAPON -> createRandomWeapon(position);
      default -> createRandomFood(position);
    };
  }

  private Elixir createRandomElixir(Position position) {
    List<ItemSubType> elixirSubTypes = getSubtypesFor(ELIXIR);
    int type = random.nextInt(elixirSubTypes.size());
    ItemSubType subType = elixirSubTypes.get(type);

    // Выбираем случайную характеристику для усиления
    CharType[] charTypes = {CharType.MAXHEALTH, CharType.STRENGTH, CharType.AGILITY};
    CharType tempType = charTypes[type];

    return new Elixir(
        position,
        subType,
        random.nextInt(20) + 10, // tempMaxHealthEffect: 10-30
        random.nextInt(5) + 1, // tempAgilityEffect: 1-5
        random.nextInt(5) + 1, // tempStrengthEffect: 1-5
        random.nextInt(10) + 5, // duration: 5-15 ходов
        tempType // тип характеристики для усиления
        );
  }

  private Food createRandomFood(Position position) {
    List<ItemSubType> foodSubTypes = getSubtypesFor(FOOD);
    ItemSubType subType = foodSubTypes.get(random.nextInt(foodSubTypes.size()));

    int healthRestore = 0;

    switch (subType) {
      case BREAD -> healthRestore = 40;
      case MEAT -> healthRestore = 60;
      case APPLE -> healthRestore = 20;
    }

    return new Food(position, subType, healthRestore); // healthRestore: 20-60 HP
  }

  private Scroll createRandomScroll(Position position) {
    List<ItemSubType> scrollTypes = getSubtypesFor(SCROLL);
    ItemSubType subType = scrollTypes.get(random.nextInt(scrollTypes.size()));

    CharType type = CharType.MAXHEALTH;

    switch (subType) {
      case HEALTH_SCROLL -> {}

      case STRENGTH_SCROLL -> type = CharType.STRENGTH;

      case AGILITY_SCROLL -> type = CharType.AGILITY;
    }

    return new Scroll(
        position,
        subType,
        random.nextInt(20) + 10, // maxHealthEffect: 10-30
        random.nextInt(5) + 1, // agilityEffect: 1-5
        random.nextInt(5) + 1, // strengthEffect: 1-5
        type // тип характеристики для усиления
        );
  }

  private Weapon createRandomWeapon(Position position) {
    List<ItemSubType> weaponTypes = getSubtypesFor(WEAPON);
    ItemSubType subType = weaponTypes.get(random.nextInt(weaponTypes.size()));

    // TODO: Я устал немного так что потом придется доделать нормальную логику
    // Увеличения атаки в зависимости от типа нашего оружия
    return new Weapon(
        position,
        subType,
        random.nextInt(10) + 5, // strengthBonus: 5-15
        false // isEquipped
        );
  }
}
