package me.botsko.mcmeprism.commands;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

public class HelpCommand implements SubHandler {

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
     * Displays help
     * 
     * @param sender
     */
    protected void help(CommandSender sender) {

        sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( ChatColor.GOLD + "--- Basic Usage ---" ) );

        sender.sendMessage(MCMEPrism.messenger.playerHelp( "i", "Toggle the inspector wand." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(l|lookup) (params)", "Search the database." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "tp (#|id:#)", "Teleport to a lookup result." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "near", "Find all changes nearby." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "pg (#|next|prev)", "Navigate lookup results." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "params", "List parameter help." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "actions", "List actions." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "flags", "List possible applier flags." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(preview|pv) (rollback|rb) (params)", "Preview a rollback." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(preview|pv) (restore|rs) (params)", "Preview a restoration." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(preview|pv) apply", "Apply the last preview." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(preview|pv) cancel", "Cancel the last preview." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(rollback|rb) (params)", "Rollback changes." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(restore|rs) (params)", "Re-apply changes." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(w|wand) profile", "Toggle the profile wand." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(w|wand) rollback", "Toggle the rollback wand." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(w|wand) restore", "Toggle the restore wand." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(w|wand) off", "Disable current wand." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "undo", "Undo a drain." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "ex (r)", "Extinguish fires within a (r)adius." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "drain (r)", "Drain water/lava within a (r)adius." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "delete (params)",
                "Purge records based on (params). No defaults!" ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "setmy wand mode (hand|item|block)",
                "Set your personal wand mode." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "setmy wand item (item id)",
                "Set your personal wand item/block id:subid." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "resetmy (wand)",
                "Reset your custom wand settings to server defaults." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(rp|report) queue", "Display statistics on current queues." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(rp|report) db", "Display basic database connection stats." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "(rp|report) sum (blocks|actions) (params)",
                "Display summary reports for a player" ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "about", "Show Prism credits." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "recorder cancel", "Stops recorder. Use with caution." ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "recorder start", "Starts recorder if it's stopped" ) );
        sender.sendMessage(MCMEPrism.messenger.playerHelp( "reload", "Reload config/language files." ) );

    }
}