package de.phillip.antiVPN;

import de.phillip.antiVPN.listeners.JoinListener;
import de.phillip.antiVPN.utils.FetchAPIUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntiVPN extends JavaPlugin {

    private FetchAPIUtils fetchAPIUtils = new FetchAPIUtils();
    public static AntiVPN instance;
    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
//        fetchAPIUtils.makeAPIRequest();

        Bukkit.getPluginManager().registerEvents(new JoinListener(), this);

    }

    public static AntiVPN getInstance() {
        return instance;
    }

    public FetchAPIUtils getFetchAPIUtils() {
        return fetchAPIUtils;
    }

    @Override
    public void onDisable() {
    }
}
