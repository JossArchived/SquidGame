package jossc.squidgame.map;

import cn.nukkit.math.Vector3;
import jossc.squidgame.map.area.OddArea;
import jossc.squidgame.map.area.PairArea;
import lombok.Getter;
import net.josscoder.gameapi.Game;
import net.josscoder.gameapi.map.GameMap;

@Getter
public class MarblesMap extends GameMap {

  private final OddArea oddArea;
  private final PairArea pairArea;

  public MarblesMap(
    Game game,
    String name,
    Vector3 safeSpawn,
    OddArea oddArea,
    PairArea pairArea
  ) {
    super(game, name, safeSpawn);
    this.oddArea = oddArea;
    this.pairArea = pairArea;
  }
}
