package me.botsko.mcmeprism.commands;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import org.bukkit.ChatColor;

import java.util.List;

public class AboutCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     * @return
     */
    public AboutCommand(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {
        call.getSender().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Prism - By " + ChatColor.GOLD + "viveleroi" + ChatColor.GRAY + " v"
                        + plugin.getPrismVersion() ) );
        call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Help: " + ChatColor.WHITE + "/pr ?" ) );
        call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "IRC: " + ChatColor.WHITE + "irc.esper.net #prism" ) );
        call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Wiki: " + ChatColor.WHITE + "http://discover-prism.com" ) );
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }
}