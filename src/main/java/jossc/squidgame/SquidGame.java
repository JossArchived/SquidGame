package jossc.squidgame;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;
import jossc.game.Game;
import jossc.game.phase.PhaseSeries;
import jossc.game.phase.lobby.PreGamePhase;
import jossc.game.phase.lobby.LobbyCountdownPhase;
import jossc.game.phase.lobby.LobbyWaitingPhase;
import lombok.Getter;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;

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
    setWaitingLobby(getServer().getDefaultLevel().getSafeSpawn().add(0, 1));
    setSpawns(new LinkedList<Vector3>(){{
      add(new Vector3(0, 5, 0));
      add(new Vector3(0, 5, 0));
    }});
    setTips(new ArrayList<String>(){{
      add("Don't move when the red light shows!");
      add("Pull hard on the rope game!");
    }});

    PhaseSeries phaseSeries = new PhaseSeries(this);
    phaseSeries.add(new LobbyWaitingPhase(this));
    phaseSeries.add(new LobbyCountdownPhase(this, Duration.ofSeconds(20)));
    phaseSeries.add(new PreGamePhase(this, 10));
    phaseSeries.start();

    registerDefaultCommands(phaseSeries);
  }

  @Override
  public String getGameName() {
    return "Squid Game";
  }

  @Override
  public String getInstruction() {
    return "Be the last one standing and earn a lot of money, do not forgive anyone ...";
  }

  @Override
  public void close() {}
}
