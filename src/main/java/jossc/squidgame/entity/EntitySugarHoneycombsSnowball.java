package jossc.squidgame.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntitySnowball;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import jossc.squidgame.SquidGameClass;
import net.josscoder.gameapi.user.User;

public class EntitySugarHoneycombsSnowball extends EntitySnowball {

  public EntitySugarHoneycombsSnowball(FullChunk chunk, CompoundTag nbt) {
    super(chunk, nbt);
  }

  public EntitySugarHoneycombsSnowball(
    FullChunk chunk,
    CompoundTag nbt,
    Entity shootingEntity
  ) {
    super(chunk, nbt, shootingEntity);
  }

  @Override
  public boolean onUpdate(int currentTick) {
    getLevel().addParticle(new FlameParticle(asVector3f().asVector3()));

    return super.onUpdate(currentTick);
  }

  @Override
  public void onHit() {
    if (!(shootingEntity instanceof Player)) {
      return;
    }

    User user = SquidGameClass.getInstance().getUserFactory().get((Player) shootingEntity);

    if (user == null) {
      return;
    }

    user.playSound("");
  }
}
