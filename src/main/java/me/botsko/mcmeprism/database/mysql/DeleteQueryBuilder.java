package me.botsko.mcmeprism.database.mysql;

import me.botsko.mcmeprism.MCMEPrism;

public class DeleteQueryBuilder extends SelectQueryBuilder {

    /**
     * 
     * @param plugin
     */
    public DeleteQueryBuilder(MCMEPrism plugin) {
        super( plugin );
    }

    /**
	 * 
	 */
    @Override
    public String select() {
        return "DELETE FROM " + tableNameData + " USING " + tableNameData + 
        " LEFT JOIN " + tableNameDataExtra + " ex ON (" + tableNameData + ".id = ex.data_id) ";
    }

    /**
	 * 
	 */
    @Override
    protected String group() {
        return "";
    }

    /**
	 * 
	 */
    @Override
    protected String order() {
        return "";
    }

    /**
	 * 
	 */
    @Override
    protected String limit() {
        return "";
    }
}