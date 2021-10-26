package jossc.squidgame;

import java.util.UUID;
import jossc.game.Game;
import jossc.squidgame.arena.Arena;
import jossc.squidgame.listener.ProtectListener;
import lombok.Getter;

public class SquidGame extends Game {

  @Getter
  private static SquidGame plugin;

  @Getter
  private Arena arena;

  @Override
  public void init() {
    plugin = this;

    registerListener(new ProtectListener());

    String uuid = UUID.randomUUID().toString().substring(0, 5);
    String worldName = getServer().getDefaultLevel().getName();

    arena = new Arena(this, uuid, worldName, 2, 12);
    arena.registerMicroGames();
    arena.enable();
  }

  @Override
  public void close() {
    arena.disable();
  }
}
