package jossc.squidgame.phase;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import jossc.squidgame.entity.EntityBurningNeedleSnowball;
import jossc.squidgame.map.RedLightGreenLightMap;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.VectorUtils;

public class SugarHoneycombs extends Microgame {

  private CustomItem burningNeedle;

  public SugarHoneycombs(Game game, Duration duration) {
    super(game, duration);
    Entity.registerEntity(
      "EntityBurningNeedleSnowball",
      EntityBurningNeedleSnowball.class,
      true
    );

    registerBurningNeedleItem();
  }

  private void registerBurningNeedleItem() {
    burningNeedle =
      new CustomItem(
        Item.get(ItemID.BOW),
        "&6Burning Needle",
        (
          (user, player) -> {
            Vector3 directionVector = player.getDirectionVector();

            CompoundTag nbt = new CompoundTag()
              .putList(
                new ListTag<DoubleTag>("Pos")
                  .add(new DoubleTag("", player.x))
                  .add(new DoubleTag("", player.y + player.getEyeHeight()))
                  .add(new DoubleTag("", player.z))
              )
              .putList(
                new ListTag<DoubleTag>("Motion")
                  .add(new DoubleTag("", directionVector.x))
                  .add(new DoubleTag("", directionVector.y))
                  .add(new DoubleTag("", directionVector.z))
              )
              .putList(
                new ListTag<FloatTag>("Rotation")
                  .add(new FloatTag("", (float) player.yaw))
                  .add(new FloatTag("", (float) player.pitch))
              );

            EntityBurningNeedleSnowball projectile = new EntityBurningNeedleSnowball(
              player
                .getLevel()
                .getChunk(player.getFloorX() >> 4, player.getFloorZ() >> 4),
              nbt,
              user,
              this
            );

            projectile.setMotion(projectile.getMotion().multiply(1.5f));

            projectile.spawnToAll();
            player
              .getLevel()
              .addLevelSoundEvent(player, LevelSoundEventPacket.SOUND_LAVA_POP);
          }
        )
      );
  }

  @Override
  public String getName() {
    return "Sugar Honeycombs";
  }

  @Override
  public String getInstruction() {
    return "You will have to cut the figure to perfection before the game ends, remember that if you miss a block, your life will decrease";
  }

  @Override
  public void setupMap(Config config) {
    ConfigSection section = config.getSection("maps.sugarHoneycombsMap");

    map =
      new GameMap(
        game,
        section.getString("name"),
        VectorUtils.stringToVector(section.getString("safeSpawn"))
      );

    List<Vector3> spawns = new LinkedList<>();

    for (int i = 1; i <= game.getMaxPlayers(); i++) {
      spawns.add(VectorUtils.stringToVector(section.getString("spawns." + i)));
    }

    map.setSpawns(spawns);
    game.getGameMapManager().addMap(map);
  }

  @Override
  public void onGameStart() {
    giveBurningNeedle();
  }

  private void giveBurningNeedle() {
    getNeutralPlayers()
      .forEach(
        player -> {
          player.getInventory().setItem(0, burningNeedle.build());

          User user = userFactory.get(player);

          if (user != null) {
            user.updateInventory();
          }
        }
      );
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {}
}
