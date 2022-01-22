package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import jossc.squidgame.map.MarblesMap;
import jossc.squidgame.map.feature.area.OddArea;
import jossc.squidgame.map.feature.area.PairArea;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.storage.LocalStorage;
import net.josscoder.gameapi.util.VectorUtils;

public class Marbles extends Microgame {

  public Marbles(Duration duration) {
    super(duration);
  }

  @Override
  public String getName() {
    return "Marbles";
  }

  @Override
  public String getInstruction() {
    return "Guess if the number is even or odd to get 20 marbles and win!";
  }

  @Override
  public void setupMap(Config config) {
    ConfigSection section = config.getSection("maps.marblesMap");
    ConfigSection oddSection = section.getSection("odd");
    ConfigSection pairSection = section.getSection("pair");

    OddArea oddArea = new OddArea(
      VectorUtils.stringToVector(oddSection.getString("cornerOne")),
      VectorUtils.stringToVector(oddSection.getString("cornerTwo"))
    );
    PairArea pairArea = new PairArea(
      VectorUtils.stringToVector(pairSection.getString("cornerOne")),
      VectorUtils.stringToVector(pairSection.getString("cornerTwo"))
    );

    map =
      new MarblesMap(
        game,
        section.getString("name"),
        VectorUtils.stringToVector(section.getString("safeSpawn")),
        oddArea,
        pairArea
      );
  }

  @Override
  public List<String> getScoreboardLines(User user) {
    List<String> lines = super.getScoreboardLines(user);

    int marbles = user.getLocalStorage().getInteger("marbles");
    lines.add("\uE14E Marbles " + marbles + "/20");

    return lines;
  }

  @Override
  public void onGameStart() {
    getNeutralUsers()
      .forEach(user -> user.getLocalStorage().set("marbles", 10));

    schedule(this::startThinkingOfANumber, 20 * 10);
  }

  private boolean isInPair(Player player) {
    return ((MarblesMap) map).getPairArea().isWithin(player.subtract(0, 1));
  }

  private boolean isInOdd(Player player) {
    return ((MarblesMap) map).getOddArea().isWithin(player.subtract(0, 1));
  }

  private boolean notInZone(Player player) {
    return !isInOdd(player) && !isInPair(player);
  }

  private void removeMarble(User user) {
    LocalStorage storage = user.getLocalStorage();

    storage.set("marbles", storage.getInteger("marbles") - 1);

    user.sendMessage("&l&c» -1 marble");

    if (storage.getInteger("marbles") <= 0) {
      lose(user.getPlayer());
    } else {
      broadcastSound("block.turtle_egg.drop");
    }
  }

  private void addMarble(User user) {
    LocalStorage storage = user.getLocalStorage();

    storage.set("marbles", storage.getInteger("marbles") + 2);

    user.sendMessage("&l&a» +2 marbles");

    if (storage.getInteger("marbles") >= 20) {
      win(user.getPlayer());
    } else {
      playSound(user.getPlayer(), "random.levelup", 2, 3);
    }
  }

  private void startThinkingOfANumber() {
    int number = ThreadLocalRandom.current().nextInt(1, 10);

    boolean isPair = (number % 2) == 0;
    String numberToString = (isPair ? "&aPAIR" : "&cODD");

    Predicate<? super Player> condition = player -> !isRoundWinner(player);

    broadcastMessage(
      "&l&6» &rThe number is " +
      number +
      "... it's &l" +
      numberToString +
      "&r!",
      condition
    );
    broadcastTitle(
      "&fThe number is " + number,
      "&fit's &l" + numberToString,
      condition
    );

    for (Player loser : getRoundLosers()) {
      User user = userFactory.get(loser);

      if (user == null) {
        continue;
      }

      if (isPair && isInPair(loser)) {
        addMarble(user);
      } else if (!isPair && isInOdd(loser)) {
        addMarble(user);
      } else {
        removeMarble(user);
      }
    }

    schedule(this::startThinkingOfANumber, 20 * 10);
  }

  @Override
  public void onGameUpdate() {
    getRoundLosers()
      .forEach(
        player -> {
          User user = userFactory.get(player);

          if (user != null) {
            player.sendActionBar(
              TextFormat.colorize(
                notInZone(player)
                  ? "&fNone"
                  : (isInPair(player) ? "&aPAIR" : "&cODD")
              )
            );
          }
        }
      );
  }

  @Override
  public void onGameEnd() {}
}
