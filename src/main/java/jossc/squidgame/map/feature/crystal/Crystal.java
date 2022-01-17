package jossc.squidgame.map.feature.crystal;

import cn.nukkit.math.Vector3;
import lombok.Getter;

@Getter
public class Crystal {

  private final Vector3 position;
  private final boolean fake;

  public Crystal(Vector3 position, boolean fake) {
    this.position = position;
    this.fake = fake;
  }
}
