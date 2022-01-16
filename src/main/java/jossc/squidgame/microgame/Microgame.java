package jossc.squidgame.microgame;

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
import cn.nukkit.utils.TextFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import jossc.squidgame.SquidGamePlugin;
import jossc.squidgame.microgame.team.ITeam;
import jossc.squidgame.microgame.team.Team;
import lombok.Getter;
import lombok.Setter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.api.event.user.UserJoinServerEvent;
import net.josscoder.gameapi.map.GameMap;
import net.josscoder.gameapi.phase.GamePhase;
import net.josscoder.gameapi.user.User;
import net.josscoder.gameapi.util.MathUtils;
import net.josscoder.gameapi.util.TimeUtils;

public abstract class Microgame extends GamePhase {

  public static String RED = "Red", BLUE = "Blue";

  public static final TextFormat[] colors = new TextFormat[] {
    TextFormat.GREEN,
    TextFormat.WHITE,
    TextFormat.DARK_GREEN
  };

  protected boolean onGameStartWasCalled = false;

  protected boolean canStartCountdown = false;

  protected int startCountdown = 11;

  @Setter
  protected int microgameCount;

  private final List<Player> roundWinners = new ArrayList<>();

  @Setter
  @Getter
  protected GameMap map = null;

  @Getter
  protected final Map<String, Team> teams = new HashMap<>();

  public Microgame(Game game, Duration duration) {
    this(game, duration, 0);
  }

  public Microgame(Game game, Duration duration, int microgameCount) {
    super(game, duration);
    this.microgameCount = microgameCount;

    setupMap(game.getConfig());
  }

  public void addTeam(Team team) {
    teams.put(team.getId(), team);
  }

  public boolean isTeam() {
    return this instanceof ITeam;
  }

  public boolean isTeamMember(Player from, Player to) {
    if (!isTeam()) {
      return false;
    }

    for (Team team : teams.values()) {
      if (team.contains(from) && team.contains(to)) {
        return true;
      }
    }

    return false;
  }

  public static int calculateTimeToReadString(String text) {
    String[] list = text.split(" ");

    int time = 0;

    for (int i = 0; i <= list.length; i++) {
      if (i % 3 == 0) {
        time++;
      }
    }

    return time;
  }

  @Override
  protected void onStart() {
    if (isTeam()) {
      addTeam(new Team(RED));
      addTeam(new Team(BLUE));
    }

    String instruction = getInstruction();

    schedule(
      () -> {
        if (instruction.isEmpty()) {
          return;
        }

        broadcastMessage(
          "&b#" +
          microgameCount +
          " &7" +
          getName() +
          " &e&l» &r&f" +
          instruction
        );
      },
      20 * 4
    );

    schedule(
      () -> canStartCountdown = true,
      20 *
      (4 + (instruction.isEmpty() ? 3 : calculateTimeToReadString(instruction)))
    );
  }

  @Override
  public void onUpdate() {
    if (canStartCountdown && startCountdown >= 0) {
      broadcastBossbar(
        "&f&lPLAYERS &b" +
        countNeutralPlayers() +
        "&f STARTING IN &b" +
        startCountdown,
        100
      );

      startCountdown--;

      if (startCountdown == 10 || startCountdown <= 5 && startCountdown > 0) {
        broadcastMessage(
          "&b&l» &r&fThis microgame will starts in &b" + startCountdown + "&f!"
        );
        broadcastSound("note.bassattack", 2, 2);

        if (startCountdown == 5 && map != null) {
          if (isTeam()) {
            teams
              .values()
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
            if (map.getSpawns().isEmpty()) {
              getOnlinePlayers()
                .forEach(
                  player -> {
                    player.setImmobile();
                    map.teleportToSafeSpawn(player);
                  }
                );
            } else {
              List<Vector3> spawns = map.getSpawns();
              Set<Integer> spawnsUsed = new HashSet<>();

              getNeutralPlayers()
                .forEach(
                  player -> {
                    spawnPlayer(player, map, spawns, spawnsUsed);
                  }
                );
            }
          }
        }

        return;
      }

      if (startCountdown == 0) {
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
          "&c&l» &r&fThis microgame will ends in &c" +
          reamingDurationToSeconds +
          "&f!"
        );
        broadcastSound("note.bassattack", 2, 2);
      }

      broadcastBossbar(
        "&l&fTHIS MICROGAME ENDS IN &b" +
        TimeUtils.timeToString(reamingDurationToSeconds),
        100
      );

      onGameUpdate();
    } else {
      getNeutralUsers()
        .forEach(
          user -> {
            user.giveDefaultAttributes();
            user.sendBossBar(
              TextFormat.BOLD.toString() +
              colors[MathUtils.nextInt(0, colors.length - 1)] +
              "PREPARING MICROGAME",
              100
            );
          }
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

    player.teleport(Position.fromObject(spawn, map.toLevel()));

    spawnsUsed.add(i);
  }

  public boolean thereIsATeamWithoutMembers() {
    for (Team team : teams.values()) {
      if (team.countMembers() <= 0) {
        return true;
      }
    }

    return false;
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

        game.end(pedestalWinners);

        return;
      }

      broadcastMessage(
        "&c&l» &r&cBad news... There were no winners in this game!"
      );

      game.end(null);

      return;
    }

    if (countNeutralPlayers() == roundWinners.size()) {
      if (microgameCount == ((SquidGamePlugin) game).getMicroGamesCount()) {
        broadcastMessage("&c&l» &r&cBad news... There was a tie!");
        game.end(null);
      } else {
        broadcastMessage("&a&l» &rAll players won this round!");
      }
    } else {
      getRoundLosers()
        .forEach(
          player -> {
            if (this instanceof NightAmbush || isTeam()) {
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

    roundWinners.clear();
    teams.clear();

    onGameEnd();

    GameMap mainMap = game.getGameMapManager().getMainMap();

    if (mainMap != null) {
      getOnlinePlayers().forEach(mainMap::teleportToSafeSpawn);
    }
  }

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

    player.sendTitle(
      TextFormat.colorize("&bYou won this game!"),
      TextFormat.WHITE + "You will continue in the next game."
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

  public Team getTeam(Player player) {
    for (Team team : teams.values()) {
      if (team.contains(player)) {
        return team;
      }
    }

    return null;
  }

  public List<Team> getSortedTeams(boolean asc) {
    return teams
      .values()
      .stream()
      .sorted(
        Comparator.comparing(
          Team::countMembers,
          asc ? Comparator.naturalOrder() : Comparator.reverseOrder()
        )
      )
      .collect(Collectors.toList());
  }

  private void giveArmor(Player player) {
    PlayerInventory inventory = player.getInventory();

    DyeColor color =
      (
        isTeam()
          ? (
            getTeam(player) == null
              ? DyeColor.GREEN
              : (
                getTeam(player).getId().equalsIgnoreCase(RED)
                  ? DyeColor.RED
                  : DyeColor.BLUE
              )
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

  @EventHandler
  @Override
  public void onJoin(UserJoinServerEvent event) {
    super.onJoin(event);
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
