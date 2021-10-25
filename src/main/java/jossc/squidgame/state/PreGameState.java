package jossc.squidgame.state;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import jossc.game.state.GameState;
import org.jetbrains.annotations.NotNull;

public class PreGameState extends GameState {

  private final int maxPlayers;
  private final int minPlayers;
  private final int initialCountdown = 60;
  private int countdown;

  public PreGameState(PluginBase plugin, int maxPlayers) {
    this(plugin, maxPlayers, (maxPlayers / 6));
  }

  public PreGameState(PluginBase plugin, int maxPlayers, int minPlayers) {
    super(plugin);
    this.countdown = initialCountdown;
    this.maxPlayers = maxPlayers;
    this.minPlayers = minPlayers;
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

  private boolean isFull() {
    return neutralPlayersSize() >= maxPlayers;
  }

  private boolean isAvailable() {
    return !isFull() && countdown > 15;
  }

  @EventHandler
  public void onJoin(PlayerJoinEvent event) {
    Player player = event.getPlayer();

    if (!isAvailable()) {
      event.setJoinMessage("");
      player.kick(
        TextFormat.RED + "This game is not available at this time!",
        true
      );

      return;
    }

    String joinMessage =
      "&7[&a+&7]&8 " +
      player.getName() +
      " &7joined the game (" +
      neutralPlayersSize() +
      "/" +
      maxPlayers +
      ").";

    event.setJoinMessage(TextFormat.colorize(joinMessage));

    if (isFull()) {
      String fullMessage =
        "&6The server is now &lFULL!&r&6 The game will start in &e15 seconds&6!";

      broadcastMessage(TextFormat.colorize(fullMessage));

      countdown = 15;
    }
  }

  @EventHandler
  public void onQuit(PlayerQuitEvent event) {
    String message =
      "&7[&c-&7]&8 " +
      event.getPlayer().getName() +
      " &7left the game (" +
      (neutralPlayersSize() - 1) +
      "/" +
      maxPlayers +
      ").";

    event.setQuitMessage(TextFormat.colorize(message));
  }

  @Override
  public void onUpdate() {
    if (neutralPlayersSize() >= minPlayers) {
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
  protected void onEnd() {
    plugin.getLogger().info(TextFormat.RED + "Players are no longer accepted!");
  }

  @Override
  public boolean isReadyToEnd() {
    return (
      super.isReadyToEnd() &&
      countdown == 0 &&
      neutralPlayersSize() >= minPlayers
    );
  }
}
