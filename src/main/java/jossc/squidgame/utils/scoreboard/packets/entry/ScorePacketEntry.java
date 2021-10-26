package jossc.squidgame.utils.scoreboard.packets.entry;

public class ScorePacketEntry {

  public static final byte TYPE_PLAYER = 0x01;
  public static final byte TYPE_ENTITY = 0x02;
  public static final byte TYPE_FAKE_PLAYER = 0x03;

  public int scoreboardId;
  public String objectiveName;
  public int score;

  public byte type;

  public int entityUniqueId;
  public String customName;
}
