package me.botsko.mcmeprism.actionlibs;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitScheduler;

import me.botsko.mcmeprism.MCMEPrism;

public class InternalAffairs implements Runnable {

    private final MCMEPrism plugin;

    /**
     * 
     * @param prism
     */
    public InternalAffairs(MCMEPrism plugin) {
        MCMEPrism.debug( "[InternalAffairs] Keeping watch over the watchers." );
        this.plugin = plugin;
    }

    /**
	 * 
	 */
    @Override
    public void run() {

        if( plugin.recordingTask != null ) {

            final int taskId = plugin.recordingTask.getTaskId();

            final BukkitScheduler scheduler = Bukkit.getScheduler();

            // is recording task running?
            if( scheduler.isCurrentlyRunning( taskId ) || scheduler.isQueued( taskId ) ) {
                MCMEPrism.debug( "[InternalAffairs] Recorder is currently active. All is good." );
                return;
            }
        }

        MCMEPrism.log( "[InternalAffairs] Recorder is NOT active... checking database" );

        // is db connection valid?
        Connection conn = null;
        try {

            conn = MCMEPrism.dbc();
            if( conn == null ) {
                MCMEPrism.log( "[InternalAffairs] Pool returned NULL instead of a valid connection." );
            } else if( conn.isClosed() ) {
                MCMEPrism.log( "[InternalAffairs] Pool returned an already closed connection." );
            } else if( conn.isValid( 5 ) ) {

                MCMEPrism.log( "[InternalAffairs] Pool returned valid connection!" );

                MCMEPrism.log( "[InternalAffairs] Restarting scheduled recorder tasks" );
                plugin.actionRecorderTask();

            }
        } catch ( final SQLException e ) {
            MCMEPrism.debug( "[InternalAffairs] Error: " + e.getMessage() );
            e.printStackTrace();
        } finally {
            if( conn != null )
                try {
                    conn.close();
                } catch ( final SQLException e ) {}
        }
    }
}