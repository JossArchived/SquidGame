package jossc.squidgame.utils.bossbar;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.network.protocol.*;
import cn.nukkit.utils.TextFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.Getter;

@Getter
public class BossbarBuilder {

  protected String title;

  private float percent;

  protected long entityId;

  protected List<Player> viewers;

  public BossbarBuilder() {
    this("");
  }

  public BossbarBuilder(String title) {
    this(title, 1F);
  }

  public BossbarBuilder(String title, float percent) {
    this.title = TextFormat.colorize(title);
    this.percent = percent;

    entityId = Entity.entityCount++;

    viewers = new ArrayList<>();
  }

  public BossbarBuilder setTitle(String title) {
    setTitle(title, false);

    return this;
  }

  public BossbarBuilder setTitle(String title, boolean update) {
    this.title = TextFormat.colorize(title);

    if (update) {
      updateToAll();
    }

    return this;
  }

  public BossbarBuilder setPercent(float percent) {
    setPercent(percent, false);

    return this;
  }

  public BossbarBuilder setPercent(float percent, boolean update) {
    this.percent = percent;

    if (update) {
      updateToAll();
    }

    return this;
  }

  public UpdateAttributesPacket getPercentPacket() {
    UpdateAttributesPacket pk = new UpdateAttributesPacket();
    pk.entityId = entityId;

    Attribute attribute = Attribute.getAttribute(4);
    attribute.setMaxValue(100.0F);
    attribute.setValue(percent);
    pk.entries = new Attribute[] { attribute };

    return pk;
  }

  public void updateToAll() {
    viewers.forEach(this::updateTo);
  }

  public void updateTo(Player player) {
    updateTo(player, title);
  }

  public void updateTo(Player player, String title) {
    BossEventPacket pk = new BossEventPacket();
    pk.bossEid = entityId;
    pk.type = BossEventPacket.TYPE_TITLE;
    pk.title = title;

    BossEventPacket pk2 = new BossEventPacket();
    pk2.bossEid = entityId;
    pk2.type = BossEventPacket.TYPE_HEALTH_PERCENT;
    pk2.healthPercent = percent;

    player.dataPacket(pk);
    player.dataPacket(pk2);
    player.dataPacket(getPercentPacket());

    SetEntityDataPacket pk3 = new SetEntityDataPacket();
    pk3.eid = entityId;
    pk3.metadata = new EntityMetadata().putString(Entity.DATA_NAMETAG, title);

    player.dataPacket(pk3);
  }

  public BossbarBuilder buildToAll() {
    viewers.forEach(this::buildTo);

    return this;
  }

  public BossbarBuilder buildTo(Player player) {
    buildTo(player, true);

    return this;
  }

  public BossbarBuilder buildTo(Player player, boolean viewer) {
    int CREEPER_LEGACY_ID = 33;

    AddEntityPacket pk = new AddEntityPacket();
    pk.type = CREEPER_LEGACY_ID;
    pk.entityUniqueId = entityId;
    pk.entityRuntimeId = entityId;
    pk.x = (float) player.getX();
    pk.y = -10.0F;
    pk.z = (float) player.getZ();
    pk.speedX = 0F;
    pk.speedY = 0F;
    pk.speedZ = 0F;
    pk.metadata =
      (new EntityMetadata()).putLong(0, 0L)
        .putShort(7, 400)
        .putShort(42, 400)
        .putLong(37, -1L)
        .putString(4, title)
        .putFloat(38, 0.0F);
    player.dataPacket(pk);

    player.dataPacket(getPercentPacket());
    if (viewer) {
      viewers.add(player);
    }

    return this;
  }

  public void showToAll() {
    viewers.forEach(this::showTo);
  }

  public void showTo(Player player) {
    BossEventPacket pk = new BossEventPacket();
    pk.bossEid = entityId;
    pk.type = BossEventPacket.TYPE_SHOW;
    pk.title = title;
    pk.healthPercent = percent;
    pk.color = 0;
    pk.overlay = 0;

    player.dataPacket(pk);
  }

  public void hideFromAll() {
    viewers.forEach(this::hideFrom);
  }

  public void hideFrom(Player player) {
    BossEventPacket pk = new BossEventPacket();
    pk.bossEid = entityId;
    pk.type = BossEventPacket.TYPE_HIDE;

    player.dataPacket(pk);
  }

  public void removeFromAll() {
    viewers.forEach(this::removeFrom);
  }

  public void removeFrom(Player player) {
    viewers.remove(player);

    hideFrom(player);

    RemoveEntityPacket pk = new RemoveEntityPacket();
    pk.eid = entityId;

    player.dataPacket(pk);
  }

  public void addAll(Player... players) {
    Arrays.stream(players).forEach(this::add);
  }

  public void add(Player player) {
    if (viewers.contains(player)) {
      return;
    }

    viewers.add(player);
  }
}
