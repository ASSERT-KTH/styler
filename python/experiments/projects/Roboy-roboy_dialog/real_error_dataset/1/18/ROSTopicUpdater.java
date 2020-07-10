package roboy.context;

import org.ros.message.MessageListener;
import roboy.ros.RosMainNode;
import roboy.ros.RosSubscribers;

/**
 * An external updater connected to a ROS topic that can push the arriving values to the target.
 * The update() method should implement the logic of adding to the target.
 * @param <Message> Type of messages from the ROS topic.
 * @param <Target> The target object to be updated.
 */
public abstract class ROSTopicUpdater<Message,Target> extends ExternalUpdater {
    protected final Target target;
    protected volatile Message message;
    protected final RosSubscribers targetSubscriber;

    /**
     * Implement this in the subclass to define the ROS subscriber this updater should use.
     */
    protected abstract RosSubscribers getTargetSubscriber();


    public ROSTopicUpdater(Target target, RosMainNode ros) {
        this.target = target;
        targetSubscriber = getTargetSubscriber();
        start(ros);
    }

    /**
     * Starts a new MessageListener.
     */
    private void start(RosMainNode ros) {
        MessageListener<Message> listener = m -> {
            message = m;
            update();
        };
        addListener(listener, ros);
    }

    protected void addListener(MessageListener listener, roboy.ros.RosMainNode ros) {
        ros.addListener(listener, getTargetSubscriber());
    }
}
