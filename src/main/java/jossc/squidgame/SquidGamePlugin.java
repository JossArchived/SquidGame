package jossc.squidgame;

import cn.nukkit.Player;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import com.denzelcode.form.FormAPI;
import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import jossc.squidgame.data.TeamEnum;
import jossc.squidgame.phase.*;
import lombok.Getter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.map.WaitingRoomMap;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.phase.PhaseSeries;
import net.josscoder.gameapi.phase.base.EndGamePhase;
import net.josscoder.gameapi.team.Team;
import net.josscoder.gameapi.team.Teamable;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.storage.LocalStorage;
import net.josscoder.gameapi.util.VectorUtils;
import net.minikloon.fsmgasm.State;

public class SquidGamePlugin extends Game implements Teamable {

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
    return "You have to complete a series of games and the last person standing in the last game wins!";
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

    List<GamePhase<Game>> lobbyPhases = createPreGamePhase();

    phaseSeries.addAll(lobbyPhases);
    phaseSeries.add(new RedLightGreenLight(Duration.ofMinutes(5)));
    phaseSeries.add(new SugarHoneycombs(Duration.ofMinutes(1)));
    phaseSeries.add(new NightAmbush(Duration.ofMinutes(2)));
    phaseSeries.add(new TugOfWar(Duration.ofMinutes(4)));
    phaseSeries.add(new Marbles(Duration.ofMinutes(5)));
    phaseSeries.add(new Hopscotch(Duration.ofMinutes(10)));
    phaseSeries.add(new SquidGame(Duration.ofMinutes(5)));
    phaseSeries.add(new EndGamePhase(this, Duration.ofSeconds(10), null));

    for (State phase : phaseSeries) {
      if (!(phase instanceof Microgame)) {
        continue;
      }

      ((Microgame) phase).setOrder(microGamesCount + 1);

      microGamesCount++;
    }

    phaseSeries.start();

    registerPhaseCommands();
  }

  private void initGameSettings() {
    saveDefaultConfig();

    setDevelopmentMode(getConfig().getBoolean("developmentMode"));
    setDefaultPlayerGamemode(getConfig().getInt("defaultGamemode"));
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

    addTeam(
      new Team(TeamEnum.RED.getId(), TextFormat.RED.toString(), DyeColor.RED)
    );
    addTeam(
      new Team(TeamEnum.BLUE.getId(), TextFormat.BLUE.toString(), DyeColor.BLUE)
    );
  }

  public File skinDataPathToFile() {
    return new File(getDataFolder() + "/skin/");
  }

  @Override
  public void showGameResume(Player player) {
    User user = userFactory.get(player);

    if (user == null) {
      return;
    }

    super.showGameResume(player);

    LocalStorage localStorage = user.getLocalStorage();

    int roundsWon = localStorage.getInteger("rounds_won");
    int blocksBroken = localStorage.getInteger("blocks_broken");
    int marblesTaken = localStorage.getInteger("marbles");

    FormAPI
      .modalWindowForm(
        TextFormat.BOLD.toString() + TextFormat.DARK_PURPLE + "            Game Over!",
        TextFormat.colorize(
          "&f&lYour game overview:\n\n   &b&l» &r&b" +
          roundsWon +
          " Rounds Won\n   &b&l» &r&b" +
          blocksBroken +
          " Blocks Broken\n   &b&l» &r&b" +
          marblesTaken +
          " Marbles Taken\n\n&rWe'll find a new game shortly.."
        ),
        TextFormat.BOLD.toString() + TextFormat.DARK_GRAY + "Close Overview",
        TextFormat.BOLD.toString() + TextFormat.RED + "Exit to Hub"
      )
      .addHandler(
        event -> {
          if (event.wasClosed()) {
            return;
          }

          if (!event.isAccepted()) {
            sendToHub(player);
          }
        }
      )
      .sendTo(player);
  }

  @Override
  public void close() {}
}
