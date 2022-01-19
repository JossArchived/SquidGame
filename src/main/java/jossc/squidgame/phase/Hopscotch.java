package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jossc.squidgame.map.HopscotchMap;
import jossc.squidgame.map.feature.crystal.Crystal;
import jossc.squidgame.map.feature.crystal.CrystalSection;
import net.josscoder.gameapi.util.VectorUtils;

public class Hopscotch extends Microgame {

  private boolean canReciveDamage = false;

  public Hopscotch(Duration duration) {
    super(duration);
  }

  @Override
  public String getName() {
    return "Hopscotch";
  }

  @Override
  public String getInstruction() {
    return "In this game you have to reach the goal, remember that there are tempered crystals through which you can walk... the rest are fake.";
  }

  @Override
  public void setupMap(Config config) {
    ConfigSection section = config.getSection("maps.hopscotchMap");
    ConfigSection goalSection = section.getSection("goal");
    ConfigSection crystalsSection = section.getSection("crystal");

    int crystalSectionsNumber = crystalsSection.getInt("number");

    List<CrystalSection> crystalSections = new ArrayList<>();

    for (int i = 1; i <= crystalSectionsNumber; i++) {
      List<Vector3> crystals = VectorUtils.stringListToVectorList(
        crystalsSection.getSection("sections").getStringList(String.valueOf(i))
      );
      boolean randomBoolean = new Random().nextBoolean();

      crystalSections.add(
        new CrystalSection(
          i,
          new Crystal(crystals.get(0), randomBoolean),
          new Crystal(crystals.get(1), !randomBoolean)
        )
      );
    }

    map =
      new HopscotchMap(
        game,
        section.getString("name"),
        VectorUtils.stringToVector(section.getString("safeSpawn")),
        crystalSections,
        VectorUtils.stringToVector(goalSection.getString("cornerOne")),
        VectorUtils.stringToVector(goalSection.getString("cornerTwo"))
      );
  }

  @Override
  public void onGameStart() {
    canReciveDamage = true;
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();

    if (player.getGamemode() == Player.SPECTATOR) {
      return;
    }

    if (((HopscotchMap) map).isTheGoal(player) && !isRoundWinner(player)) {
      win(player);

      return;
    }

    Block blockBelow = player.getLevel().getBlock(player.subtract(0, 1));

    if (blockBelow == null || blockBelow.getId() != Block.GLASS) {
      return;
    }

    if (
      ((HopscotchMap) map).isFakeCrystal(
          blockBelow.asBlockVector3().asVector3().round()
        )
    ) {
      player.getLevel().setBlock(blockBelow, new BlockAir(), false, true);
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  @Override
  public void onDamage(EntityDamageEvent event) {
    Entity entity = event.getEntity();

    if (!(entity instanceof Player) || !canReciveDamage) {
      super.onDamage(event);

      return;
    }

    Player player = (Player) entity;

    if (
      event.getCause().equals(EntityDamageEvent.DamageCause.FALL) &&
      player.getGamemode() != Player.CREATIVE &&
      !isRoundWinner(player)
    ) {
      lose(player);
    } else {
      super.onDamage(event);
    }
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {
    canReciveDamage = false;
  }
}
