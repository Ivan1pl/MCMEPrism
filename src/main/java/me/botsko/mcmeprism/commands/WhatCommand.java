package me.botsko.mcmeprism.commands;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.Executor;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import me.botsko.mcmeprism.utils.ItemUtils;

import java.util.List;

public class WhatCommand extends Executor {

    /**
     * 
     * @param prism
     */
    public WhatCommand(MCMEPrism prism) {
        super( prism, "command", "prism" );
        setupCommands();
    }

    /**
	 * 
	 */
    private void setupCommands() {

        /**
         * /prism about
         */
        addSub( new String[] { "about", "default" }, "prism.help" ).allowConsole().setHandler(new SubHandler() {
            @Override
            public void handle(CallInfo call) {
                final ItemStack item = call.getPlayer().getItemInHand();

                call.getPlayer().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Item Profile:" ) );

                String line1 = ChatColor.WHITE + "Name: " + ChatColor.DARK_AQUA
                        + item.getType().toString().toLowerCase();
                line1 += ChatColor.WHITE + " Prism Alias: " + ChatColor.DARK_AQUA
                        + MCMEPrism.getItems().getAlias( item.getTypeId(), item.getDurability() );
                line1 += ChatColor.WHITE + " ID: " + ChatColor.DARK_AQUA + item.getTypeId() + ":"
                        + item.getDurability();

                call.getPlayer().sendMessage(MCMEPrism.messenger.playerMsg( line1 ) );
                call.getPlayer().sendMessage(MCMEPrism.messenger.playerMsg(ChatColor.WHITE + "Full Display Name: " + ChatColor.DARK_AQUA
                                + us.dhmc.mcmeelixr.ItemUtils.getItemFullNiceName(item ) ) );

            }

            @Override
            public List<String> handleComplete(CallInfo call) {
                return null;
            }
        } );
    }
}