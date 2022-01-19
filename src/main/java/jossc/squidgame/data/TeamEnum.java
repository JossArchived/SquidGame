package jossc.squidgame.data;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TeamEnum {
  RED("Red"),
  BLUE("Blue");

  private final String id;
}
