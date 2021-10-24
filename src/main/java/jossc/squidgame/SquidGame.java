package jossc.squidgame;

import jossc.game.Game;
import jossc.game.state.ScheduledStateSeries;
import jossc.squidgame.state.*;

public class SquidGame extends Game {

  @Override
  public void init() {
    ScheduledStateSeries mainState = new ScheduledStateSeries(this, 1);
    mainState.add(new PrepareGame(this));
    mainState.add(new GreenLightRedLight(this));
    mainState.add(new Dalgona(this));
    mainState.add(new LightsOff(this));
    mainState.add(new TheRope(this));
    mainState.add(new Crystals(this));
    mainState.add(new Squid(this));
    mainState.add(new EndGame(this));
    mainState.start();

    registerDefaultCommands(mainState);
  }
}
