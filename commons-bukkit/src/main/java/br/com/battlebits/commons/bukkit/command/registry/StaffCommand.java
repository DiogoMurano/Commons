package br.com.battlebits.commons.bukkit.command.registry;

import br.com.battlebits.commons.Commons;
import br.com.battlebits.commons.account.BattleAccount;
import br.com.battlebits.commons.account.Group;
import br.com.battlebits.commons.bukkit.api.admin.AdminMode;
import br.com.battlebits.commons.bukkit.api.vanish.VanishAPI;
import br.com.battlebits.commons.bukkit.command.BukkitCommandArgs;
import br.com.battlebits.commons.command.CommandClass;
import br.com.battlebits.commons.command.CommandFramework;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static br.com.battlebits.commons.translate.TranslateTag.*;
import static br.com.battlebits.commons.translate.TranslationCommon.tl;

public class StaffCommand implements CommandClass {
    @CommandFramework.Command(name = "admin", groupToUse = Group.DEVELOPER, noPermMessageId = COMMAND_NO_PERMISSION)
    public void admin(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player p = cmdArgs.getPlayer();
            if (AdminMode.getInstance().isAdmin(p)) {
                AdminMode.getInstance().setPlayer(p);
            } else {
                AdminMode.getInstance().setAdmin(p);
            }
        }
    }

    @CommandFramework.Command(name = "updatevanish", groupToUse = Group.DEVELOPER, noPermMessageId = COMMAND_NO_PERMISSION)
    public void updateVanish(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player p = cmdArgs.getPlayer();
            VanishAPI.getInstance().updateVanishToPlayer(p);
        }
    }

    @CommandFramework.Command(name = "visible", aliases = {"vis", "visivel"}, groupToUse = Group.DEVELOPER, noPermMessageId = COMMAND_NO_PERMISSION)
    public void visible(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player p = cmdArgs.getPlayer();
            VanishAPI.getInstance().showPlayer(p);
            p.sendMessage(COMMAND_VANISH_PREFIX  + COMMAND_VANISH_VISIBLE_ALL);
        }
    }

    @CommandFramework.Command(name = "invisible", aliases = {"invis", "invisivel"}, groupToUse = Group.DEVELOPER, noPermMessageId = COMMAND_NO_PERMISSION)
    public void invisible(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player p = cmdArgs.getPlayer();
            BattleAccount battlePlayer = Commons.getAccountCommon().getBattleAccount(p.getUniqueId());
            Group group = Group.DEFAULT;
            if (cmdArgs.getArgs().length > 0) {
                try {
                    group = Group.valueOf(cmdArgs.getArgs()[0].toUpperCase());
                } catch (Exception e) {
                    p.sendMessage(COMMAND_VANISH_PREFIX + COMMAND_VANISH_RANK_NOT_EXIST);
                    return;
                }
                if (group.ordinal() >= battlePlayer.getServerGroup().ordinal()) {
                    p.sendMessage(COMMAND_VANISH_PREFIX + COMMAND_VANISH_RANK_HIGH);
                    return;
                }
            } else {
                group = VanishAPI.getInstance().hidePlayer(p);
                VanishAPI.getInstance().setPlayerVanishToGroup(p, group);
                p.sendMessage(COMMAND_VANISH_PREFIX + tl(battlePlayer.getLanguage(), COMMAND_VANISH_INVISIBLE, group.toString()));
                return;
            }
        }
    }

    @CommandFramework.Command(name = "inventorysee", aliases = {"invsee", "inv"}, groupToUse = Group.DEVELOPER, noPermMessageId = COMMAND_NO_PERMISSION)
    public void inventorysee(BukkitCommandArgs cmdArgs) {
        if (cmdArgs.isPlayer()) {
            Player p = cmdArgs.getPlayer();
            BattleAccount battlePlayer = Commons.getAccountCommon().getBattleAccount(p.getUniqueId());
            if (cmdArgs.getArgs().length == 0) {
                p.sendMessage(COMMAND_INVENTORYSEE_PREFIX + COMMAND_INVENTORYSEE_USAGE);
            } else {
                Player player = Bukkit.getPlayer(cmdArgs.getArgs()[0]);
                if (player != null) {
                    p.sendMessage(COMMAND_INVENTORYSEE_PREFIX + COMMAND_INVENTORYSEE_SUCCESS);
                    p.openInventory(player.getInventory());
                } else {
                    p.sendMessage(COMMAND_INVENTORYSEE_NOT_FOUND);
                }
            }
        }
    }
}
