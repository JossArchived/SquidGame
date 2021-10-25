package jossc.squidgame;

import jossc.game.Game;
import jossc.game.state.ScheduledStateSeries;
import jossc.squidgame.listener.DeathListener;
import jossc.squidgame.listener.GeneralListener;
import jossc.squidgame.state.*;
import lombok.Getter;

public class SquidGame extends Game {

  @Getter
  private static SquidGame plugin;

  @Getter
  private ScheduledStateSeries mainState;

  @Override
  public void init() {
    plugin = this;

    registerListener(new GeneralListener());
    registerListener(new DeathListener());

    mainState = new ScheduledStateSeries(this);

    mainState.add(new PreGameState(this, 12));
    mainState.add(new GreenLightRedLightGameState(this));
    mainState.add(new DalgonaGameState(this));
    mainState.add(new LightsOffGameState(this));
    mainState.add(new TheRopeGameState(this));
    mainState.add(new CrystalsGameState(this));
    mainState.add(new SquidGameState(this));
    mainState.add(new EndGameState(this));

    mainState.start();

    registerDefaultCommands(mainState);
  }
}
