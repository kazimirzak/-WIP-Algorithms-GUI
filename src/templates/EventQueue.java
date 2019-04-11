package templates;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

/**
 * @author Kenny Brink - kebri18@student.sdu.dk
 */

public class EventQueue {

    private List<VisualizeArray> queue;
    private int currentEvent;
    public BooleanProperty isPlaying;
    private final static long playSpeed = 250;

    public EventQueue() {
        queue = new ArrayList<>();
        currentEvent = -1;
        isPlaying = new SimpleBooleanProperty(false);
    }

    public void addEvent(VisualizeArray event) {
        queue.add(event);
    }

    public void play() {
        isPlaying.setValue(true);
        while(isPlaying.getValue() && currentEvent < queue.size()) {
            currentEvent++;
            queue.get(currentEvent).show();
            try {
                Thread.sleep(playSpeed);
            } catch (InterruptedException e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    public void pause() {
        isPlaying.setValue(false);
    }

    public void next() {
        isPlaying.setValue(false);
        if(currentEvent != queue.size()) {
            currentEvent++;
            queue.get(currentEvent).show();
        }
    }

    public void previous() {
        isPlaying.setValue(false);
        if(currentEvent > 1) {
            currentEvent--;
            queue.get(currentEvent).show();
        }
    }

    public void toStart() {
        isPlaying.setValue(false);
        currentEvent = 1;
        queue.get(currentEvent).show();
    }

    public void toEnd() {
        isPlaying.setValue(false);
        currentEvent = queue.size()-1;
        queue.get(currentEvent).show();
    }


}
