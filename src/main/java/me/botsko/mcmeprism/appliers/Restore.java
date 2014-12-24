package me.botsko.mcmeprism.appliers;

import java.util.List;

import org.bukkit.command.CommandSender;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actions.Handler;

public class Restore extends Preview {

    /**
     * 
     * @param plugin
     * @return
     */
    public Restore(MCMEPrism plugin, CommandSender sender, List<Handler> results, QueryParameters parameters,
            ApplierCallback callback) {
        super( plugin, sender, results, parameters, callback );
    }

    /**
     * Set preview move and then do a rollback
     * 
     * @return
     */
    @Override
    public void preview() {
        is_preview = true;
        apply();
    }
}