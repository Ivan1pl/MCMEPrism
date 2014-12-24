package me.botsko.mcmeprism.database.mysql;

import java.util.ArrayList;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;

public class ActionReportQueryBuilder extends SelectQueryBuilder {

    /**
     * 
     * @param plugin
     */
    public ActionReportQueryBuilder(MCMEPrism plugin) {
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

        final String sql = "SELECT COUNT(*), a.action " + "FROM " + prefix + "data "
                + "INNER JOIN " + prefix + "actions a ON a.action_id = " + prefix + "data.action_id " + where() + " "
                + "GROUP BY a.action_id " + "ORDER BY COUNT(*) DESC";

        return sql;

    }
}