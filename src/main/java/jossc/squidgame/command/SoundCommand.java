package jossc.squidgame.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.network.protocol.PlaySoundPacket;
import cn.nukkit.utils.TextFormat;

public class SoundCommand extends Command {

  public SoundCommand() {
    super(
      "sound",
      "Play a sound",
      "/sound <sound name> <optional: pitch> <optional: volume>",
      new String[] { "s" }
    );
  }

  @Override
  public boolean execute(CommandSender sender, String s, String[] args) {
    if (!(sender instanceof Player)) {
      return false;
    }

    if (args.length < 1) {
      sender.sendMessage(TextFormat.RED + "Usage: " + getUsage());

      return false;
    }

    Player player = (Player) sender;

    String soundName = args[0];

    if (args.length == 1) {
      playSound(player, soundName);
      player.sendMessage(TextFormat.DARK_GREEN + "SOUND= " + soundName);

      return true;
    }

    if (args.length == 2) {
      int pitch = Integer.parseInt(args[1]);

      playSound(player, soundName, pitch);
      player.sendMessage(
        TextFormat.BLUE + "SOUND= " + soundName + ", PITCH= " + pitch
      );

      return true;
    }

    if (args.length == 3) {
      int pitch = Integer.parseInt(args[1]);
      int volume = Integer.parseInt(args[2]);

      playSound(player, soundName, pitch, volume);
      player.sendMessage(
        TextFormat.GOLD +
        "SOUND= " +
        soundName +
        ", PITCH= " +
        pitch +
        ", VOLUME= " +
        volume
      );

      return true;
    }

    sender.sendMessage(TextFormat.RED + "Usage: " + getUsage());

    return false;
  }

  private void playSound(Player player, String soundName) {
    playSound(player, soundName, 1);
  }

  private void playSound(Player player, String soundName, int pitch) {
    playSound(player, soundName, pitch, 1);
  }

  private void playSound(
    Player player,
    String soundName,
    int pitch,
    int volume
  ) {
    PlaySoundPacket pk = new PlaySoundPacket();

    pk.name = soundName;
    pk.pitch = pitch;
    pk.volume = volume;
    pk.x = (int) player.x;
    pk.y = (int) player.y;
    pk.z = (int) player.z;

    player.dataPacket(pk);
  }
}
