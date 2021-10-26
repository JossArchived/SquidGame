package jossc.squidgame.state;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import jossc.game.utils.math.MathUtils;

import java.time.Duration;
import java.util.ArrayList;

public class GreenLightRedLightGameState extends Microgame {

  private boolean canWalk = true;

  public GreenLightRedLightGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(5));
  }

  @Override
  public String getInstructions() {
    return "To win you must reached the goal";
  }

  @Override
  public void onGameStart() {
    singDoll();
  }

  private void singDoll() {
    int time = MathUtils.randomNumber(2, 5);

    broadcastTitle(
      TextFormat.DARK_GREEN + "Green Light",
      TextFormat.GREEN + "Now you can move"
    );
    broadcastSound("mob.ghast.moan");
    canWalk = true;

    schedule(
      () -> {
        broadcastTitle(
          TextFormat.DARK_RED + "Red Light",
          TextFormat.RED + "DO NOT MOVE"
        );
        broadcastSound("mob.blaze.hit");

        schedule(
          () -> {
            canWalk = false;
            int waitTime = MathUtils.randomNumber(2, 5);

            schedule(this::singDoll, waitTime * 20);
          },
          20
        );
      },
      time * 20
    );
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!canWalk) {
      lose(event.getPlayer());
    }
  }

  @Override
  public void onGameUpdate() {
    ArrayList<String> lines = new ArrayList<>();

    lines.add(" ");
    lines.add("Round winners: " + roundWinners.size());
    lines.add("Ending in: " + duration.getSeconds());
    lines.add("   ");
    lines.add(
      "State: " +
      TextFormat.BOLD +
      (canWalk ? TextFormat.GREEN + "MOVE ON" : "DO NOT MOVE") +
      "!"
    );
    lines.add("     ");
    lines.add(TextFormat.YELLOW + "play.ubbly.club");

    getPlayers()
      .forEach(
        player -> scoreboard.sendLines(player, lines.toArray(new String[0]))
      );
  }

  @Override
  public void onGameEnd() {
    schedule(() -> getRoundLosers().forEach(this::lose), 40);
  }
}
