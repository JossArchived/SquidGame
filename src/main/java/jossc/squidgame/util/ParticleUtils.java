package jossc.squidgame.util;

import cn.nukkit.level.Position;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.math.Vector3;

public class ParticleUtils {

  public static void fireShoot(Position dollPosition, Position playerPosition) {
    if (dollPosition.getLevel() != playerPosition.getLevel()) {
      return;
    }

    Vector3 from = dollPosition.asVector3f().asVector3();
    Vector3 to = playerPosition.asVector3f().asVector3();

    for (double i = 0; i < 10; i += 0.5) {
      to.multiply(i);
      from.add(to);

      dollPosition.getLevel().addParticle(new FlameParticle(from));

      from.subtract(to);
      to.normalize();
    }
  }
}
