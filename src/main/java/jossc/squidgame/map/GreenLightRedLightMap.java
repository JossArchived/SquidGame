package jossc.squidgame.map;

import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.Map;

@Setter
@Getter
public class GreenLightRedLightMap extends Map {

  private Vector3 goalCornerOne = null;
  private Vector3 goalCornerTwo = null;
  private Vector3 dollPosition = null;

  public GreenLightRedLightMap(Game game, String name, Vector3 safeSpawn) {
    super(game, name, safeSpawn);
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
}
