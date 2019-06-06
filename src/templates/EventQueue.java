package templates;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * Creates an eventQueue to show all visuals for a scene.
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class EventQueue <T extends Showable> {

    private List<List<T>> queue;
    private int currentEvent;
    public BooleanProperty isPlaying;
    private final DoubleProperty sliderProperty;
    private final static long playSpeed = 250;
    private boolean wasInterrupted;
    private long timeSpentSleeping;

    /**
     * Constructor.
     * @param sliderProperty
     */

    public EventQueue(DoubleProperty sliderProperty) {
        this.sliderProperty = sliderProperty;
        queue = null;
        currentEvent = -1;
        isPlaying = new SimpleBooleanProperty(false);
        wasInterrupted = false;
        timeSpentSleeping = 0;
    }

    /**
     * Adds an event to this EventQueue.
     * @param event
     */

    public void addEvent(T event) {
        if(queue == null) {
            queue = new ArrayList<>();
            queue.add(new ArrayList<>());
        } else if(queue.size() > 1) {
            throw new IllegalArgumentException("You cant insert only 1 event when there are multiple queues");
        }
        queue.get(0).add(event);
    }

    public void addEvent(List<T> events) {
        if(queue == null) {
            queue = new ArrayList<>();
            for(int i = 0; i < events.size(); i++) {
                queue.add(new ArrayList<>());
            }
        } else if(queue.size() != events.size()) {
            throw new IllegalArgumentException("There are too many/few events in the list: " + queue.size() + " != " + events.size());
        }
        for(int i = 0; i < events.size(); i++) {
            queue.get(i).add(events.get(i));
        }
    }

    /**
     * Starts playing through the eventQueue. Stops when it reaches the end of the user interrupts it.
     */

    public void play() {
        isPlaying.setValue(true);
        while(isPlaying.getValue() && currentEvent < queue.get(0).size() - 1) {
            timeout();
            if(!wasInterrupted && isPlaying.getValue()) {
                currentEvent++;
                queue.forEach(list -> list.get(currentEvent).show());
            }
        }
        isPlaying.setValue(false);
    }

    /**
     * Pauses the eventQueue.
     */

    public void pause() {
        isPlaying.setValue(false);
    }

    /**
     * Shows the next event in this eventQueue. Also pauses the queue.
     */

    public void next() {
        isPlaying.setValue(false);
        if(currentEvent != queue.get(0).size() - 1) {
            currentEvent++;
            queue.forEach(list -> list.get(currentEvent).show());
        }
    }

    /**
     * Shows the previous event of this eventQueue. Also pauses the queue.
     */

    public void previous() {
        isPlaying.setValue(false);
        if(currentEvent > 0) {
            currentEvent--;
            queue.forEach(list -> list.get(currentEvent).show());
        }
    }

    /**
     * Shows the start of this eventQueue. Also pauses the queue.
     */

    public void toStart() {
        isPlaying.setValue(false);
        currentEvent = 0;
        queue.forEach(list -> list.get(currentEvent).show());
    }

    /**
     * Shows the end of this eventQueue. Also pauses the queue.
     */

    public void toEnd() {
        isPlaying.setValue(false);
        currentEvent = queue.get(0).size() - 1;
        queue.forEach(list -> list.get(currentEvent).show());
    }

    /**
     * Check if the eventQueue is at the end. Pre condition: there must be atleast 1 event in the queue when called.
     * @return true if this eventQueue is at the end.
     */

    public boolean isDone() {
        return currentEvent == queue.get(0).size() - 1;
    }

    /**
     * Times out this eventQueue. If it is interrupted it is because the user changed the speedSlider so it calls itself and check how long it should now wait for.
     */

    private void timeout() {
        try {
            if (wasInterrupted) {
                long timeToSleep = (long) (playSpeed * sliderProperty.getValue()) - timeSpentSleeping;
                if (timeToSleep > 0) {
                    timeSpentSleeping = System.currentTimeMillis();
                    Thread.sleep(timeToSleep);
                } else {
                    timeSpentSleeping = System.currentTimeMillis();
                    Thread.sleep(0);
                }
                wasInterrupted = false;
            } else {
                timeSpentSleeping = System.currentTimeMillis();
                Thread.sleep((long) (playSpeed * sliderProperty.getValue()));
            }
        } catch (InterruptedException e) {
            timeSpentSleeping = System.currentTimeMillis() - timeSpentSleeping;
            wasInterrupted = true;
            timeout();
        }
    }
}
