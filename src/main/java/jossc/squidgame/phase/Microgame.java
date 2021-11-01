package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import jossc.game.Game;
import jossc.game.phase.GamePhase;

public abstract class Microgame extends GamePhase {

  protected int countdown = 11;

  protected List<Player> roundWinners = new ArrayList<>();

  public Microgame(Game game) {
    super(game);
  }

  public Microgame(Game game, Duration duration) {
    super(game, duration);
  }

  public abstract String getName();

  public abstract String getInstruction();

  @Override
  protected void onStart() {}

  @Override
  public void onUpdate() {
    countdown--;

    if (countdown > 0) {
      if (countdown == 10 || countdown <= 5) {
        broadcastMessage(
          "&c&l» &r&fThis game will start in &c" + countdown + "&f seconds!"
        );
        broadcastSound("note.bassattack", 2, 2);
      }
    } else if (countdown == 0) {
      String instruction = getInstruction();

      if (instruction.isEmpty()) {
        return;
      }

      schedule(() -> broadcastMessage("&7" + getName() + " &e&l» &r&f" + instruction), 11);

      onGameStart();
    }

    if (neutralPlayersSize() == 1) {
      end();
    } else if (neutralPlayersSize() < 1) {
      end();
      game.shutdown();
    } else {
      onGameUpdate();
    }
  }

  public abstract void onGameStart();

  public abstract void onGameUpdate();

  @Override
  protected void onEnd() {
    super.onEnd();
    onGameEnd();
    roundWinners.clear();
  }

  public abstract void onGameEnd();

  public List<Player> getRoundLosers() {
    List<Player> losers = new ArrayList<>();

    getNeutralPlayers()
      .forEach(
        player -> {
          if (!roundWinners.contains(player)) {
            roundWinners.add(player);
          }
        }
      );

    return losers;
  }

  public void win(Player player) {
    game.giveDefaultAttributes(player);
    roundWinners.add(player);
    player.sendTitle(
      TextFormat.colorize("&bYou won this game!"),
      TextFormat.WHITE + "You will continue in the next game."
    );
    playSound(player, "random.levelup", 2, 3);
  }

  public void lose(Player player) {
    game.convertSpectator(player, true);
  }
}
