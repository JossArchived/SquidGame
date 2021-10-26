package jossc.squidgame.utils.scoreboard;

import cn.nukkit.Player;
import java.util.*;
import java.util.stream.Collectors;
import jossc.squidgame.utils.scoreboard.packets.RemoveObjectivePacket;
import jossc.squidgame.utils.scoreboard.packets.SetDisplayObjectivePacket;
import jossc.squidgame.utils.scoreboard.packets.SetScorePacket;
import jossc.squidgame.utils.scoreboard.packets.entry.ScorePacketEntry;

public class ScoreboardBuilder {

  private final Map<String, String> displayedTexts = new HashMap<>();

  private final Map<String, String[]> viewers = new HashMap<>();

  private final Map<String, String> titles = new HashMap<>();

  public static int ASCENDING = 0;
  public static int DESCENDING = 1;

  public void send(Player player, String text) {
    send(player, text, DESCENDING);
  }

  public void send(Player player, String text, int type) {
    displayedTexts.put(player.getName(), text);

    text = formatLines(text);
    text = removeDuplicateLines(text);

    String[] splitText = text.split("\n");
    String title = splitText[0];

    splitText = Arrays.copyOfRange(splitText, 1, splitText.length);

    if (
      (!titles.containsKey(player.getName())) ||
      (!titles.get(player.getName()).equals(title))
    ) {
      if (titles.containsKey(player.getName())) {
        remove(player);
      }

      createScoreBoard(player, title, type);
    }

    if (!viewers.containsKey(player.getName())) {
      sendLines(player, splitText);
      viewers.put(player.getName(), splitText);
      return;
    }

    updateLines(player, viewers.get(player.getName()), splitText);
    viewers.put(player.getName(), splitText);
  }

  public void remove(Player player) {
    if (!titles.containsKey(player.getName())) {
      return;
    }

    titles.remove(player.getName());
    viewers.remove(player.getName());
    displayedTexts.remove(player.getName());

    RemoveObjectivePacket pk = new RemoveObjectivePacket();
    pk.objectiveName = player.getName().toLowerCase();

    player.dataPacket(pk);
  }

  public boolean hasObjectiveDisplayed(Player player) {
    return titles.containsKey(player.getName());
  }

  public String getDisplayedText(Player player) {
    return displayedTexts.getOrDefault(player.getName(), "");
  }

  public void createScoreBoard(Player player, String title, int type) {
    SetDisplayObjectivePacket pk = new SetDisplayObjectivePacket();
    pk.objectiveName = player.getName().toLowerCase();
    pk.displayName = title;
    pk.sortOrder = type;
    pk.criteriaName = "dummy";
    pk.displaySlot = "sidebar";

    player.dataPacket(pk);
  }

  public void sendLines(Player player, String[] splitText) {
    sendLines(player, splitText, null);
  }

  public void sendLines(Player player, String[] splitText, int[] filter) {
    int entryCount = splitText.length;
    if (filter != null) {
      List<Integer> filterList = Arrays
        .stream(filter)
        .boxed()
        .collect(Collectors.toList()); // int[] to List<Integer> convert
      entryCount = 0;

      for (int i = 0; i < splitText.length; i++) {
        if (!filterList.contains(i)) {
          splitText[i] = null;
          continue;
        }
        entryCount++;
      }
    }

    ScorePacketEntry[] entries = new ScorePacketEntry[entryCount];

    int j = 0;
    for (int i = 0; i < splitText.length; i++) {
      String line = splitText[i];
      if (line != null) {
        ScorePacketEntry entry = new ScorePacketEntry();
        entry.objectiveName = player.getName().toLowerCase();
        entry.scoreboardId = i + 1;
        entry.score = i + 1;
        entry.type = ScorePacketEntry.TYPE_FAKE_PLAYER;
        entry.customName = line;

        entries[j++] = entry;
      }
    }

    SetScorePacket pk = new SetScorePacket();
    pk.type = SetScorePacket.TYPE_CHANGE;
    pk.entries = entries;

    player.dataPacket(pk);
  }

  public void updateLines(
    Player player,
    String[] oldSplitText,
    String[] splitText
  ) {
    if (oldSplitText.length == splitText.length) {
      List<Integer> updateList = new ArrayList<>();

      for (int i = 0; i < splitText.length; i++) {
        if (
          oldSplitText[i] == null ||
          splitText[i] == null ||
          !oldSplitText[i].equals(splitText[i])
        ) {
          updateList.add(i);
        }
      }

      int[] updates = updateList.stream().mapToInt(i -> i).toArray();

      removeLines(player, updates);
      sendLines(player, splitText, updates);
      return;
    }

    if (oldSplitText.length > splitText.length) {
      List<Integer> updateList = new ArrayList<>();

      for (int i = 0; i < oldSplitText.length; i++) {
        if (i >= splitText.length || splitText[i] == null) {
          updateList.add(i);
          continue;
        }

        if (!splitText[i].equals(oldSplitText[i])) {
          updateList.add(i);
        }
      }

      int[] lineUpdates = updateList.stream().mapToInt(i -> i).toArray();

      removeLines(player, lineUpdates);
      sendLines(player, splitText, lineUpdates);
      return;
    }

    List<Integer> toRemove = new ArrayList<>();
    List<Integer> toSend = new ArrayList<>();

    for (int i = 0; i < splitText.length; i++) {
      String line = splitText[i];

      if (i >= oldSplitText.length || oldSplitText[i] == null) {
        toSend.add(i);
        continue;
      }

      if (!oldSplitText[i].equals(line)) {
        toRemove.add(i);
        toSend.add(i);
      }
    }

    removeLines(player, toRemove.stream().mapToInt(j -> j).toArray());
    sendLines(player, splitText, toSend.stream().mapToInt(j -> j).toArray());
  }

  public void removeLines(Player player, int[] lines) {
    ScorePacketEntry[] entries = new ScorePacketEntry[lines.length];
    for (int i = 0; i < lines.length; i++) {
      int lineNumber = lines[i];

      ScorePacketEntry entry = new ScorePacketEntry();
      entry.objectiveName = player.getName().toLowerCase();
      entry.scoreboardId = lineNumber + 1;
      entry.score = lineNumber + 1;

      entries[i] = entry;
    }

    SetScorePacket pk = new SetScorePacket();
    pk.type = SetScorePacket.TYPE_REMOVE;
    pk.entries = entries;

    player.dataPacket(pk);
  }

  public String removeDuplicateLines(String text) {
    String[] lines = text.split("\n");

    List<String> used = new ArrayList<>();
    int i = 0;
    for (String line : lines) {
      if (i == 0) {
        i++;
        continue;
      }

      while (used.contains(line)) {
        line += " ";
      }

      lines[i] = line;
      used.add(line);
      i++;
    }

    return String.join("\n", lines);
  }

  public String formatLines(String text) {
    String[] lines = text.split("\n");
    int i = 0;
    for (String line : lines) {
      if (i == 0) {
        i++;
        continue;
      }

      lines[i] = " " + line + " ";
      i++;
    }

    return String.join("\n", lines);
  }
}
