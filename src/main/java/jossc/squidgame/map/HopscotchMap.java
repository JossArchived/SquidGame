package jossc.squidgame.map;

import cn.nukkit.math.Vector3;
import java.util.List;
import jossc.squidgame.map.crystal.Crystal;
import jossc.squidgame.map.crystal.CrystalSection;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;

@Getter
@Setter
public class HopscotchMap extends GameMap {

  private final List<CrystalSection> crystalSections;
  private final Vector3 goalCornerOne;
  private final Vector3 goalCornerTwo;

  public HopscotchMap(
    Game game,
    String name,
    Vector3 safeSpawn,
    List<CrystalSection> crystalSections,
    Vector3 goalCornerOne,
    Vector3 goalCornerTwo
  ) {
    super(game, name, safeSpawn);
    this.crystalSections = crystalSections;
    this.goalCornerOne = goalCornerOne;
    this.goalCornerTwo = goalCornerTwo;
  }

  public boolean isTheGoal(Vector3 vector3) {
    if (goalCornerOne == null || goalCornerTwo == null) {
      return true;
    }

    int minX = (int) Math.min(goalCornerOne.x, goalCornerTwo.x);
    int maxX = (int) Math.max(goalCornerOne.x, goalCornerTwo.x);

    int minZ = (int) Math.min(goalCornerOne.z, goalCornerTwo.z);
    int maxZ = (int) Math.max(goalCornerOne.z, goalCornerTwo.z);

    return (
      minX <= vector3.x &&
      maxX >= vector3.x &&
      minZ <= vector3.z &&
      maxZ >= vector3.z
    );
  }

  public boolean isFakeCrystal(Vector3 position) {
    for (CrystalSection crystalSection : crystalSections) {
      Crystal firstCrystal = crystalSection.getFirstCrystal();
      Crystal secondCrystal = crystalSection.getSecondCrystal();

      return (
        firstCrystal.getPosition().equals(position) &&
        firstCrystal.isFake() ||
        secondCrystal.getPosition().equals(position) &&
        secondCrystal.isFake()
      );
    }

    return true;
  }
}
