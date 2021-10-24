package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;

public class GreenLightRedLightGameState extends Microgame {

  public GreenLightRedLightGameState(PluginBase plugin) {
    super(plugin, Duration.ofMinutes(5));
  }

  @Override
  public String getInstructions() {
    return "&7In this game, you will have to cross the line to win, but ... you can only walk when the &l&2light is green.&r&7 If the &l&clight is red&r&7 and you walk, you will be automatically eliminated. &6Good luck!";
  }

  @Override
  public void onGameStart() {
    schedule(
      () -> {
        broadcastTitle(
          TextFormat.BOLD.toString() +
          TextFormat.GREEN +
          "GREEN LIGHT " +
          TextFormat.RED +
          " RED LIGHT!",
          TextFormat.DARK_GRAY + "Cross the line!",
          0,
          (int) (duration.toMillis() / 1000) * 20,
          0
        );
        broadcastSound("");
      },
      11
    );
  }

  @Override
  public void onUpdate() {}

  @Override
  public void onGameEnd() {}
}
