package jossc.squidgame.utils.scoreboard.packets;

import cn.nukkit.network.protocol.DataPacket;
import jossc.squidgame.utils.scoreboard.packets.entry.ScorePacketEntry;

public class SetScorePacket extends DataPacket {

  public static final byte TYPE_CHANGE = 0x00;
  public static final byte TYPE_REMOVE = 0x01;

  public byte type;
  public ScorePacketEntry[] entries;

  @Override
  public byte pid() {
    return (byte) 0x6c;
  }

  @Override
  public void decode() {}

  @Override
  public void encode() {
    this.reset();

    this.putByte(this.type);
    this.putUnsignedVarInt(this.entries.length);

    for (ScorePacketEntry entry : this.entries) {
      this.putVarLong(entry.scoreboardId);
      this.putString(entry.objectiveName);
      this.putLInt(entry.score);

      if (this.type != SetScorePacket.TYPE_REMOVE) {
        this.putByte(entry.type);
        switch (entry.type) {
          case ScorePacketEntry.TYPE_PLAYER:
          case ScorePacketEntry.TYPE_ENTITY:
            this.putEntityUniqueId(entry.entityUniqueId);
            break;
          case ScorePacketEntry.TYPE_FAKE_PLAYER:
            this.putString(entry.customName);
            break;
        }
      }
    }
  }
}
