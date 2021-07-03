package roboy.context;

import java.util.HashMap;

/**
 * HashMap implementation of a value history with unique Integer keys.
 */
public class ValueHistory<V> implements AbstractValueHistory<Integer, V> {
    /**
     * This counter tracks the number of values, indices still start from 0.
     * Reading is allowed without synchronization, modifications only through generateKey().
     */
    private volatile int counter;
    private HashMap<Integer, V> data;

    public ValueHistory() {
        data = new HashMap<>();
        counter = 0;
    }

    /**
     * @return The last element added to this history.
     */
    @Override
    public V getValue() {
        if (counter == 0) {
            return null;
        } else {
            return getValue(counter - 1);
        }
    }

    /**
     * Get a copy of the last n entries added to the history.
     * Less values may be returned if there are not enough values in this history.
     * In case of no values, an empty array is returned.
     *
     * @param n The number of instances to retrieve.
     * @return A hashmap of n last values added to the history.
     */
    @Override
    public synchronized HashMap<Integer, V> getLastNValues(int n) {
        HashMap<Integer, V> response = new HashMap<>();
        int responseCounter = Math.min(n, data.size());
        int lastToRetrieve = counter - responseCounter;
        for (int i = counter - 1; i >= lastToRetrieve; i--) {
            responseCounter--;
            response.put(responseCounter, getValue(i));
        }
        return response;
    }

    /**
     * Puts a value into the history in the last place.
     *
     * @param value The value to be added.
     */
    @Override
    public synchronized void updateValue(V value) {
        if(data.size() >= getMaxLimit()) {
            data.remove(counter - getMaxLimit());
        }
        data.put(generateKey(), value);
    }

    /**
     * Checks if the value is contained in the history.
     *
     * @param value The value to be checked if exists.
     * @return true if such value is in the history and false otherwise
     */
    @Override
    public synchronized boolean contains(V value) {
        return data.containsValue(value);
    }

    /**
     * Removes all values from the history.
     *
     * @return true if the history was emptied and false otherwise
     */
    @Override
    public synchronized boolean purgeHistory() {
        data.clear();
        counter = 0;
        return data.isEmpty();
    }

    /**
     * Generates a key that is unique through incrementing an internal counter.
     *
     * @return A key which is unique in this list instance.
     */
    private synchronized int generateKey() {
        return counter++;
    }

    /**
     * In a ValueList, only getValue() and updateValue() directly access the HashMap data.
     * Setting these methods to be synchronous avoids concurrency issues.
     *
     * @param key The key of the value.
     * @return The value, or <code>null</code> if not found.
     */
    private synchronized V getValue(int key) {
        return data.getOrDefault(key, null);
    }

    @Override
    public int getNumberOfValuesSinceStart() {
        return counter;
    }
}
