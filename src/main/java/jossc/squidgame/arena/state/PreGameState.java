package jossc.squidgame.arena.state;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.squidgame.arena.Arena;
import org.jetbrains.annotations.NotNull;

public class PreGameState extends GameState {

  private final int initialCountdown = 60;
  private int countdown;

  public PreGameState(PluginBase plugin, Arena arena) {
    super(plugin, Duration.ZERO, arena);
    this.countdown = initialCountdown;
  }

  @NotNull
  @Override
  public Duration getDuration() {
    return Duration.ZERO;
  }

  @Override
  protected void onStart() {
    //TODO: reset map
    plugin.getLogger().info(TextFormat.GREEN + "Now accepting players!");
  }

  private boolean isAvailable() {
    return !isFull() && countdown > 15;
  }

  @Override
  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!isAvailable()) {
      event.setJoinMessage("");
      player.kick(TextFormat.RED + "This game is already starting!", true);

      return;
    }

    super.onJoin(event);

    if (isFull()) {
      String fullMessage =
        "&6The server is now &lFULL!&r&6 The game will start in &e15 seconds&6!";

      broadcastMessage(TextFormat.colorize(fullMessage));

      countdown = 15;
    }
  }

  @Override
  public void onUpdate() {
    if (neutralPlayersSize() >= arena.getMinPlayers()) {
      countdown--;
      broadcastActionBar(
        TextFormat.GREEN +
        "Starting in " +
        countdown +
        " second" +
        (countdown == 1 ? "" : "s")
      );
    } else {
      countdown = initialCountdown;
      broadcastActionBar(TextFormat.RED + "Waiting for more players...");
    }
  }

  @Override
  public boolean isReadyToEnd() {
    return (
      super.isReadyToEnd() &&
      countdown == 0 &&
      neutralPlayersSize() >= arena.getMinPlayers()
    );
  }

  @Override
  protected void onEnd() {
    plugin.getLogger().info(TextFormat.RED + "Players are no longer accepted!");
  }
}
