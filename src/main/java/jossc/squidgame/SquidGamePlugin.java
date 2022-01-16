package jossc.squidgame;

import cn.nukkit.Player;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import jossc.squidgame.microgame.*;
import lombok.Getter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.phase.PhaseSeries;
import net.josscoder.gameapi.phase.base.EndGamePhase;
import net.josscoder.gameapi.util.VectorUtils;
import net.minikloon.fsmgasm.State;

public class SquidGamePlugin extends Game {

  @Getter
  private static SquidGamePlugin instance;

  @Getter
  private int microGamesCount = 0;

  @Getter
  private GameMap roomMap;

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
    instance = this;

    initGameSettings();

    if (isDevelopmentMode()) {
      return;
    }

    moveResourcesToDataPath("skin");

    phaseSeries = new PhaseSeries(this);

    gameMapManager.setMainMap(roomMap);

    List<GamePhase> lobbyPhases = createPreGamePhase();

    phaseSeries.addAll(lobbyPhases);
    phaseSeries.add(new RedLightGreenLight(this, Duration.ofMinutes(5)));
    phaseSeries.add(new SugarHoneycombs(this, Duration.ofMinutes(2)));
    phaseSeries.add(new NightAmbush(this, Duration.ofMinutes(2)));
    phaseSeries.add(new TugOfWar(this, Duration.ofMinutes(4)));
    phaseSeries.add(new Marbles(this, Duration.ofMinutes(5)));
    //Hopscotch
    phaseSeries.add(new SquidGame(this, Duration.ofMinutes(5)));
    phaseSeries.add(new EndGamePhase(this, Duration.ofSeconds(10), null));

    for (State phase : phaseSeries) {
      if (!(phase instanceof Microgame)) {
        continue;
      }

      ((Microgame) phase).setMicrogameCount(microGamesCount + 1);

      microGamesCount++;
    }

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

    ConfigSection waitingRoomMapSection = getConfig()
      .getSection("maps.waitingRoomMap");

    WaitingRoomMap waitingRoomMap = new WaitingRoomMap(
      this,
      waitingRoomMapSection.getString("name"),
      VectorUtils.stringToVector(waitingRoomMapSection.getString("safeSpawn")),
      VectorUtils.stringToVector(
        waitingRoomMapSection.getString("exitEntitySpawn")
      )
    );
    waitingRoomMap.setPedestalCenterSpawn(
      VectorUtils.stringToVector(
        waitingRoomMapSection.getString("pedestalCenterSpawn")
      )
    );
    waitingRoomMap.setPedestalOneSpawn(
      VectorUtils.stringToVector(
        waitingRoomMapSection.getString("pedestalOneSpawn")
      )
    );
    waitingRoomMap.setPedestalTwoSpawn(
      VectorUtils.stringToVector(
        waitingRoomMapSection.getString("pedestalTwoSpawn")
      )
    );
    waitingRoomMap.setPedestalThreeSpawn(
      VectorUtils.stringToVector(
        waitingRoomMapSection.getString("pedestalThreeSpawn")
      )
    );

    setWaitingRoomMap(waitingRoomMap);

    ConfigSection roomMapSection = getConfig().getSection("maps.roomMap");

    roomMap =
      new GameMap(
        this,
        roomMapSection.getString("name"),
        VectorUtils.stringToVector(roomMapSection.getString("safeSpawn"))
      );

    gameMapManager.addMap(roomMap);
  }

  @Override
  public void searchNewGameFor(Player player) {
    player.sendMessage("Sending packet...");
  }

  @Override
  public void sendToTheGameCenter(Player player) {
    player.sendMessage("Sending packet...");
  }

  public File skinDataPathToFile() {
    return new File(getDataFolder() + "/skin/");
  }

  @Override
  public void close() {}
}
