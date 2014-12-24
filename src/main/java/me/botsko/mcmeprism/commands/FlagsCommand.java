package me.botsko.mcmeprism.commands;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.Flag;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class FlagsCommand implements SubHandler {

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

        sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( ChatColor.GOLD + "--- Flags Help ---" ) );

        sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.GRAY
                + "Flags control how Prism applies a rollback/restore, or formats lookup results." ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.GRAY
                + "Use them after parameters, like /pr l p:viveleroi -extended" ) );
        for ( final Flag flag : Flag.values() ) {
            sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.LIGHT_PURPLE + flag.getUsage().replace( "_", "-" )
                    + ChatColor.WHITE + " " + flag.getDescription() ) );
        }
    }
}