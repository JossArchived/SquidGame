package jossc.squidgame.phase;

import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.utils.Config;
import java.time.Duration;
import jossc.squidgame.entity.EntitySugarHoneycombsSnowball;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.customitem.CustomItem;
import net.josscoder.gameapi.user.User;

public class SugarHoneycombs extends Microgame {

  private CustomItem burningNeedle;

  public SugarHoneycombs(Game game, Duration duration) {
    super(game, duration);
    Entity.registerEntity(
      "EntitySugarHoneycombsSnowball",
      EntitySugarHoneycombsSnowball.class
    );

    registerBurningNeedleItem();
  }

  private void registerBurningNeedleItem() {
    burningNeedle =
      new CustomItem(
        Item.get(ItemID.BLAZE_ROD),
        "&6Burning Needle",
        ((user, player) -> {})
      );
  }

  @Override
  public String getName() {
    return "Sugar Honeycombs";
  }

  @Override
  public String getInstruction() {
    return "You will have to cut the figure to perfection before the game ends, remember that if you miss a block, your life will decrease";
  }

  @Override
  public void setupMap(Config config) {}

  @Override
  public void onGameStart() {
    giveBurningNeedle();
  }

  private void giveBurningNeedle() {
    getNeutralPlayers()
      .forEach(
        player -> {
          player.getInventory().setItem(0, burningNeedle.build());

          User user = userFactory.get(player);

          if (user != null) {
            user.updateInventory();
          }
        }
      );
  }

  @Override
  public void onGameUpdate() {}

  @Override
  public void onGameEnd() {}
}
