package de.phillip.antiVPN.listeners;

import de.phillip.antiVPN.AntiVPN;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        String ip = p.getAddress().getAddress().getHostAddress();
        AntiVPN.getInstance().getFetchAPIUtils().makeAPIRequest(ip, p.getName());
    }
}
