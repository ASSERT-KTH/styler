/*
 * Copyright 2004-2018 H2 Group. Multiple-Licensed under the MPL 2.0,
 * and the EPL 1.0 (http://h2database.com/html/license.html).
 * Initial Developer: H2 Group
 */
package org.h2.expression.aggregate;

import org.h2.engine.Constants;
import org.h2.engine.Database;
import org.h2.util.IntIntHashMap;
import org.h2.value.Value;
import org.h2.value.ValueInt;

/**
 * Data stored while calculating a SELECTIVITY aggregate.
 */
class AggregateDataSelectivity extends AggregateData {

    private final boolean distinct;

    private long count;
    private IntIntHashMap distinctHashes;
    private double m2;

    /**
     * Creates new instance of data for SELECTIVITY aggregate.
     *
     * @param distinct if distinct is used
     */
    AggregateDataSelectivity(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
//<<<<<<< HEAD:h2/src/main/org/h2/expression/AggregateDataSelectivity.java
//    void add(Database database, int dataType, boolean distinct, Value v) {
//        //是基于某个表达式(多数是单个字段)算不重复的记录数所占总记录数的百分比
//        //org.h2.engine.Constants.SELECTIVITY_DISTINCT_COUNT默认是1万，这个值不能改，
//        //对统计值影响很大。通常这个值越大，统计越精确，但是会使用更多内存。
//        //SELECTIVITY越大，说明重复的记录越少，在选择索引时更有利。
//=======
    void add(Database database, int dataType, Value v) {
        count++;
        if (distinctHashes == null) {
            distinctHashes = new IntIntHashMap();
        }
        int size = distinctHashes.size();
        if (size > Constants.SELECTIVITY_DISTINCT_COUNT) {
            distinctHashes = new IntIntHashMap();
            m2 += size;
        }
        int hash = v.hashCode();
        // the value -1 is not supported
        distinctHashes.put(hash, 1);
    }

    @Override
    Value getValue(Database database, int dataType) {
        if (distinct) {
            count = 0; //加distinct时，意思就是没有重复的字段值了，所以SELECTIVITY就是0
        }
        Value v = null;
        int s = 0;
        if (count == 0) {
            s = 0;
        } else {
            m2 += distinctHashes.size();
            m2 = 100 * m2 / count;
            s = (int) m2;
            s = s <= 0 ? 1 : s > 100 ? 100 : s;
        }
        v = ValueInt.get(s);
        return v.convertTo(dataType);
    }

}
