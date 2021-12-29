package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import java.time.Duration;
import java.util.function.Predicate;
import jossc.squidgame.map.GreenLightRedLightMap;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.PlayerRequestToLoseEvent;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.MathUtils;

public class GreenLightRedLight extends Microgame {

  private boolean canWalk = true;

  public GreenLightRedLight(Game game, Duration duration) {
    super(game, duration);
  }

  @Override
  public String getName() {
    return "Red light & Green light";
  }

  @Override
  public String getInstruction() {
    return "To win you must reached the goal";
  }

  @Override
  public void onGameStart() {
    singDoll();
  }

  private void singDoll() {
    int time = MathUtils.nextInt(2, 5);

    Predicate<? super Player> condition = player ->
      !roundWinners.contains(player);

    broadcastMessage("&l&a» &r&fGreen light!", condition);
    broadcastTitle("&aGreen Light!", "&fYou can move.");
    broadcastSound("mob.ghast.moan", condition);

    giveGreenWool();

    canWalk = true;

    schedule(
      () -> {
        broadcastMessage("&l&c» &r&fRed light!", condition);
        broadcastTitle("&cRed Light!", "You can not move.");
        broadcastSound("mob.blaze.hit", condition);

        giveRedWool();

        schedule(
          () -> {
            canWalk = false;
            int waitTime = MathUtils.nextInt(2, 5);

            schedule((this::singDoll), waitTime * 20);
          },
          20
        );
      },
      time * 20
    );
  }

  private void giveWool(int meta) {
    getRoundLosers()
      .forEach(
        loser -> {
          for (int i = 0; i <= 8; i++) {
            loser.getInventory().setItem(i, Item.get(Item.WOOL, meta));
          }
          User user = userFactory.get(loser);

          if (user != null) {
            user.updateInventory();
          }
        }
      );
  }

  private void giveGreenWool() {
    giveWool(5);
  }

  private void giveRedWool() {
    giveWool(14);
  }

  @EventHandler
  public void onRequestToDeath(PlayerRequestToLoseEvent event) {
    if (event.getLoseCause() != PlayerRequestToLoseEvent.LoseCause.VOID) {
      lose(event.getPlayer());
    }
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!(map instanceof GreenLightRedLightMap)) {
      return;
    }

    Player player = event.getPlayer();

    Vector3 playerPosition = player.asVector3f().asVector3();

    if (
      !roundWinners.contains(player) &&
      !canWalk &&
      !isReadyToEnd() &&
      !player.isSpectator()
    ) {
      lose(player);

      return;
    }

    if (
      ((GreenLightRedLightMap) map).isGoalArea(playerPosition) &&
      !roundWinners.contains(player)
    ) {
      win(player);
    }
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {}
}
