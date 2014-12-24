package me.botsko.mcmeprism.commands;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.ActionType;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ActionsCommand implements SubHandler {

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {
        help( call.getSender() );
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        return null;
    }

    /**
     * Display param help
     * 
     * @param sender
     */
    private void help(CommandSender sender) {

        sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( ChatColor.GOLD + "--- Actions List ---" ) );

        // Build short list
        final ArrayList<String> shortNames = new ArrayList<String>();
        final TreeMap<String, ActionType> actions = MCMEPrism.getActionRegistry().getRegisteredAction();
        for ( final Entry<String, ActionType> entry : actions.entrySet() ) {
            if( entry.getKey().contains( "prism" ) )
                continue;
            if( shortNames.contains( entry.getValue().getShortName() ) )
                continue;
            shortNames.add( entry.getValue().getShortName() );
        }
        // Sort alphabetically
        Collections.sort( shortNames );

        // Build display of shortname list
        String actionList = "";
        int i = 1;
        for ( final String shortName : shortNames ) {
            actionList += shortName + ( i < shortNames.size() ? ", " : "" );
            i++;
        }
        sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.LIGHT_PURPLE + "Action Aliases:" + ChatColor.WHITE
                + " " + actionList ) );

        // Build display of full actions
        actionList = "";
        i = 1;
        for ( final Entry<String, ActionType> entry : actions.entrySet() ) {
            if( entry.getKey().contains( "prism" ) )
                continue;
            actionList += entry.getKey() + ( i < actions.size() ? ", " : "" );
            i++;
        }
        sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.LIGHT_PURPLE + "Full Actions:" + ChatColor.GRAY + " "
                + actionList ) );

    }
}