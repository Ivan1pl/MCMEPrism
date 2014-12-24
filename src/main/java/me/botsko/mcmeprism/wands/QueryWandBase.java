package me.botsko.mcmeprism.wands;

import java.util.Arrays;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.commandlibs.PreprocessArgs;
import org.bukkit.entity.Player;

/**
 * A base class for Wands that use
 * {@link me.botsko.mcmeprism.actionlibs.QueryParameters} and show results. This
 * will allow users to specify parameters when creating the wand and have them
 * saved in the Wand for every time they use it until it is disabled.
 */
public abstract class QueryWandBase extends WandBase {

    /**
     * The parameters that are specified. Whenever we do a search we can clone
     * this and then add the extra stuff. (Location, etc)
     */
    protected QueryParameters parameters;

    /**
     * Keep an instance of {@link me.botsko.mcmeprism.MCMEPrism Prism} to use.
     */
    protected final MCMEPrism plugin;

    /**
     * When we initialize the class, make the {@link #parameters} equal to a
     * fresh QueryParameters.
     */
    public QueryWandBase(MCMEPrism plugin) {
        parameters = new QueryParameters();
        this.plugin = plugin;
    }

    /**
     * Set the field {@link #parameters} with the parameters here. This will be
     * using the stuff in <code>/prism params</code>
     * 
     * @param sender
     *            The sender of the command.
     * @param args
     *            The arguments from <code>/prism params</code>.
     * @param argStart
     *            What argument to start on.
     */
    public boolean setParameters(Player sender, String[] args, int argStart) {
        final PrismProcessType processType = this instanceof RollbackWand ? PrismProcessType.ROLLBACK
                : this instanceof RestoreWand ? PrismProcessType.RESTORE
                        : this instanceof InspectorWand ? PrismProcessType.LOOKUP : PrismProcessType.LOOKUP;

        final QueryParameters params = PreprocessArgs.process( plugin, sender, args, processType, argStart, false, true );
        if( params == null ) {
            return false;
        } else {
            params.resetMinMaxVectors();
            this.parameters = params;
            return true;
        }
    }

    /**
     * Get the {@link #parameters} set from {@link #setParameters}.
     * 
     * @return The wand's {@link me.botsko.mcmeprism.actionlibs.QueryParameters}.
     */
    public QueryParameters getParameters() {
        return parameters;
    }
}
