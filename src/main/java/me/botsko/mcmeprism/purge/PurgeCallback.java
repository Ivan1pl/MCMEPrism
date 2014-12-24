package me.botsko.mcmeprism.purge;

import me.botsko.mcmeprism.actionlibs.QueryParameters;

public interface PurgeCallback {
    public void cycle(QueryParameters param, int cycle_rows_affected, int total_records_affected, boolean cycle_complete);
}