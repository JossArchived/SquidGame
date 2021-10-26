package jossc.squidgame.arena;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import java.util.LinkedList;
import java.util.List;
import jossc.game.state.ScheduledStateSeries;
import jossc.squidgame.arena.state.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Arena {

  private final PluginBase plugin;
  private final String uuid;
  private final String worldName;
  private final int minPlayers;
  private final int maxPlayers;
  private final List<GameState> microGames = new LinkedList<>();
  private ScheduledStateSeries mainState;

  public void registerMicroGames() {
    microGames.add(new PreGameState(plugin, this));
    microGames.add(new GreenLightRedLightGameState(plugin, this));
    microGames.add(new DalgonaGameState(plugin, this));
    microGames.add(new LightsOffGameState(plugin, this));
    microGames.add(new TheRopeGameState(plugin, this));
    microGames.add(new CrystalsGameState(plugin, this));
    microGames.add(new SquidGameState(plugin, this));
  }

  public Level getWorld() {
    return Server.getInstance().getLevelByName(worldName);
  }

  public void enable() {
    mainState = new ScheduledStateSeries(plugin, 1);
    microGames.forEach(microGame -> mainState.add(microGame));
    mainState.start();
  }

  public void disable() {
    mainState.setFrozen(true);
    mainState.end();
  }
}
