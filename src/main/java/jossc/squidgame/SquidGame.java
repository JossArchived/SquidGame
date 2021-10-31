package jossc.squidgame;

import cn.nukkit.Player;
import jossc.game.Game;
import lombok.Getter;

public class SquidGame extends Game {

  @Getter
  private static SquidGame plugin;

  @Override
  public void init() {
    plugin = this;

    setDefaultGameMode(Player.ADVENTURE);
  }

  @Override
  public String getGameName() {
    return "Squid Game";
  }

  @Override
  public String getInstruction() {
    return "";
  }

  @Override
  public void close() {}
}
