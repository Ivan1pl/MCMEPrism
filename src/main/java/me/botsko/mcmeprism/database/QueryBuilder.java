package me.botsko.mcmeprism.database;

import java.util.ArrayList;
import java.util.List;

import me.botsko.mcmeprism.MCMEPrism;
import me.botsko.mcmeprism.actionlibs.QueryParameters;

abstract public class QueryBuilder {

    /**
	 * 
	 */
    protected final MCMEPrism plugin;
    protected List<String> columns = new ArrayList<String>();
    protected List<String> conditions = new ArrayList<String>();

    protected final String tableNameData;
    protected final String tableNameDataExtra;

    protected QueryParameters parameters;
    protected boolean shouldGroup;

    /**
     * 
     * @param plugin
     */
    public QueryBuilder(MCMEPrism plugin) {
        this.plugin = plugin;
        String prefix = plugin.getConfig().getString("prism.mysql.prefix");
        tableNameData = prefix + "data";
        tableNameDataExtra = prefix + "data_extra";
    }

    /**
     * 
     * @param parameters
     * @param shouldGroup
     * @return
     */
    public String getQuery(QueryParameters parameters, boolean shouldGroup) {

        this.parameters = parameters;
        this.shouldGroup = shouldGroup;

        // Reset
        columns = new ArrayList<String>();
        conditions = new ArrayList<String>();

        String query = select() + where() + group() + order() + limit();

        query += ";";

        if( plugin.getConfig().getBoolean( "prism.debug" ) ) {
            MCMEPrism.debug( query );
        }

        return query;

    }

    /**
	 * 
	 */
    protected String select() {
        return "";
    }

    /**
	 * 
	 */
    protected String where() {
        return "";
    }

    /**
	 * 
	 */
    protected String group() {
        return "";
    }

    /**
	 * 
	 */
    protected String order() {
        return "";
    }

    /**
	 * 
	 */
    protected String limit() {
        return "";
    }

    /**
     * 
     * @param condition
     */
    protected void addCondition(String condition) {
        if( !condition.isEmpty() ) {
            conditions.add( condition );
        }
    }
}