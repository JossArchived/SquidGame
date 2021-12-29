package jossc.squidgame;

import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import jossc.squidgame.phase.GreenLightRedLight;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.util.VectorUtils;

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
    initGameSettings();

    if (isDevelopmentMode()) {
      return;
    }

    moveResourcesToDataPath("skin");

    List<GamePhase> lobbyPhases = createPreGamePhase();

    phaseSeries.addAll(lobbyPhases);
    phaseSeries.add(new GreenLightRedLight(this, Duration.ofMinutes(5)));
    phaseSeries.start();

    registerPhaseCommands();
  }

  private void initGameSettings() {
    saveResource("config.yml");

    setDevelopmentMode(getConfig().getBoolean("developmentMode"));
    setDefaultGamemode(getConfig().getInt("defaultGamemode"));
    setMaxPlayers(getConfig().getInt("maxPlayers"));
    setMinPlayers(getConfig().getInt("minPlayers"));

    setCanMoveInPreGame(true);
    setCanVoteMap(false);

    ConfigSection section = getConfig().getSection("maps.waitingRoomMap");

    WaitingRoomMap waitingRoomMap = new WaitingRoomMap(
      this,
      section.getString("name"),
      VectorUtils.stringToVector(section.getString("safeSpawn")),
      VectorUtils.stringToVector(section.getString("exitEntitySpawn"))
    );
    waitingRoomMap.setPedestalCenterSpawn(
      VectorUtils.stringToVector(section.getString("pedestalCenterSpawn"))
    );
    waitingRoomMap.setPedestalOneSpawn(
      VectorUtils.stringToVector(section.getString("pedestalOneSpawn"))
    );
    waitingRoomMap.setPedestalTwoSpawn(
      VectorUtils.stringToVector(section.getString("pedestalTwoSpawn"))
    );
    waitingRoomMap.setPedestalThreeSpawn(
      VectorUtils.stringToVector(section.getString("pedestalThreeSpawn"))
    );

    setWaitingRoomMap(waitingRoomMap);
  }

  public File skinDataPathToFile() {
    return new File(getDataFolder() + "/skin/");
  }

  @Override
  public void close() {}
}
