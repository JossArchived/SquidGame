package jossc.squidgame.entity;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.FlameParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import jossc.squidgame.phase.Microgame;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.storage.LocalStorage;

public class EntityBurningNeedleSnowball
  extends cn.nukkit.entity.projectile.EntitySnowball {

  private final User shootingUser;
  private final Microgame microgame;

  public EntityBurningNeedleSnowball(
    FullChunk chunk,
    CompoundTag nbt,
    User shootingUser,
    Microgame microgame
  ) {
    super(chunk, nbt);
    this.shootingUser = shootingUser;
    this.microgame = microgame;
  }

  @Override
  public boolean onUpdate(int currentTick) {
    Player player = shootingUser.getPlayer();

    if (isCollided && player != null) {
      Block block = getLevelBlock();

      if (block != null && block.getId() == Block.STAINED_HARDENED_CLAY) {
        shootingUser.playSound("random.levelup", 2, 3);

        LocalStorage storage = shootingUser.getLocalStorage();

        storage.set("blocks_broken", storage.getInteger("blocks_broken") + 1);

        shootingUser.sendMessage("&l&6» +1 point");

        if (storage.getInteger("blocks_broken") >= 5) {
          microgame.win(player);
        }
      } else {
        player.setHealth(player.getHealth() - 1);
        level.addLevelSoundEvent(
          player,
          LevelSoundEventPacket.SOUND_ATTACK_NODAMAGE
        );
      }

      close();

      return false;
    }

    boolean update = super.onUpdate(currentTick);

    if (update) {
      level.addParticle(new FlameParticle(this));
    }

    return update;
  }

  @Override
  public void onHit() {}
}
