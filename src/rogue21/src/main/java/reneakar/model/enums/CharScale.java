package reneakar.model.enums;

/**
 * Шкала характеристик персонажей.
 * Сбалансированные значения для честного геймплея:
 * - LOW: слабый показатель
 * - MEDIUM: средний показатель  
 * - HIGH: сильный показатель
 */
public enum CharScale {
  LOW(15),
  MEDIUM(30),
  HIGH(45);

  private final int scale;

  CharScale(int scale) {
    this.scale = scale;
  }

  public int getScale() {
    return scale;
  }
}
