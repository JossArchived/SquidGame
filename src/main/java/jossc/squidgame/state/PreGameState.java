package jossc.squidgame.state;

import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.game.state.GameState;
import org.jetbrains.annotations.NotNull;

public class PreGameState extends GameState {

  private final int maxPlayers;
  private final int minPlayers;

  public PreGameState(PluginBase plugin, int maxPlayers) {
    super(plugin);
    this.maxPlayers = maxPlayers;
    this.minPlayers = maxPlayers / 6;

    System.out.println(minPlayers);
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return neutralPlayersSize() >= maxPlayers
      ? Duration.ofSeconds(15)
      : neutralPlayersSize() >= minPlayers
        ? Duration.ofMinutes(1)
        : (neutralPlayersSize() < minPlayers ? Duration.ofMinutes(1) : Duration.ZERO);
  }

  @Override
  protected void onStart() {

  }

  @Override
  public void onUpdate() {}

  @Override
  protected void onEnd() {}
}
