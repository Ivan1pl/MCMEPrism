package me.botsko.mcmeprism.appliers;

import java.util.List;

import org.bukkit.entity.Player;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actions.Handler;

public class Undo extends Preview {

    /**
     * 
     * @param plugin
     * @return
     */
    public Undo(MCMEPrism plugin, Player player, List<Handler> results, QueryParameters parameters, ApplierCallback callback) {
        super( plugin, player, results, parameters, callback );
    }

    /**
     * Set preview move and then do a rollback
     * 
     * @return
     */
    @Override
    public void preview() {
        player.sendMessage(MCMEPrism.messenger.playerError( "You can't preview an undo." ) );
    }
}