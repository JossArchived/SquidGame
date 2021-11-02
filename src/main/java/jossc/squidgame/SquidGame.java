package jossc.squidgame;

import cn.nukkit.Player;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import jossc.game.Game;
import jossc.game.phase.PhaseSeries;
import jossc.game.phase.lobby.LobbyCountdownPhase;
import jossc.game.phase.lobby.LobbyWaitingPhase;
import jossc.game.phase.lobby.PreGamePhase;
import jossc.squidgame.command.SoundCommand;
import jossc.squidgame.phase.RedLightGreenLight;
import lombok.Getter;

public class SquidGame extends Game {

  @Getter
  private static SquidGame plugin;

  @Override
  public void init() {
    plugin = this;

    setMapName("squidgame");
    prepareMap("squidgame");
    setDefaultGameMode(Player.ADVENTURE);
    setMaxPlayers(100);

    Level waitingLobbyMap = getServer().getDefaultLevel();

    setWaitingLobby(waitingLobbyMap.getSafeSpawn().add(0, 1));

    setPedestalPosition(
      Position.fromObject(new Vector3(147, 11, 129), waitingLobbyMap)
    );

    Map<Integer, Vector3> pedestalList = new HashMap<>();
    pedestalList.put(
      1,
      new Vector3(156, 14, 129)
    );
    pedestalList.put(
      2,
      new Vector3(156, 13, 133)
    );
    pedestalList.put(
      3,
      new Vector3(156, 12, 125)
    );

    setPedestalList(pedestalList);

    setSpawns(
      new LinkedList<Vector3>() {
        {
          for (int i = 0; i <= 100; i++) {
            add(new Vector3(113, 5, 133));
          }
        }
      }
    );

    phaseSeries = new PhaseSeries(this);
    phaseSeries.add(new LobbyWaitingPhase(this));
    phaseSeries.add(new LobbyCountdownPhase(this, Duration.ofSeconds(21)));
    phaseSeries.add(new PreGamePhase(this, 10));
    phaseSeries.add(new RedLightGreenLight(this));
    phaseSeries.start();

    registerDefaultCommands();
    registerCommand(new SoundCommand());
  }

  @Override
  public String getGameName() {
    return "Squid Game";
  }

  @Override
  public String getInstruction() {
    return "The last person standing will win a lot of money. For this you have to win all the games...";
  }

  @Override
  public void close() {}
}
