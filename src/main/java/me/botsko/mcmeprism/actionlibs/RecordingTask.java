package me.botsko.mcmeprism.actionlibs;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.PrismConfig;
import me.botsko.mcmeprism.actions.Handler;
import me.botsko.mcmeprism.players.PlayerIdentification;
import me.botsko.mcmeprism.players.PrismPlayer;

public class RecordingTask implements Runnable {

    /**
	 *
	 */
    private final MCMEPrism plugin;

    /**
     *
     * @param plugin
     */
    public RecordingTask(MCMEPrism plugin) {
        this.plugin = plugin;
    }

    /**
	 *
	 */
    public void save() {
        if( !RecordingQueue.getQueue().isEmpty() ) {
            insertActionsIntoDatabase();
        }
    }

    /**
     *
     * @param a
     */
    public static int insertActionIntoDatabase(Handler a) {
        String prefix = MCMEPrism.config.getString("prism.mysql.prefix");
        int id = 0;
        Connection conn = null;
        PreparedStatement s = null;
        ResultSet generatedKeys = null;
        try {

            // prepare to save to the db
            a.save();

            conn = MCMEPrism.dbc();
            if( conn == null ) {
                MCMEPrism.log( "Prism database error. Connection should be there but it's not. This action wasn't logged." );
                return 0;
            }

            int world_id = 0;
            if( MCMEPrism.prismWorlds.containsKey( a.getWorldName() ) ) {
                world_id = MCMEPrism.prismWorlds.get( a.getWorldName() );
            }

            int action_id = 0;
            if( MCMEPrism.prismActions.containsKey( a.getType().getName() ) ) {
                action_id = MCMEPrism.prismActions.get( a.getType().getName() );
            }

            int player_id = 0;
            PrismPlayer prismPlayer = PlayerIdentification.cachePrismPlayer( a.getPlayerName() );
            if( prismPlayer != null ){
                player_id = prismPlayer.getId();
            }

            if( world_id == 0 || action_id == 0 || player_id == 0 ) {
                // @todo do something, error here
            }

            s = conn.prepareStatement(
                    "INSERT INTO " + prefix + "data (epoch,action_id,player_id,world_id,block_id,block_subid,old_block_id,old_block_subid,x,y,z) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                    Statement.RETURN_GENERATED_KEYS );
            s.setLong( 1, System.currentTimeMillis() / 1000L );
            s.setInt( 2, world_id );
            s.setInt( 3, player_id );
            s.setInt( 4, world_id );
            s.setInt( 5, a.getBlockId() );
            s.setInt( 6, a.getBlockSubId() );
            s.setInt( 7, a.getOldBlockId() );
            s.setInt( 8, a.getOldBlockSubId() );
            s.setInt( 9, (int) a.getX() );
            s.setInt( 10, (int) a.getY() );
            s.setInt( 11, (int) a.getZ() );
            s.executeUpdate();

            generatedKeys = s.getGeneratedKeys();
            if( generatedKeys.next() ) {
                id = generatedKeys.getInt( 1 );
            }

            // Add insert query for extra data if needed
            if( a.getData() != null && !a.getData().isEmpty() ) {
                s = conn.prepareStatement( "INSERT INTO " + prefix + "data_extra (data_id,data) VALUES (?,?)" );
                s.setInt( 1, id );
                s.setString( 2, a.getData() );
                s.executeUpdate();
            }

        } catch ( final SQLException e ) {
            // plugin.handleDatabaseException( e );
        } finally {
            if( generatedKeys != null )
                try {
                    generatedKeys.close();
                } catch ( final SQLException ignored ) {}
            if( s != null )
                try {
                    s.close();
                } catch ( final SQLException ignored ) {}
            if( conn != null )
                try {
                    conn.close();
                } catch ( final SQLException ignored ) {}
        }
        return id;
    }

    /**
     *
     * @throws SQLException
     */
    public void insertActionsIntoDatabase() {
        String prefix = plugin.getConfig().getString("prism.mysql.prefix");

        PreparedStatement s = null;
        Connection conn = null;

        int actionsRecorded = 0;
        try {

            int perBatch = plugin.getConfig().getInt( "prism.database.actions-per-insert-batch" );
            if( perBatch < 1 )
                perBatch = 1000;

            if( !RecordingQueue.getQueue().isEmpty() ) {

                MCMEPrism.debug( "Beginning batch insert from queue. " + System.currentTimeMillis() );

                final ArrayList<Handler> extraDataQueue = new ArrayList<Handler>();
                conn = MCMEPrism.dbc();

                // Handle dead connections
                if( conn == null || conn.isClosed() ) {
                    if( RecordingManager.failedDbConnectionCount == 0 ) {
                        MCMEPrism.log( "Prism database error. Connection should be there but it's not. Leaving actions to log in queue." );
                    }
                    RecordingManager.failedDbConnectionCount++;
                    if( RecordingManager.failedDbConnectionCount > plugin.getConfig().getInt(
                            "prism.database.max-failures-before-wait" ) ) {
                        MCMEPrism.log( "Too many problems connecting. Giving up for a bit." );
                        scheduleNextRecording();
                    }
                    MCMEPrism.debug( "Database connection still missing, incrementing count." );
                    return;
                } else {
                    RecordingManager.failedDbConnectionCount = 0;
                }

                // Connection valid, proceed
                conn.setAutoCommit( false );
                s = conn.prepareStatement(
                        "INSERT INTO " + prefix + "data (epoch,action_id,player_id,world_id,block_id,block_subid,old_block_id,old_block_subid,x,y,z) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                        Statement.RETURN_GENERATED_KEYS );
                int i = 0;
                while ( !RecordingQueue.getQueue().isEmpty() ) {

                    if( conn.isClosed() ) {
                        MCMEPrism.log( "Prism database error. We have to bail in the middle of building primary bulk insert query." );
                        break;
                    }

                    final Handler a = RecordingQueue.getQueue().poll();

                    // poll() returns null if queue is empty
                    if( a == null )
                        break;

                    int world_id = 0;
                    if( MCMEPrism.prismWorlds.containsKey( a.getWorldName() ) ) {
                        world_id = MCMEPrism.prismWorlds.get( a.getWorldName() );
                    }

                    int action_id = 0;
                    if( MCMEPrism.prismActions.containsKey( a.getType().getName() ) ) {
                        action_id = MCMEPrism.prismActions.get( a.getType().getName() );
                    }

                    int player_id = 0;
                    PrismPlayer prismPlayer = PlayerIdentification.cachePrismPlayer( a.getPlayerName() );
                    if( prismPlayer != null ){
                        player_id = prismPlayer.getId();
                    }

                    if( world_id == 0 || action_id == 0 || player_id == 0 ) {
                        // @todo do something, error here
                        MCMEPrism.log( "Cache data was empty. Please report to developer: world_id:" + world_id + "/"
                                + a.getWorldName() + " action_id:" + action_id + "/" + a.getType().getName()
                                + " player_id:" + player_id + "/" + a.getPlayerName() );
                        MCMEPrism.log( "HOWEVER, this likely means you have a broken prism database installation." );
                        continue;
                    }

                    if( a.isCanceled() )
                        continue;

                    actionsRecorded++;

                    s.setLong( 1, System.currentTimeMillis() / 1000L );
                    s.setInt( 2, action_id );
                    s.setInt( 3, player_id );
                    s.setInt( 4, world_id );
                    s.setInt( 5, a.getBlockId() );
                    s.setInt( 6, a.getBlockSubId() );
                    s.setInt( 7, a.getOldBlockId() );
                    s.setInt( 8, a.getOldBlockSubId() );
                    s.setInt( 9, (int) a.getX() );
                    s.setInt( 10, (int) a.getY() );
                    s.setInt( 11, (int) a.getZ() );
                    s.addBatch();

                    extraDataQueue.add( a );

                    // Break out of the loop and just commit what we have
                    if( i >= perBatch ) {
                        MCMEPrism.debug( "Recorder: Batch max exceeded, running insert. Queue remaining: "
                                + RecordingQueue.getQueue().size() );
                        break;
                    }
                    i++;
                }

                s.executeBatch();

                if( conn.isClosed() ) {
                    MCMEPrism.log( "Prism database error. We have to bail in the middle of building primary bulk insert query." );
                } else {
                    conn.commit();
                    MCMEPrism.debug( "Batch insert was commit: " + System.currentTimeMillis() );
                }

                // Save the current count to the queue for short historical data
                plugin.queueStats.addRunCount( actionsRecorded );

                // Insert extra data
                insertExtraData( extraDataQueue, s.getGeneratedKeys() );

            }
        } catch ( final SQLException e ) {
            e.printStackTrace();
            plugin.handleDatabaseException( e );
        } finally {
            if( s != null )
                try {
                    s.close();
                } catch ( final SQLException ignored ) {}
            if( conn != null )
                try {
                    conn.close();
                } catch ( final SQLException ignored ) {}
        }
    }

    /**
     *
     * @param keys
     * @throws SQLException
     */
    protected void insertExtraData(ArrayList<Handler> extraDataQueue, ResultSet keys) throws SQLException {
        String prefix = plugin.getConfig().getString("prism.mysql.prefix");

        if( extraDataQueue.isEmpty() )
            return;

        PreparedStatement s = null;
        final Connection conn = MCMEPrism.dbc();

        if( conn == null || conn.isClosed() ) {
            MCMEPrism.log( "Prism database error. Skipping extra data queue insertion." );
            return;
        }

        try {
            conn.setAutoCommit( false );
            s = conn.prepareStatement( "INSERT INTO " + prefix + "data_extra (data_id,data) VALUES (?,?)" );
            int i = 0;
            while ( keys.next() ) {

                if( conn.isClosed() ) {
                    MCMEPrism.log( "Prism database error. We have to bail in the middle of building bulk insert extra data query." );
                    break;
                }

                // @todo should not happen
                if( i >= extraDataQueue.size() ) {
                    MCMEPrism.log( "Skipping extra data for " + prefix + "data.id " + keys.getInt( 1 )
                            + " because the queue doesn't have data for it." );
                    continue;
                }

                final Handler a = extraDataQueue.get( i );

                if( a.getData() != null && !a.getData().isEmpty() ) {
                    s.setInt( 1, keys.getInt( 1 ) );
                    s.setString( 2, a.getData() );
                    s.addBatch();
                }

                i++;

            }
            s.executeBatch();

            if( conn.isClosed() ) {
                MCMEPrism.log( "Prism database error. We have to bail in the middle of building extra data bulk insert query." );
            } else {
                conn.commit();
            }

        } catch ( final SQLException e ) {
            e.printStackTrace();
            plugin.handleDatabaseException( e );
        } finally {
            if( s != null )
                try {
                    s.close();
                } catch ( final SQLException ignored ) {}
            try {
                conn.close();
            } catch ( final SQLException ignored ) {}
        }
    }

    /**
	 *
	 */
    @Override
    public void run() {
        if( RecordingManager.failedDbConnectionCount > 5 ) {
            plugin.rebuildPool(); // force rebuild pool after several failures
        }
        save();
        scheduleNextRecording();
    }

    /**
     *
     * @return
     */
    protected int getTickDelayForNextBatch() {

        // If we have too many rejected connections, increase the schedule
        if( RecordingManager.failedDbConnectionCount > plugin.getConfig().getInt(
                "prism.database.max-failures-before-wait" ) ) { return RecordingManager.failedDbConnectionCount * 20; }

        int recorder_tick_delay = plugin.getConfig().getInt( "prism.queue-empty-tick-delay" );
        if( recorder_tick_delay < 1 ) {
            recorder_tick_delay = 3;
        }
        return recorder_tick_delay;
    }

    /**
	 *
	 */
    protected void scheduleNextRecording() {
        if( !plugin.isEnabled() ) {
            MCMEPrism.log( "Can't schedule new recording tasks as plugin is now disabled. If you're shutting down the server, ignore me." );
            return;
        }
        plugin.recordingTask = plugin.getServer().getScheduler()
                .runTaskLaterAsynchronously( plugin, new RecordingTask( plugin ), getTickDelayForNextBatch() );
    }
}
