package ReaderWriter;

import java.util.LinkedList;
import java.util.Queue;

public class Driver {

    public int numReader = 0;
    public int numWriter = 0;

    public Driver() {}

    public static void main(String[] args) {
        int numReader = 5;
        int numWriter = 3;

        Driver driver = new Driver();
        Queue<Object> queue = new LinkedList();

        Reader[] readers = new GoodReader[numReader];
        for (int i = 0; i < numReader; i++) {
            readers[i] = new GoodReader(i, driver, queue);
            new Thread(readers[i]).start();
        }

        Writer[] writers = new GoodWriter[numWriter];
        for (int i = 0; i < numWriter; i++) {
            writers[i] = new GoodWriter(i, driver, queue);
            new Thread(writers[i]).start();
        }
    }
}
