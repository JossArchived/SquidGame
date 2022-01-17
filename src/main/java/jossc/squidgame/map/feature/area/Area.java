package jossc.squidgame.map.feature.area;

import cn.nukkit.math.Vector3;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public abstract class Area {

  private Vector3 cornerOne;
  private Vector3 cornerTwo;

  public boolean isWithin(Vector3 vector3) {
    if (cornerOne == null || cornerTwo == null) {
      return true;
    }

    int minX = (int) Math.min(cornerOne.x, cornerTwo.x);
    int maxX = (int) Math.max(cornerOne.x, cornerTwo.x);

    int minZ = (int) Math.min(cornerOne.z, cornerTwo.z);
    int maxZ = (int) Math.max(cornerOne.z, cornerTwo.z);

    return (
      minX <= vector3.x &&
      maxX >= vector3.x &&
      minZ <= vector3.z &&
      maxZ >= vector3.z
    );
  }
}
