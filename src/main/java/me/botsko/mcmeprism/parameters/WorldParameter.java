package me.botsko.mcmeprism.parameters;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class WorldParameter extends SimplePrismParameterHandler {

    /**
	 * 
	 */
    public WorldParameter() {
        super( "World", Pattern.compile( "[^\\s]+" ), "w" );
    }

    /**
	 * 
	 */
    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        String worldName = input;
        if( worldName.equalsIgnoreCase( "current" ) ) {
            if( sender instanceof Player ) {
                worldName = ( (Player) sender ).getWorld().getName();
            } else {
                sender.sendMessage(MCMEPrism.messenger
                        .playerError( "Can't use the current world since you're not a player. Using default world." ) );
                worldName = Bukkit.getServer().getWorlds().get( 0 ).getName();
            }
        }
        query.setWorld( worldName );
    }

    /**
	 * 
	 */
    @Override
    public void defaultTo(QueryParameters query, CommandSender sender) {
        if( query.getProcessType().equals( PrismProcessType.DELETE ) )
            return;
        if( sender instanceof Player && !query.allowsNoRadius() ) {
            query.setWorld( ( (Player) sender ).getWorld().getName() );
        }
    }
}