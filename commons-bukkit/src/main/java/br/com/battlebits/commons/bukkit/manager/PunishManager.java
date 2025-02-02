package br.com.battlebits.commons.bukkit.manager;

import br.com.battlebits.commons.Commons;
import br.com.battlebits.commons.account.BattleAccount;
import br.com.battlebits.commons.account.Group;
import br.com.battlebits.commons.account.punishment.Ban;
import br.com.battlebits.commons.translate.TranslateTag;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import static br.com.battlebits.commons.translate.TranslateTag.*;
import static br.com.battlebits.commons.translate.TranslationCommon.tl;

public class PunishManager {

    private Cache<String, Map.Entry<UUID, Ban>> banCache;

    public PunishManager() {
        banCache = CacheBuilder.newBuilder().expireAfterWrite(30L, TimeUnit.MINUTES).build(new CacheLoader<String, Map.Entry<UUID, Ban>>() {
            @Override
            public Map.Entry<UUID, Ban> load(String key) throws Exception {
                return null;
            }
        });
    }

    public void ban(BattleAccount player, Ban ban) {
        player.getPunishmentHistory().getBanHistory().add(ban);
        for (Player online : Bukkit.getOnlinePlayers()) {
            BattleAccount battleAccount = Commons.getAccountCommon().getBattleAccount(online.getUniqueId());
            if (battleAccount != null && battleAccount.hasGroupPermission(Group.ADMIN)) {
                String banSuccess = "";
                if (ban.isPermanent()) {
                    banSuccess = tl(COMMAND_BAN_PREFIX) + tl(COMMAND_BAN_SUCCESS, player.getUniqueId().toString().toString().replace("-", ""), ban.getBannedBy(), ban.getDuration());
                } else {
                    //TODO: with tempban
                }
                online.sendMessage(banSuccess);
            }
            Commons.getDataAccount().saveAccount(player, "punishmentHistoric");
            Bukkit.getPlayer(player.getUniqueId()).kickPlayer("Voce foi banido!"); //remove this later
        }
    }
}
