package jossc.squidgame.phase;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.block.*;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.inventory.*;
import cn.nukkit.item.ItemBootsLeather;
import cn.nukkit.item.ItemChestplateLeather;
import cn.nukkit.item.ItemLeggingsLeather;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import jossc.squidgame.SquidGamePlugin;
import jossc.squidgame.util.ReadUtils;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.user.storage.LocalStorage;
import net.josscoder.gameapi.util.MathUtils;
import net.josscoder.gameapi.util.TimeUtils;

public abstract class Microgame extends GamePhase<SquidGamePlugin> {

  protected boolean onGameStartWasCalled = false;

  protected boolean canStartCountdown = false;

  protected int startCountdown = 11;

  @Setter
  protected int order = 0;

  private final List<Player> roundWinners = new ArrayList<>();

  @Setter
  @Getter
  protected GameMap map = null;

  public Microgame(Duration duration) {
    super(SquidGamePlugin.getInstance(), duration);
    setupMap(game.getConfig());
  }

  @Override
  protected void onStart() {
    //cleanupNeutralPlayers();

    String instruction = getInstruction();

    schedule(
      () -> {
        if (instruction.isEmpty()) {
          return;
        }

        broadcastMessage("&d&l» &e#" + order + "&b " + getName());
        broadcastMessage("&l&7» &r" + instruction);
        //cleanupNeutralPlayers();
      },
      20 * 4
    );

    schedule(
      () -> canStartCountdown = true,
      20 *
      (
        4 +
        (
          instruction.isEmpty()
            ? 3
            : ReadUtils.calculateTimeToReadString(instruction)
        )
      )
    );
  }

  private void spawnPlayers() {
    if (game.isTeamable() && this instanceof TugOfWar) {
      game
        .getTeams()
        .forEach(
          team ->
            team
              .getMembers()
              .forEach(
                player -> {
                  List<Vector3> spawns = map.getSpawns(team.getId());
                  Set<Integer> spawnsUsed = new HashSet<>();

                  spawnPlayer(player, map, spawns, spawnsUsed);
                }
              )
        );
    } else {
      if (map.getSpawns(GameMap.SOLO).isEmpty()) {
        getOnlinePlayers()
          .forEach(
            player -> {
              player.setImmobile();
              map.teleportToSafeSpawn(player);
            }
          );
      } else {
        List<Vector3> spawns = map.getSpawns(GameMap.SOLO);
        Set<Integer> spawnsUsed = new HashSet<>();

        getNeutralPlayers()
          .forEach(player -> spawnPlayer(player, map, spawns, spawnsUsed));
      }
    }
  }

  private void preparePlayers() {
    getNeutralUsers()
      .forEach(
        user -> {
          Player player = user.getPlayer();

          if (player != null) {
            player.setImmobile(false);
            user.giveDefaultAttributes();
            giveArmor(player);
          }
        }
      );
  }

  public List<String> getScoreboardLines(User user) {
    List<String> lines = new ArrayList<>();

    lines.add(
      "\uE112 Ending in " +
      TimeUtils.timeToString((int) getRemainingDuration().getSeconds())
    );

    return lines;
  }

  @Override
  public void onUpdate() {
    if (canStartCountdown && startCountdown >= 0) {
      broadcastScoreboard(
        "",
        player -> getNeutralPlayers().contains(player),
        "\uE181 " + order + "/" + game.getMicroGamesCount(),
        "\uE105 " + countNeutralPlayers(),
        "\uE142 Starting in " + startCountdown
      );

      startCountdown--;

      if (startCountdown == 10 || startCountdown <= 5 && startCountdown > 0) {
        broadcastMessage(
          "&b&l» &r&fThis game will starts in &b" + startCountdown + "&f!"
        );
        broadcastSound("liquid.lavapop", 0.9f, 1);

        if (startCountdown == 5 && map != null) {
          spawnPlayers();
        }

        return;
      }

      if (startCountdown == 0) {
        preparePlayers();
        onGameStart();
        onGameStartWasCalled = true;
      }
    } else if (onGameStartWasCalled) {
      int reamingDurationToSeconds = (int) getRemainingDuration().getSeconds();

      if (
        reamingDurationToSeconds == 20 ||
        reamingDurationToSeconds == 15 ||
        reamingDurationToSeconds == 10 ||
        reamingDurationToSeconds <= 5 &&
        reamingDurationToSeconds > 0
      ) {
        broadcastMessage(
          "&c&l» &r&fThis game will ends in &c" +
          reamingDurationToSeconds +
          "&f!"
        );
        broadcastSound("liquid.lavapop", 0.9f, 3);
      }
      getNeutralUsers()
        .forEach(
          user -> {
            String[] lines = getScoreboardLines(user).toArray(new String[0]);

            user.sendScoreboard("", lines);
          }
        );
      onGameUpdate();
    } else {
      broadcastScoreboard(
        "",
        player -> getNeutralPlayers().contains(player),
        "\uE181 " + order + "/" + game.getMicroGamesCount(),
        "\uE105 " + countNeutralPlayers()
      );
    }
  }

  public boolean isRoundWinner(Player player) {
    return roundWinners.contains(player);
  }

  public List<Player> getRoundLosers() {
    return getNeutralPlayers()
      .stream()
      .filter(player -> !isRoundWinner(player))
      .collect(Collectors.toList());
  }

  private void spawnPlayer(
    Player player,
    GameMap map,
    List<Vector3> spawns,
    Set<Integer> spawnsUsed
  ) {
    int i;

    do {
      i = MathUtils.nextInt(spawns.size());
    } while (spawnsUsed.contains(i));

    Vector3 spawn = spawns.get(i);

    player.setImmobile();

    map.prepare();

    player.teleport(Position.fromObject(spawn, map.toLevel()));

    spawnsUsed.add(i);
  }

  @Override
  public boolean isReadyToEnd() {
    return (
      super.isReadyToEnd() ||
      countNeutralPlayers() <= 1 ||
      countNeutralPlayers() == roundWinners.size()
    );
  }

  @Override
  protected void onEnd() {
    if (countNeutralPlayers() <= 1) {
      if (countNeutralPlayers() == 1) {
        Player winner = getNeutralPlayers().get(0);

        broadcastMessage(
          "&d&l» &r&fGood news... " +
          winner.getName() +
          " is the winner, congrats!"
        );

        Map<Player, Integer> pedestalWinners = new HashMap<>();
        pedestalWinners.put(winner, 1);

        //cleanupNeutralPlayers();
        game.end(pedestalWinners);

        return;
      }

      broadcastMessage(
        "&c&l» &r&cBad news... There were no winners in this game!"
      );

      //cleanupNeutralPlayers();
      game.end(null);

      return;
    }

    if (countNeutralPlayers() == roundWinners.size()) {
      if (order == game.getMicroGamesCount()) {
        broadcastMessage("&c&l» &r&cBad news... There was a tie!");
        //cleanupNeutralPlayers();
        game.end(null);
      } else {
        broadcastMessage("&a&l» &rAll players won this round!");
        broadcastSound("random.levelup", 1, 3);
        //cleanupNeutralPlayers();
      }
    } else {
      getRoundLosers()
        .forEach(
          player -> {
            if (this instanceof NightAmbush || this instanceof TugOfWar) {
              User user = userFactory.get(player);

              if (user != null) {
                user.giveDefaultAttributes();
              }
            } else if (getRemainingDuration().getSeconds() <= 0) {
              lose(player, false);
            }
          }
        );
    }

    //cleanupNeutralPlayers();

    roundWinners.clear();

    onGameEnd();

    GameMap mainMap = game.getGameMapManager().getMainMap();

    if (mainMap != null) {
      getOnlinePlayers().forEach(mainMap::teleportToSafeSpawn);
    }
  }

  /*private void cleanupNeutralPlayers() {
    getNeutralUsers()
      .forEach(
        user -> {
          user.giveDefaultAttributes();
          user.removeScoreboard();
          user.removeBossBar();
        }
      );
  }*/

  public abstract String getName();

  public abstract String getInstruction();

  public abstract void setupMap(Config config);

  public abstract void onGameStart();

  public abstract void onGameUpdate();

  public abstract void onGameEnd();

  public void win(Player player) {
    User user = userFactory.get(player);

    if (user == null || player.getGamemode() == Player.SPECTATOR) {
      return;
    }

    roundWinners.add(player);

    user.giveDefaultAttributes();

    LocalStorage localStorage = user.getLocalStorage();

    localStorage.set("rounds_won", localStorage.getInteger("rounds_won") + 1);

    broadcastMessage(
      "&l&b» &r&fPlayer &b" + player.getName() + "&f won this game!"
    );

    playSound(player, "random.levelup", 2, 3);
  }

  public void lose(Player player) {
    lose(player, true);
  }

  public void lose(Player player, boolean teleport) {
    User user = userFactory.get(player);

    if (user == null || player.getGamemode() == Player.SPECTATOR) {
      return;
    }

    broadcastMessage(
      "&l&6» &r&fPlayer &6" + countNeutralPlayers() + "&f was eliminated!"
    );
    broadcastSound("mob.guardian.death");

    if (map != null && teleport) {
      map.teleportToSafeSpawn(player);
    }

    user.convertSpectator(true, false);
  }

  private void giveArmor(Player player) {
    PlayerInventory inventory = player.getInventory();

    boolean isTugOfWar = (this instanceof TugOfWar);

    DyeColor color =
      (
        isTugOfWar
          ? (
            game.getTeam(player) == null
              ? DyeColor.GREEN
              : (game.getTeam(player).getDyeColor())
          )
          : DyeColor.GREEN
      );

    ItemChestplateLeather chestPlateLeather = new ItemChestplateLeather();
    chestPlateLeather.setColor(color);

    ItemLeggingsLeather leggingsLeather = new ItemLeggingsLeather();
    leggingsLeather.setColor(color);

    ItemBootsLeather bootsLeather = new ItemBootsLeather();
    bootsLeather.setColor(color);

    inventory.setChestplate(chestPlateLeather);
    inventory.setLeggings(leggingsLeather);
    inventory.setBoots(bootsLeather);

    User user = userFactory.get(player);

    if (user != null) {
      user.updateInventory();
    }
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockPlace(BlockPlaceEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBreak(BlockBreakEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onBlockBurn(BlockBurnEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemDrop(PlayerDropItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onDamage(EntityDamageEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onEntitySpawn(CreatureSpawnEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onFoodLevelChange(PlayerFoodLevelChangeEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onIgnite(BlockIgniteEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onSpread(BlockSpreadEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onLeavesDecay(LeavesDecayEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onLiquidFlow(LiquidFlowEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityExplode(EntityExplodeEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onEntityExplosionPrimed(EntityExplosionPrimeEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onItemFrameDrop(ItemFrameDropItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPickupItem(InventoryPickupItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBedEnter(PlayerBedEnterEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBedLeave(PlayerBedLeaveEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onBucketFill(PlayerBucketFillEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onOpenCraftingTable(CraftingTableOpenEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onInventoryOpen(InventoryOpenEvent event) {
    Inventory inventory = event.getInventory();
    if (
      inventory instanceof AnvilInventory ||
      inventory instanceof BeaconInventory ||
      inventory instanceof BrewingInventory ||
      inventory instanceof CraftingGrid ||
      inventory instanceof DispenserInventory ||
      inventory instanceof DropperInventory ||
      inventory instanceof EnchantInventory ||
      inventory instanceof FurnaceInventory ||
      inventory instanceof FurnaceRecipe ||
      inventory instanceof HopperInventory ||
      inventory instanceof ShapedRecipe ||
      inventory instanceof ShapelessRecipe ||
      inventory instanceof MixRecipe
    ) {
      event.setCancelled();
    }
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onDeath(PlayerDeathEvent event) {
    event.setDeathMessage("");
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onCraftItem(CraftItemEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
    event.setCancelled();
  }

  @EventHandler(priority = EventPriority.NORMAL)
  public void onLevelFoodChange(PlayerFoodLevelChangeEvent event) {
    event.setCancelled();
  }
}
