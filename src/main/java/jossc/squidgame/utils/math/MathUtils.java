package jossc.squidgame.utils.math;

import java.util.Random;

public class MathUtils {

  public static int randomNumber(final int min, final int max) {
    if (min >= max) {
      throw new IllegalArgumentException("max must be greater than min");
    }

    return (new Random()).nextInt((max - min) + 1) + min;
  }
}
