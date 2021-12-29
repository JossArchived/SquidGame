package jossc.squidgame.map;

import cn.nukkit.math.Vector3;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;

@Setter
@Getter
public class GreenLightRedLightMap extends GameMap {

  private Vector3 goalCornerOne;
  private Vector3 goalCornerTwo;
  private Vector3 dollPosition;

  public GreenLightRedLightMap(
    Game game,
    String name,
    Vector3 safeSpawn,
    Vector3 goalCornerOne,
    Vector3 goalCornerTwo,
    Vector3 dollPosition
  ) {
    super(game, name, safeSpawn);
    this.goalCornerOne = goalCornerOne;
    this.goalCornerTwo = goalCornerTwo;
    this.dollPosition = dollPosition;
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
