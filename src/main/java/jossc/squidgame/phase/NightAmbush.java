package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.potion.Effect;
import cn.nukkit.scheduler.Task;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.List;
import net.josscoder.gameapi.user.User;

public class NightAmbush extends Microgame {

  private boolean canAttack = false;

  private int countdown = 6;

  public NightAmbush(Duration duration) {
    super(duration);
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
    map = game.getRoomMap();
  }

  @Override
  public List<String> getScoreboardLines(User user) {
    List<String> lines = super.getScoreboardLines(user);

    lines.add(
      "\uE114 " +
      (
        countdown == 0
          ? TextFormat.RED + "PvP Enabled"
          : " Enabling pvp in " + countdown
      )
    );

    return lines;
  }

  @Override
  public void onGameStart() {
    giveWeapons();
    giveEffect();

    broadcastMessage("&6&l» &r&fGet safe, pvp is coming soon...");
    broadcastSound("note.bass", 0.8f, 1);

    scheduleRepeating(
      new Task() {
        @Override
        public void onRun(int i) {
          if (countdown > 0) {
            countdown--;
          }

          if (countdown == 0) {
            cancel();
            canAttack = true;
            broadcastMessage("&c&l» &r&cPvP has been enabled!");
            broadcastSound("block.turtle_egg.drop", 0.6f, 2);
          }
        }
      },
      20
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
