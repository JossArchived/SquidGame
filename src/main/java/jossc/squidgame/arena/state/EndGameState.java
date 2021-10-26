package jossc.squidgame.arena.state;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import java.time.Duration;
import jossc.squidgame.arena.Arena;
import jossc.squidgame.util.Util;

public class EndGameState extends GameState {

  public EndGameState(PluginBase plugin, Arena arena, boolean withPlayers) {
    super(
      plugin,
      (withPlayers ? Duration.ofSeconds(20) : Duration.ZERO),
      arena
    );
  }

  @Override
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Util.convertSpectator(event.getPlayer(), true);
  }

  @Override
  protected void onStart() {}

  @Override
  public void onUpdate() {}

  @Override
  protected void onEnd() {}
}
