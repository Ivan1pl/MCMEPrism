package me.botsko.mcmeprism.commands;

import us.dhmc.mcmeelixr.TypeUtils;
import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.MatchRule;
import me.botsko.mcmeprism.actionlibs.QueryParameters;
import me.botsko.mcmeprism.actionlibs.RecordingManager;
import me.botsko.mcmeprism.actionlibs.RecordingQueue;
import me.botsko.mcmeprism.appliers.PrismProcessType;
import me.botsko.mcmeprism.commandlibs.CallInfo;
import me.botsko.mcmeprism.commandlibs.PreprocessArgs;
import me.botsko.mcmeprism.commandlibs.SubHandler;
import me.botsko.mcmeprism.database.mysql.ActionReportQueryBuilder;
import me.botsko.mcmeprism.database.mysql.BlockReportQueryBuilder;
import me.botsko.mcmeprism.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitScheduler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;

public class ReportCommand implements SubHandler {

    /**
	 * 
	 */
    private final MCMEPrism plugin;
    private final List<String> secondaries, sumTertiaries;

    /**
     * 
     * @param plugin
     * @return
     */
    public ReportCommand(MCMEPrism plugin) {
        this.plugin = plugin;
        secondaries = new ArrayList<String>();
        secondaries.add( "queue" );
        secondaries.add( "db" );
        secondaries.add( "sum" );
        sumTertiaries = new ArrayList<String>();
        sumTertiaries.add( "blocks" );
        sumTertiaries.add( "actions" );
    }

    /**
     * Handle the command
     */
    @Override
    public void handle(CallInfo call) {

        if( call.getArgs().length < 2 ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "Please specify a report. Use /prism ? for help." ) );
            return;
        }

        // /prism report queue
        if( call.getArg( 1 ).equals( "queue" ) ) {
            queueReport( call.getSender() );
        }

        // /prism report db
        if( call.getArg( 1 ).equals( "db" ) ) {
            databaseReport( call.getSender() );
        }

        // /prism report queue
        if( call.getArg( 1 ).equals( "sum" ) ) {

            if( call.getArgs().length < 3 ) {
                call.getSender().sendMessage(MCMEPrism.messenger.playerError( "Please specify a 'sum' report. Use /prism ? for help." ) );
                return;
            }

            if( call.getArgs().length < 4 ) {
                call.getSender().sendMessage(MCMEPrism.messenger.playerError( "Please provide a player name. Use /prism ? for help." ) );
                return;
            }

            if( call.getArg( 2 ).equals( "blocks" ) ) {
                blockSumReports( call );
            }

            if( call.getArg( 2 ).equals( "actions" ) ) {
                actionTypeCountReport( call );
            }
        }
    }

    @Override
    public List<String> handleComplete(CallInfo call) {
        if( call.getArgs().length == 2 ) { return MiscUtils.getStartingWith( call.getArg( 1 ), secondaries ); }
        if( call.getArg( 1 ).equals( "sum" ) ) {
            if( call.getArgs().length == 3 ) { return MiscUtils.getStartingWith( call.getArg( 2 ), sumTertiaries ); }
            return PreprocessArgs.complete( call.getSender(), call.getArgs() );
        }
        return null;
    }

    /**
     * 
     * @param sender
     */
    protected void queueReport(CommandSender sender) {

        sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Current Stats" ) );

        sender.sendMessage(MCMEPrism.messenger.playerMsg( "Actions in queue: " + ChatColor.WHITE
                + RecordingQueue.getQueueSize() ) );

        final ConcurrentSkipListMap<Long, Integer> runs = plugin.queueStats.getRecentRunCounts();
        if( runs.size() > 0 ) {
            sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Recent queue save stats:" ) );
            for ( final Entry<Long, Integer> entry : runs.entrySet() ) {
                final String time = new SimpleDateFormat( "HH:mm:ss" ).format( entry.getKey() );
                sender.sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.GRAY + time + " " + ChatColor.WHITE
                        + entry.getValue() ) );
            }
        }
    }

    /**
     * 
     * @param sender
     */
    protected void databaseReport(CommandSender sender) {

        sender.sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Database Connection State" ) );

        sender.sendMessage(MCMEPrism.messenger.playerMsg( "Active Failure Count: " + ChatColor.WHITE
                + RecordingManager.failedDbConnectionCount ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg( "Actions in queue: " + ChatColor.WHITE
                + RecordingQueue.getQueueSize() ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg("Pool active: " + ChatColor.WHITE + MCMEPrism.getPool().getActive() ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg("Pool idle: " + ChatColor.WHITE + MCMEPrism.getPool().getIdle() ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg("Pool active count: " + ChatColor.WHITE
                + MCMEPrism.getPool().getNumActive() ) );
        sender.sendMessage(MCMEPrism.messenger.playerMsg("Pool idle count: " + ChatColor.WHITE
                + MCMEPrism.getPool().getNumIdle() ) );

        boolean recorderActive = false;
        if( plugin.recordingTask != null ) {
            final int taskId = plugin.recordingTask.getTaskId();
            final BukkitScheduler scheduler = Bukkit.getScheduler();
            if( scheduler.isCurrentlyRunning( taskId ) || scheduler.isQueued( taskId ) ) {
                recorderActive = true;
            }
        }

        if( recorderActive ) {
            sender.sendMessage(MCMEPrism.messenger.playerSuccess( "Recorder is currently queued or running!" ) );
        } else {
            sender.sendMessage(MCMEPrism.messenger
                    .playerError( "Recorder stopped running! DB conn problems? Try /pr recorder start" ) );
        }

        sender.sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Attempting to check connection readiness..." ) );

        Connection conn = null;
        try {

            conn = MCMEPrism.dbc();
            if( conn == null ) {
                sender.sendMessage(MCMEPrism.messenger.playerError( "Pool returned NULL instead of a valid connection." ) );
            } else if( conn.isClosed() ) {
                sender.sendMessage(MCMEPrism.messenger.playerError( "Pool returned an already closed connection." ) );
            } else if( conn.isValid( 5 ) ) {
                sender.sendMessage(MCMEPrism.messenger.playerSuccess( "Pool returned valid connection!" ) );
            }
        } catch ( final SQLException e ) {
            sender.sendMessage(MCMEPrism.messenger.playerError( "Error: " + e.getMessage() ) );
            e.printStackTrace();
        } finally {
            if( conn != null )
                try {
                    conn.close();
                } catch ( final SQLException ignored ) {}
        }
    }

    /**
     * 
     * @param sender
     */
    protected void blockSumReports(final CallInfo call) {

        // Process and validate all of the arguments
        final QueryParameters parameters = PreprocessArgs.process( plugin, call.getSender(), call.getArgs(),
                PrismProcessType.LOOKUP, 3, !plugin.getConfig().getBoolean( "prism.queries.never-use-defaults" ) );
        if( parameters == null ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "You must specify parameters, at least one player." ) );
            return;
        }

        // No actions
        if( !parameters.getActionTypes().isEmpty() ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "You may not specify any action types for this report." ) );
            return;
        }

        // Verify single player name for now
        final HashMap<String, MatchRule> players = parameters.getPlayerNames();
        if( players.size() != 1 ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "You must provide only a single player name." ) );
            return;
        }
        // Get single playername
        String tempName = "";
        for ( final String player : players.keySet() ) {
            tempName = player;
            break;
        }
        final String playerName = tempName;

        final BlockReportQueryBuilder reportQuery = new BlockReportQueryBuilder( plugin );
        final String sql = reportQuery.getQuery( parameters, false );

        final int colTextLen = 20;
        final int colIntLen = 12;

        /**
         * Run the lookup itself in an async task so the lookup query isn't done
         * on the main thread
         */
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Crafting block change report for "
                                + ChatColor.DARK_AQUA + playerName + "..." ) );

                Connection conn = null;
                PreparedStatement s = null;
                ResultSet rs = null;
                try {

                    conn = MCMEPrism.dbc();
                    s = conn.prepareStatement( sql );
                    s.executeQuery();
                    rs = s.getResultSet();

                    call.getSender().sendMessage(MCMEPrism.messenger.playerHeaderMsg( "Total block changes for " + ChatColor.DARK_AQUA
                                    + playerName ) );
                    call.getSender().sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.GRAY + TypeUtils.padStringRight( "Block", colTextLen )
                                    + TypeUtils.padStringRight( "Placed", colIntLen )
                                    + TypeUtils.padStringRight( "Broken", colIntLen ) ) );

                    while ( rs.next() ) {

                        final String alias = MCMEPrism.getItems().getAlias( rs.getInt( 1 ), 0 );

                        final int placed = rs.getInt( 2 );
                        final int broken = rs.getInt( 3 );

                        final String colAlias = TypeUtils.padStringRight( alias, colTextLen );
                        final String colPlaced = TypeUtils.padStringRight( "" + placed, colIntLen );
                        final String colBroken = TypeUtils.padStringRight( "" + broken, colIntLen );

                        call.getSender().sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.DARK_AQUA + colAlias + ChatColor.GREEN + colPlaced
                                        + " " + ChatColor.RED + colBroken ) );

                    }
                } catch ( final SQLException e ) {
                    e.printStackTrace();
                } finally {
                    if( rs != null )
                        try {
                            rs.close();
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
            }
        } );
    }

    /**
     * 
     * @param sender
     */
    protected void actionTypeCountReport(final CallInfo call) {

        // Process and validate all of the arguments
        final QueryParameters parameters = PreprocessArgs.process( plugin, call.getSender(), call.getArgs(),
                PrismProcessType.LOOKUP, 3, !plugin.getConfig().getBoolean( "prism.queries.never-use-defaults" ) );
        if( parameters == null ) { return; }

        // No actions
        if( !parameters.getActionTypes().isEmpty() ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "You may not specify any action types for this report." ) );
            return;
        }

        // Verify single player name for now
        final HashMap<String, MatchRule> players = parameters.getPlayerNames();
        if( players.size() != 1 ) {
            call.getSender().sendMessage(MCMEPrism.messenger.playerError( "You must provide only a single player name." ) );
            return;
        }
        // Get single playername
        String tempName = "";
        for ( final String player : players.keySet() ) {
            tempName = player;
            break;
        }
        final String playerName = tempName;

        final ActionReportQueryBuilder reportQuery = new ActionReportQueryBuilder( plugin );
        final String sql = reportQuery.getQuery( parameters, false );

        final int colTextLen = 16;
        final int colIntLen = 12;

        /**
         * Run the lookup itself in an async task so the lookup query isn't done
         * on the main thread
         */
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
            @Override
            public void run() {

                call.getSender().sendMessage(MCMEPrism.messenger.playerSubduedHeaderMsg( "Crafting action type report for "
                                + ChatColor.DARK_AQUA + playerName + "..." ) );

                Connection conn = null;
                PreparedStatement s = null;
                ResultSet rs = null;
                try {

                    conn = MCMEPrism.dbc();
                    s = conn.prepareStatement( sql );
                    s.executeQuery();
                    rs = s.getResultSet();

                    call.getSender().sendMessage(MCMEPrism.messenger.playerMsg( ChatColor.GRAY + TypeUtils.padStringRight( "Action", colTextLen )
                                    + TypeUtils.padStringRight( "Count", colIntLen ) ) );

                    while ( rs.next() ) {

                        final String action = rs.getString( 2 );
                        final int count = rs.getInt( 1 );

                        final String colAlias = TypeUtils.padStringRight( action, colTextLen );
                        final String colPlaced = TypeUtils.padStringRight( "" + count, colIntLen );

                        call.getSender().sendMessage(MCMEPrism.messenger
                                        .playerMsg( ChatColor.DARK_AQUA + colAlias + ChatColor.GREEN + colPlaced ) );

                    }
                } catch ( final SQLException e ) {
                    e.printStackTrace();
                } finally {
                    if( rs != null )
                        try {
                            rs.close();
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
            }
        } );
    }
}