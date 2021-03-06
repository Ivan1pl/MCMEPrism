package me.botsko.mcmeprism.listeners.self;

import java.util.ArrayList;

import me.botsko.mcmeprism.actionlibs.ActionFactory;
import me.botsko.mcmeprism.actionlibs.RecordingQueue;
import me.botsko.mcmeprism.actionlibs.RecordingTask;
import me.botsko.mcmeprism.actions.Handler;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.events.BlockStateChange;
import me.botsko.mcmeprism.events.PrismBlocksDrainEvent;
import me.botsko.mcmeprism.events.PrismBlocksExtinguishEvent;

import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PrismMiscEvents implements Listener {

    /**
     * 
     * @param event
     */
    @EventHandler
    public void onPrismBlocksDrainEvent(final PrismBlocksDrainEvent event) {

        // Get all block changes for this event
        final ArrayList<BlockStateChange> blockStateChanges = event.getBlockStateChanges();
        if( !blockStateChanges.isEmpty() ) {

            // Create an entry for the rollback as a whole
            final Handler primaryAction = ActionFactory.createPrismProcess("prism-process", PrismProcessType.DRAIN,
                    event.onBehalfOf(), "" + event.getRadius());
            final int id = RecordingTask.insertActionIntoDatabase( primaryAction );
            if( id == 0 ) { return; }
            for ( final BlockStateChange stateChange : blockStateChanges ) {

                final BlockState orig = stateChange.getOriginalBlock();
                final BlockState newBlock = stateChange.getNewBlock();

                // Build the action
                RecordingQueue.addToQueue( ActionFactory.createPrismRollback("prism-drain", orig, newBlock, event.onBehalfOf()
                        .getName(), id) );

            }
            // ActionQueue.save();
        }
    }

    /**
     * 
     * @param event
     */
    @EventHandler
    public void onPrismBlocksExtinguishEvent(final PrismBlocksExtinguishEvent event) {

        // Get all block changes for this event
        final ArrayList<BlockStateChange> blockStateChanges = event.getBlockStateChanges();
        if( !blockStateChanges.isEmpty() ) {

            // Create an entry for the rollback as a whole
            final Handler primaryAction = ActionFactory.createPrismProcess("prism-process", PrismProcessType.EXTINGUISH,
                    event.onBehalfOf(), "" + event.getRadius());
            final int id = RecordingTask.insertActionIntoDatabase( primaryAction );
            if( id == 0 ) { return; }
            for ( final BlockStateChange stateChange : blockStateChanges ) {

                final BlockState orig = stateChange.getOriginalBlock();
                final BlockState newBlock = stateChange.getNewBlock();

                // Build the action
                RecordingQueue.addToQueue( ActionFactory.createPrismRollback("prism-extinguish", orig, newBlock, event.onBehalfOf()
                        .getName(), id) );

            }
            // ActionQueue.save();
        }
    }
}