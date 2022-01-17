package jossc.squidgame.map.feature.crystal;

import lombok.Getter;

@Getter
public class CrystalSection {

  private final int index;
  private final Crystal firstCrystal;
  private final Crystal secondCrystal;

  public CrystalSection(
    int index,
    Crystal firstCrystal,
    Crystal secondCrystal
  ) {
    this.index = index;
    this.firstCrystal = firstCrystal;
    this.secondCrystal = secondCrystal;
  }
}
