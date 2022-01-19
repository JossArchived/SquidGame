package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
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
    return "Wow you have come so far... In this game we will give you 10 marbles, you have to get 20 marbles to advance, remember that every 10 seconds you will be given a random number, you have to guess, if it is odd or even, remember that if you If you make a mistake, you will lose 1 marble, if you are right, you will win 2 marbles!, if you lose all of them, you will lose...";
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

  private void startThinkingOfANumber() {
    int actualNumber = ThreadLocalRandom.current().nextInt(1, 10);

    boolean isPair = (actualNumber % 2) == 0;
    String numberToString = (isPair ? "&aPAIR" : "&cODD");

    Predicate<? super Player> condition = player -> !isRoundWinner(player);

    broadcastMessage(
      "&l&6» &rThe number is " +
      actualNumber +
      "... it's &l" +
      numberToString +
      "&r!",
      condition
    );
    broadcastTitle(
      "&fThe number is " + actualNumber,
      "&fit's &l" + numberToString,
      condition
    );

    getRoundLosers()
      .forEach(
        player -> {
          User user = userFactory.get(player);

          if (user != null) {
            LocalStorage storage = user.getLocalStorage();

            if (isInPair(player) == isPair) {
              playSound(player, "random.levelup", 2, 3);

              storage.set("marbles", storage.getInteger("marbles") + 2);

              user.sendMessage("&l&a» +2 points");

              if (storage.getInteger("marbles") >= 20) {
                win(player);
              }
            } else {
              playSound(player, "mob.blaze.hit");

              storage.set("marbles", storage.getInteger("marbles") - 1);

              user.sendMessage("&l&c» -1 point");

              if (storage.getInteger("marbles") <= 0) {
                lose(player);
              }
            }

            map.teleportToSafeSpawn(player);
          }
        }
      );

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
              TextFormat.colorize("&fMarbles &l&e") +
              user.getLocalStorage().getInteger("marbles") +
              TextFormat.colorize(
                " &r&fOption Selected: &l" +
                (
                  notInZone(player)
                    ? "&fNone"
                    : (isInPair(player) ? "&aPAIR" : "&cODD")
                )
              )
            );
          }
        }
      );
  }

  @Override
  public void onGameEnd() {}
}
