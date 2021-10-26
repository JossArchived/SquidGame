package jossc.squidgame.util;

import cn.nukkit.utils.TextFormat;

public class Util {

  public static String SCOREBOARD_TITLE =
    TextFormat.BOLD.toString() + TextFormat.YELLOW + "SQUID GAME";

  public static String formatTime(int time) {
    int minutes = Math.floorDiv(time, 60);
    int seconds = (int) Math.floor(time % 60);
    return (
      (minutes < 10 ? "0" : "") +
      minutes +
      ":" +
      (seconds < 10 ? "0" : "") +
      seconds
    );
  }
}
