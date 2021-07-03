package plugily.projects.murdermystery.commands.arguments.admin;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import plugily.projects.murdermystery.commands.arguments.ArgumentsRegistry;
import plugily.projects.murdermystery.commands.arguments.data.CommandArgument;
import plugily.projects.murdermystery.commands.arguments.data.LabelData;
import plugily.projects.murdermystery.commands.arguments.data.LabeledCommandArgument;
import plugily.projects.murdermystery.user.User;

/**
 * @author Tigerpanzer_02
 * <p>
 * Created at 30.06.2020
 */

public class SpectateArgument {

  public SpectateArgument(ArgumentsRegistry registry) {
    registry.mapArgument("murdermysteryadmin", new LabeledCommandArgument("spectate", "murdermystery.admin.spectate", CommandArgument.ExecutorType.PLAYER,
      new LabelData("/mma spectate", "/mma spectate", "&7Enable/Disable permanent spectator mode\n&6Permission: &7murdermystery.admin.spectate")) {
      @Override
      public void execute(CommandSender sender, String[] args) {
        User user = registry.getPlugin().getUserManager().getUser((Player) sender);
        user.setPermanentSpectator(!user.isPermanentSpectator());
      }
    });
  }

}
