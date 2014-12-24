package me.botsko.mcmeprism.parameters;

import me.botsko.mcmeprism.actionlibs.QueryParameters;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class KeywordParameter extends SimplePrismParameterHandler {

    /**
	 * 
	 */
    public KeywordParameter() {
        super( "Keyword", Pattern.compile( "[^\\s]+" ), "k" );
    }

    /**
	 * 
	 */
    @Override
    public void process(QueryParameters query, String alias, String input, CommandSender sender) {
        query.setKeyword( input );
    }
}