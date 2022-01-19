package jossc.squidgame.util;

public class ReadUtils {

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
}
