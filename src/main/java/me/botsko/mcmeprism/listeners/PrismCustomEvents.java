package me.botsko.mcmeprism.listeners;

import java.util.ArrayList;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionFactory;
import me.botsko.mcmeprism.actionlibs.RecordingQueue;
import me.botsko.mcmeprism.events.PrismCustomPlayerActionEvent;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class PrismCustomEvents implements Listener {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     */
    public PrismCustomEvents(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * 
     * @param event
     */
    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCustomPlayerAction(final PrismCustomPlayerActionEvent event) {
        final ArrayList<String> allowedPlugins = (ArrayList<String>) plugin.getConfig().getList(
                "prism.tracking.api.allowed-plugins" );
        if( allowedPlugins.contains( event.getPluginName() ) ) {
            RecordingQueue.addToQueue( ActionFactory.createPlayer(event.getActionTypeName(), event.getPlayer(),
                    event.getMessage()) );
        }
    }
}