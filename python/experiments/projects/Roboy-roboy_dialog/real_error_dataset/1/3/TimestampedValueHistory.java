package roboy.context;

import java.util.Iterator;
import java.util.TreeMap;

/**
 * Sample implementation of a ValueHistory using timestamps (longs) as keys
 * and a TreeMap for data storage.
 *
 * The timestamps are equal or larger than the time when updateValue() was called.
 * Implementation does not guarantee perfect timestamp accuracy, but achieves key uniqueness.
 *
 */
public class TimestampedValueHistory<V> implements AbstractValueHistory<Long, V> {
    /**
     * Marks the last time a value was added to the history (or initialization).
     */
    private volatile long lastTime;
    private TreeMap<Long, V> data;
    private int totalValuesAdded;

    public TimestampedValueHistory() {
        data = new TreeMap<>();
        lastTime = System.nanoTime();
    }

    /**
     * @return The last element added to this history, or <code>null</code> if not found.
     */
    @Override
    public synchronized V getValue() {
        if (data.isEmpty()) return null;
        return data.lastEntry().getValue();
    }

    /**
     * Get a copy of the last n entries added to the history.
     * Less values may be returned if there are not enough values in this history.
     * In case of no values, an empty map is returned.
     *
     * Needs to be synchronized because data cannot be changed while working with an Iterator.
     */
    @Override
    public synchronized TreeMap<Long, V> getLastNValues(int n) {
        TreeMap<Long, V> map = new TreeMap<>();
        Iterator<Long> keyIterator = data.descendingKeySet().iterator();
        Long key;
        while (keyIterator.hasNext() && (n > 0)) {
            key = keyIterator.next();
            map.put(key, data.get(key));
            n--;
        }
        return map;
    }

    /**
     * Puts a value into the history in the last place.
     */
    @Override
    public synchronized void updateValue(V value) {
        if(data.size() >= getMaxLimit()) {
            data.remove(data.firstKey());
        }
        data.put(generateKey(), value);
        totalValuesAdded++;
    }

    private synchronized long generateKey() {
        long currentTime = System.nanoTime();
        // Avoid duplicates (synchronized method, so no concurrent modifications to lastTime can happen).
        if (lastTime <= currentTime) {
            // Catch up with system time.
            lastTime = currentTime + 1;
        } else {
            // Continue with current counter.
            lastTime++;
        }
        return lastTime;
    }

    @Override
    public int getNumberOfValuesSinceStart() {
        return totalValuesAdded;
    }

    @Override
    public synchronized boolean contains(V value) {
        return data.containsValue(value);
    }

    @Override
    public synchronized boolean purgeHistory() {
        data.clear();
        return data.isEmpty();
    }
}
