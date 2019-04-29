package fr.inria.spirals.jtravis.entities;

/**
 * Created by urli on 04/01/2017.
 */
public class TestsInformation {
    private int running;
    private int failing;
    private int skipping;
    private int errored;
    private int passing;

    public TestsInformation() {}

    public int getRunning() {
        return running;
    }

    public void setRunning(int running) {
        this.running = running;
    }

    public int getFailing() {
        return failing;
    }

    public void setFailing(int failing) {
        this.failing = failing;
    }

    public int getSkipping() {
        return skipping;
    }

    public void setSkipping(int skipping) {
        this.skipping = skipping;
    }

    public int getErrored() {
        return errored;
    }

    public void setErrored(int errored) {
        this.errored = errored;
    }

    public int getPassing() {
        return passing;
    }

    public void setPassing(int passing) {
        this.passing = passing;
    }
}
