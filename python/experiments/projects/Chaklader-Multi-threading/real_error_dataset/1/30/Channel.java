package GlobalSnapshot;

import java.util.LinkedList;
import java.util.Queue;

public class Channel {

    private Account sender;
    private Account receiver;
    private Queue<Event> queue;

    private boolean isRecording;
    private int state;

    public Channel(Account sender, Account receiver) {
        this.sender = sender;
        this.receiver = receiver;
        queue = new LinkedList<>();

        isRecording = false;
        state = 0;

    }

    public void startRecording() {
        isRecording = true;
    }

    public void stopRecording() {
        isRecording = false;

        System.out.printf("State of channel[%d][%d]: %d\n", sender.getId(), receiver.getId(), state);
    }

    public boolean isRecording() {
        return isRecording;
    }

    public void add(Event event) {
        queue.add(event);
    }

    public Event poll() {
        Event front = queue.poll();
        if (isRecording && front instanceof Transaction) {
            state += ((Transaction) front).getAmount();
        }
        return front;
    }
}
