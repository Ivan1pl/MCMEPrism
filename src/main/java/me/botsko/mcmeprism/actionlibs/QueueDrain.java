package me.botsko.mcmeprism.actionlibs;

import me.botsko.mcmeprism.MCMEPrism;

public class QueueDrain {

    /**
	 * 
	 */
    private final MCMEPrism plugin;

    /**
     * 
     * @param plugin
     */
    public QueueDrain(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
	 * 
	 */
    public void forceDrainQueue() {

        MCMEPrism.log( "Forcing recorder queue to run a new batch before shutdown..." );

        final RecordingTask recorderTask = new RecordingTask( plugin );

        // Force queue to empty
        while ( !RecordingQueue.getQueue().isEmpty() ) {

            MCMEPrism.log( "Starting drain batch..." );
            MCMEPrism.log( "Current queue size: " + RecordingQueue.getQueue().size() );

            // run insert
            try {
                recorderTask.insertActionsIntoDatabase();
            } catch ( final Exception e ) {
                e.printStackTrace();
                MCMEPrism.log( "Stopping queue drain due to caught exception. Queue items lost: "
                        + RecordingQueue.getQueue().size() );
                break;
            }

            if( RecordingManager.failedDbConnectionCount > 0 ) {
                MCMEPrism.log( "Stopping queue drain due to detected database error. Queue items lost: "
                        + RecordingQueue.getQueue().size() );
            }
        }
    }
}