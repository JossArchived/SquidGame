package jossc.squidgame;

import jossc.game.Game;
import jossc.game.state.ScheduledStateSeries;
import jossc.squidgame.state.*;

public class SquidGame extends Game {

  @Override
  public void init() {
    ScheduledStateSeries mainState = new ScheduledStateSeries(this, 1);
    mainState.add(new PrepareGameState(this));
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
