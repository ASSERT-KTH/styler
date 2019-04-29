package com.developmentontheedge.be5.modules.core.queries.system;

import com.developmentontheedge.be5.base.services.Be5Caches;
import com.developmentontheedge.be5.query.model.TableModel;
import com.developmentontheedge.be5.server.queries.support.TableBuilderSupport;
import com.github.benmanes.caffeine.cache.Cache;

import javax.inject.Inject;
import java.util.Map;


public class CacheTable extends TableBuilderSupport
{
    @Inject
    private Be5Caches be5Caches;

    @Override
    public TableModel getTableModel()
    {
        addColumns("Name",
                "Size",
                "Hit rate",
                "Eviction count",
                "Average load penalty",
                "Load / hit count",
                "Miss count",
                "Failure count");

        for (Map.Entry<String, Cache> entry : be5Caches.getCaches().entrySet())
        {
            addRow(entry.getKey(), cells(
                    entry.getKey(),
                    be5Caches.getCacheSize(entry.getKey()),
                    String.format("%.4f", entry.getValue().stats().hitRate()),
                    entry.getValue().stats().evictionCount(),
                    String.format("%.4f", entry.getValue().stats().averageLoadPenalty() / 1000_000_000.0),
                    entry.getValue().stats().loadCount() + " / " + entry.getValue().stats().hitCount(),
                    entry.getValue().stats().missCount(),
                    entry.getValue().stats().loadFailureCount()
            ));
        }
        return table(columns, rows);
    }
}
