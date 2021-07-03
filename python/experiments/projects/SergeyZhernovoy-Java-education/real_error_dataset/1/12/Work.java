package ru.szhernovoy.pool;

/**
 * Created by admin on 26.10.2016.
 */
public class Work implements Runnable {
    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        System.out.println(String.format("Thread -%s is execute",Thread.currentThread().getId()));
    }

}
