package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.game.Game;
import jossc.game.utils.math.MathUtils;

public class RedLightGreenLight extends Microgame {

  private boolean canWalk = true;
  private final Vector3 cornerOne;
  private final Vector3 cornerTwo;

  public RedLightGreenLight(Game game) {
    super(game, Duration.ofMinutes(5));
    gamePosition = Position.fromObject(new Vector3(-22, 4, 188), game.getMap());
    cornerOne = new Vector3(42, 3, 169);
    cornerTwo = new Vector3(54, 23, 212);
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

  @Override
  public void onGameUpdate() {}

  @Override
  public boolean isReadyToEnd() {
    return super.isReadyToEnd() || getRoundLosers().size() == 0;
  }

  @Override
  public void onGameEnd() {
    schedule(() -> getRoundLosers().forEach(this::lose), 40);
  }

  public boolean isSafeArena(Vector3 vector3) {
    int minX = (int) Math.min(cornerOne.x, cornerTwo.x);
    int maxX = (int) Math.max(cornerOne.x, cornerTwo.x);

    int minZ = (int) Math.min(cornerOne.z, cornerTwo.z);
    int maxZ = (int) Math.max(cornerOne.z, cornerTwo.z);

    return (
      minX <= vector3.x &&
      maxX >= vector3.x &&
      minZ <= vector3.z &&
      maxZ >= vector3.z
    );
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    Vector3 playerAsVector3 = player.asVector3f().asVector3();

    if (
      !roundWinners.contains(player) &&
      !canWalk &&
      !isReadyToEnd() &&
      !player.isSpectator() &&
      !isSafeArena(playerAsVector3)
    ) {
      lose(player);

      return;
    }

    if (isSafeArena(playerAsVector3) && !roundWinners.contains(player)) {
      win(player);
    }
  }

  private void giveWool(int meta) {
    getRoundLosers()
      .forEach(
        loser -> {
          for (int i = 0; i <= 8; i++) {
            loser.getInventory().setItem(i, Item.get(Item.WOOL, meta));
          }
          game.updatePlayerInventory(loser);
        }
      );
  }

  private void singDoll() {
    int time = MathUtils.nextInt(2, 5);

    getRoundLosers()
      .forEach(
        loser -> {
          loser.sendMessage(TextFormat.colorize("&l&a» &r&fGreen light!"));
          loser.sendTitle(
            TextFormat.colorize("&aGreen Light!"),
            TextFormat.WHITE + "You can move."
          );
          playSound(loser, "mob.ghast.moan");
        }
      );
    giveWool(5);

    canWalk = true;

    schedule(
      () -> {
        getRoundLosers()
          .forEach(
            loser -> {
              loser.sendMessage(TextFormat.colorize("&l&c» &r&fRed light!"));
              loser.sendTitle(
                TextFormat.colorize("&cRed Light!"),
                TextFormat.WHITE + "You can not move."
              );
              playSound(loser, "mob.blaze.hit");
            }
          );
        giveWool(14);

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
}
