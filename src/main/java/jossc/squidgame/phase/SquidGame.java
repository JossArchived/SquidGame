package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import java.time.Duration;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.VectorUtils;

public class SquidGame extends Microgame {

  private boolean canAttack = false;

  public SquidGame(Duration duration) {
    super(duration);
  }

  @Override
  public String getName() {
    return "Squid Game";
  }

  @Override
  public String getInstruction() {
    return "Wow, last game, speaking of last... the last person standing wins!";
  }

  @Override
  public void setupMap(Config config) {
    ConfigSection section = config.getSection("maps.greenLightRedLightMap");

    map =
      new GameMap(
        game,
        section.getString("name"),
        VectorUtils.stringToVector(section.getString("safeSpawn"))
      );
    //TODO: u can add spawns
  }

  @Override
  public void onGameStart() {
    getNeutralUsers().forEach(User::giveDefaultAttributes);
    broadcastMessage("&6&l» &r&fGet safe, pvp is coming soon...");

    schedule(
      () -> {
        canAttack = true;
        broadcastMessage("&c&l» &r&cPvP has been enabled!");
      },
      20 * 5
    );
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {}

  @EventHandler
  @Override
  public void onDamage(EntityDamageEvent event) {
    if (!canAttack) {
      event.setCancelled();

      return;
    }

    Entity entity = event.getEntity();

    if (!(entity instanceof Player)) {
      return;
    }

    if (event.getFinalDamage() < entity.getHealth()) {
      return;
    }

    event.setCancelled();

    lose((Player) entity);
  }
}
