package ReaderWriter;

import utils.Util;

import java.util.Queue;

public class GoodWriter extends Writer {
    
    private Queue<Object> queue;
    public boolean okToGo;

    public GoodWriter(int id, Driver driver, Queue<Object> queue) {
        super(id, driver);
        this.queue = queue;
    }

    public void startWrite() {
        synchronized (queue) {
            System.out.println("Writer " + id + " is trying to write.");
            if (driver.numReader > 0 || driver.numWriter > 0) {
                okToGo = false;
                queue.add(this);
            } else {
                okToGo = true;
                driver.numWriter++;
            }
        }
        synchronized (this) {
            if (!okToGo)
                Util.wait(this);
        }
    }

    public void endWrite() {
        synchronized (queue) {
            System.out.println("Writer " + id + " finished writing.");
            driver.numWriter--;
            if (!queue.isEmpty()) {
                if (queue.peek() instanceof GoodWriter) {
                    driver.numWriter++;
                    GoodWriter request = (GoodWriter) queue.remove();
                    synchronized (request) {
                        request.okToGo = true;
                        request.notify();
                    }
                } else {
                    while (!queue.isEmpty() && queue.peek() instanceof Reader) {
                        driver.numReader++;
                        GoodReader request = (GoodReader) queue.remove();
                        synchronized (request) {
                            request.okToGo = true;
                            request.notify();
                        }
                    }
                }
            }
        }
    }
}
