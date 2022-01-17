package jossc.squidgame.phase.feature.team;

import cn.nukkit.Player;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Team {

  private final String id;
  private final List<Player> members = new ArrayList<>();

  public void add(Player player) {
    members.add(player);
  }

  public boolean contains(Player player) {
    return members.contains(player);
  }

  public int countMembers() {
    return members.size();
  }
}
