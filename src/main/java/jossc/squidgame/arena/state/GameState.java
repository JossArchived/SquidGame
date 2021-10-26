package jossc.squidgame.arena.state;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.squidgame.arena.Arena;
import jossc.squidgame.util.Util;

public abstract class GameState extends jossc.game.state.GameState {

  protected final Arena arena;

  public GameState(PluginBase plugin) {
    this(plugin, null);
  }

  public GameState(PluginBase plugin, Arena arena) {
    this(plugin, Duration.ZERO, arena);
  }

  public GameState(PluginBase plugin, Duration duration, Arena arena) {
    super(plugin, duration);
    this.arena = arena;
  }

  public boolean isFull() {
    return neutralPlayersSize() >= arena.getMaxPlayers();
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    Util.setDefaultAttributes(player);

    String joinMessage =
      "&7[&a+&7]&8 " +
      player.getName() +
      " &7joined the game (" +
      neutralPlayersSize() +
      "/" +
      arena.getMaxPlayers() +
      ").";

    event.setJoinMessage(TextFormat.colorize(joinMessage));
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    String message =
      "&7[&c-&7]&8 " +
      event.getPlayer().getName() +
      " &7left the game (" +
      (neutralPlayersSize() - 1) +
      "/" +
      arena.getMaxPlayers() +
      ").";

    event.setQuitMessage(TextFormat.colorize(message));
  }
}
