package jossc.squidgame;

import cn.nukkit.utils.TextFormat;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import jossc.squidgame.phase.GreenLightRedLight;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.phase.GamePhase;

public class SquidGame extends Game {

  @Override
  public String getId() {
    return UUID.randomUUID().toString().substring(0, 5);
  }

  @Override
  public String getGameName() {
    return TextFormat.RED + "Squid Game";
  }

  @Override
  public String getInstruction() {
    return "You have to complete a series of microgames and the last person standing in the last microgame wins!";
  }

  @Override
  public void init() {
    saveResource("config.yml");

    moveResourcesToDataPath("skin");

    initGameSettings();

    List<GamePhase> lobbyPhases = createPreGamePhase();

    phaseSeries.addAll(lobbyPhases);
    phaseSeries.add(new GreenLightRedLight(this, Duration.ofMinutes(5))); //TODO: set map
    phaseSeries.start();

    registerPhaseCommands();
  }

  private void initGameSettings() {
    //TODO: finish this tomorrow
  }

  public File skinDataPathToFile() {
    return new File(getDataFolder() + "/skin/");
  }

  @Override
  public void close() {}
}
