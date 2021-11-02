package jossc.squidgame;

import cn.nukkit.Player;
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

    Position testPosition = getServer().getDefaultLevel().getSafeSpawn().add(0, 1);

    setWaitingLobby(testPosition);
    setPedestalPosition(testPosition);

    Map<Integer, Position> pedestalList = new HashMap<>();
    pedestalList.put(1, testPosition);
    setPedestalList(pedestalList);

    setSpawns(
      new LinkedList<Vector3>() {
        {
          add(new Vector3(0, 5, 3));
          add(new Vector3(0, 5, 0));
        }
      }
    );

    PhaseSeries phaseSeries = new PhaseSeries(this);
    phaseSeries.add(new LobbyWaitingPhase(this));
    phaseSeries.add(new LobbyCountdownPhase(this, Duration.ofSeconds(21)));
    phaseSeries.add(new PreGamePhase(this, 10));
    phaseSeries.add(new RedLightGreenLight(this));
    phaseSeries.start();

    registerDefaultCommands(phaseSeries);
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
