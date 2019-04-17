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

public class EventQueue {

    private List<VisualizeArray> queue;
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
        queue = new ArrayList<>();
        currentEvent = -1;
        isPlaying = new SimpleBooleanProperty(false);
        wasInterrupted = false;
        timeSpentSleeping = 0;
    }

    /**
     * Adds an event to this EventQueue.
     * @param event
     */

    public void addEvent(VisualizeArray event) {
        queue.add(event);
    }

    /**
     * Starts playing through the eventQueue. Stops when it reaches the end of the user interrupts it.
     */

    public void play() {
        isPlaying.setValue(true);
        while(isPlaying.getValue() && currentEvent < queue.size() - 1) {
            timeout();
            if(!wasInterrupted && isPlaying.getValue()) {
                currentEvent++;
                queue.get(currentEvent).show();
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
        if(currentEvent != queue.size() - 1) {
            currentEvent++;
            queue.get(currentEvent).show();
        }
    }

    /**
     * Shows the previous event of this eventQueue. Also pauses the queue.
     */

    public void previous() {
        isPlaying.setValue(false);
        if(currentEvent > 0) {
            currentEvent--;
            queue.get(currentEvent).show();
        }
    }

    /**
     * Shows the start of this eventQueue. Also pauses the queue.
     */

    public void toStart() {
        isPlaying.setValue(false);
        currentEvent = 0;
        queue.get(currentEvent).show();
    }

    /**
     * Shows the end of this eventQueue. Also pauses the queue.
     */

    public void toEnd() {
        isPlaying.setValue(false);
        currentEvent = queue.size() - 1;
        queue.get(currentEvent).show();
    }

    /**
     * Check if the eventQueue is at the end.
     * @return true if this eventQueue is at the end.
     */

    public boolean isDone() {
        return currentEvent == queue.size() - 1;
    }

    /**
     * Returns the total steps this eventQueue has.
     * @return the number of steps.
     */

    public int getTotalSteps() {
        return queue.size();
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
