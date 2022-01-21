package jossc.squidgame.map.feature.crystal;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Getter
public class CrystalSection {

  private final int index;
  private final Crystal firstCrystal;
  private final Crystal secondCrystal;
  private final List<Crystal> crystals = new ArrayList<>();

  public CrystalSection(
    int index,
    Crystal firstCrystal,
    Crystal secondCrystal
  ) {
    this.index = index;
    this.firstCrystal = firstCrystal;
    this.secondCrystal = secondCrystal;
    crystals.add(firstCrystal);
    crystals.add(secondCrystal);
  }

  @Override
  public String toString() {
    return (
      "CrystalSection{" +
      "index=" +
      index +
      ", firstCrystal=" +
      firstCrystal +
      ", secondCrystal=" +
      secondCrystal +
      '}'
    );
  }
}
