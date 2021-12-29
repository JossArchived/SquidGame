package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.function.Predicate;
import jossc.squidgame.SquidGame;
import jossc.squidgame.map.GreenLightRedLightMap;
import jossc.squidgame.util.ParticleUtils;
import lombok.NonNull;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.PlayerRequestToLoseEvent;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.MathUtils;
import net.josscoder.gameapi.util.VectorUtils;
import org.citizen.attributes.CitizenSkin;
import org.citizen.attributes.InvokeAttribute;
import org.citizen.entity.Citizen;

public class GreenLightRedLight extends Microgame {

  private boolean canWalk = true;

  private Citizen doll = null;

  public GreenLightRedLight(Game game, Duration duration) {
    super(game, duration);
    generateDoll();
  }

  @Override
  public String getName() {
    return "Red light & Green light";
  }

  @Override
  public String getInstruction() {
    return "To win you must reached the goal";
  }

  @Override
  public void setupMap(Config config) {
    ConfigSection section = config.getSection("maps.greenLightRedLightMap");

    map =
      new GreenLightRedLightMap(
        game,
        section.getString("name"),
        VectorUtils.stringToVector(section.getString("safeSpawn")),
        VectorUtils.stringToVector(section.getString("goalCornerOne")),
        VectorUtils.stringToVector(section.getString("goalCornerTwo")),
        VectorUtils.stringToVector(section.getString("dollPosition"))
      );
  }

  @Override
  public void onGameStart() {
    spawnDoll();
    singDoll();
  }

  private void generateDoll() {
    if (!(map instanceof GreenLightRedLightMap)) {
      return;
    }

    Vector3 dollPosition = ((GreenLightRedLightMap) map).getDollPosition();

    if (dollPosition == null) {
      return;
    }

    doll = new Citizen();
    doll.setPosition(
      Position.fromObject(dollPosition.add(0.5, 1, 0.5), map.toLevel())
    );
    doll.setSkin(
      CitizenSkin.from(
        ((SquidGame) game).skinDataPathToFile().toPath().resolve("doll.png")
      )
    );
    doll.setScale(3.5f);
    doll.setInvokeAttribute(
      new InvokeAttribute(doll) {
        @Override
        public void invoke(@NonNull Player player) {
          player.sendMessage(TextFormat.RED + "Do not touch me....");
        }
      }
    );
    doll.lookAt(map.getSafeSpawn());

    game.getCitizenLibrary().getFactory().add(doll);
  }

  private void spawnDoll() {
    if (doll == null) {
      return;
    }

    getOnlinePlayers().forEach(player -> doll.spawnTo(player));
  }

  private void singDoll() {
    int time = MathUtils.nextInt(2, 5);

    Predicate<? super Player> condition = player ->
      !roundWinners.contains(player);

    broadcastMessage("&l&a» &r&fGreen light!", condition);
    broadcastTitle("&aGreen Light!", "&fYou can move.", condition);
    broadcastSound("mob.ghast.moan", condition);

    if (doll != null) {
      Vector3 newVector = doll
        .getPosition()
        .subtract(map.getSafeSpawn())
        .asVector3f()
        .asVector3();

      doll.lookAt(newVector);
    }

    giveGreenWool();

    canWalk = true;

    schedule(
      () -> {
        broadcastMessage("&l&c» &r&fRed light!", condition);
        broadcastTitle("&cRed Light!", "You can not move.", condition);
        broadcastSound("mob.blaze.hit", condition);

        if (doll != null) {
          doll.lookAt(map.getSafeSpawn());
        }

        giveRedWool();

        schedule(
          () -> {
            canWalk = false;
            int waitTime = MathUtils.nextInt(2, 5);

            schedule((this::singDoll), waitTime * 20);
          },
          20
        );
      },
      time * 20
    );
  }

  private void giveWool(int meta) {
    getRoundLosers()
      .forEach(
        loser -> {
          for (int i = 0; i <= 8; i++) {
            loser.getInventory().setItem(i, Item.get(Item.WOOL, meta));
          }
          User user = userFactory.get(loser);

          if (user != null) {
            user.updateInventory();
          }
        }
      );
  }

  private void giveGreenWool() {
    giveWool(5);
  }

  private void giveRedWool() {
    giveWool(14);
  }

  @EventHandler
  public void onRequestToDeath(PlayerRequestToLoseEvent event) {
    if (event.getLoseCause() != PlayerRequestToLoseEvent.LoseCause.VOID) {
      lose(event.getPlayer());
    }
  }

  @EventHandler
  public void onMove(PlayerMoveEvent event) {
    if (!(map instanceof GreenLightRedLightMap)) {
      return;
    }

    Player player = event.getPlayer();

    Vector3 playerPosition = player.asVector3f().asVector3();

    if (
      !roundWinners.contains(player) &&
      !canWalk &&
      !isReadyToEnd() &&
      !player.isSpectator()
    ) {
      lose(player, true);

      return;
    }

    if (
      ((GreenLightRedLightMap) map).isTheGoal(playerPosition) &&
      !roundWinners.contains(player)
    ) {
      win(player);
    }
  }

  @Override
  public void lose(Player player, boolean teleport) {
    if (doll != null) {
      ParticleUtils.fireShoot(
        doll.getPosition().add(0, 1.5),
        player.getPosition()
      );
    }
    //TODO: verify if this is work

    super.lose(player, teleport);
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {
    despairDoll();
  }

  private void despairDoll() {
    getOnlinePlayers().forEach(player -> doll.despairFrom(player));
  }
}
