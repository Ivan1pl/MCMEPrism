package me.botsko.mcmeprism.database.mysql;

import java.util.ArrayList;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;

public class BlockReportQueryBuilder extends SelectQueryBuilder {

    /**
     * 
     * @param plugin
     */
    public BlockReportQueryBuilder(MCMEPrism plugin) {
        super( plugin );
    }

    /**
     * 
     * @param parameters
     * @param shouldGroup
     * @return
     */
    @Override
    public String getQuery(QueryParameters parameters, boolean shouldGroup) {

        this.parameters = parameters;
        this.shouldGroup = shouldGroup;

        // Reset
        columns = new ArrayList<String>();
        conditions = new ArrayList<String>();

        String query = select();

        query += ";";

        if( plugin.getConfig().getBoolean( "prism.debug" ) ) {
            MCMEPrism.debug( query );
        }

        return query;

    }

    /**
	 * 
	 */
    @Override
    public String select() {
        String prefix = MCMEPrism.config.getString("prism.mysql.prefix");

        parameters.addActionType( "block-place" );

        // block-place query
        String sql = "" + "SELECT block_id, SUM(placed) AS placed, SUM(broken) AS broken " + "FROM (("
                + "SELECT block_id, COUNT(id) AS placed, 0 AS broken " + "FROM " + prefix + "data " + where() + " "
                + "GROUP BY block_id) ";

        conditions.clear();
        parameters.getActionTypes().clear();
        parameters.addActionType( "block-break" );

        sql += "UNION ( " + "SELECT block_id, 0 AS placed, count(id) AS broken " + "FROM " + prefix + "data " + where() + " "
                + "GROUP BY block_id)) " + "AS PR_A " + "GROUP BY block_id ORDER BY (SUM(placed) + SUM(broken)) DESC";

        return sql;

    }
}