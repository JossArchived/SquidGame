package jossc.squidgame.microgame;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Config;
import java.time.Duration;
import jossc.squidgame.SquidGamePlugin;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.user.User;

public class NightAmbush extends Microgame {

  private boolean canAttack = false;

  public NightAmbush(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  public String getName() {
    return "Night Ambush";
  }

  @Override
  public String getInstruction() {
    return "We have heard that various groups came together to kill players and raise the amount of money... survive";
  }

  @Override
  public void setupMap(Config config) {
    map = ((SquidGamePlugin) game).getRoomMap();
  }

  @Override
  public void onGameStart() {
    giveWeapons();
    giveEffect();

    broadcastMessage("&6&l» &r&fGet safe, pvp is coming soon...");

    schedule(
      () -> {
        canAttack = true;
        broadcastMessage("&c&l» &r&cPvP has been enabled!");
      },
      20 * 5
    );
  }

  private void giveWeapons() {
    getNeutralUsers()
      .forEach(
        user -> {
          Player player = user.getPlayer();

          if (player != null) {
            player.getInventory().setItem(0, Item.get(ItemID.STONE_SWORD));
            user.updateInventory();
          }
        }
      );
  }

  private void giveEffect() {
    getNeutralPlayers()
      .forEach(
        player -> {
          Effect blindness = Effect
            .getEffect(Effect.BLINDNESS)
            .setDuration(1000000)
            .setAmplifier(5)
            .setVisible(false);

          player.addEffect(blindness);
        }
      );
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {
    getNeutralUsers().forEach(User::giveDefaultAttributes);
  }

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
