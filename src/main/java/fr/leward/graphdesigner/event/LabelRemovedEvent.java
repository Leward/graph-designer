package fr.leward.graphdesigner.event;

import fr.leward.graphdesigner.event.bus.Event;
import fr.leward.graphdesigner.graph.Label;

/**
 * Created by Paul-Julien on 18/02/2015.
 */
public class LabelRemovedEvent implements Event {

    private Label label;

    public LabelRemovedEvent(Label label) {
        this.label = label;
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }
}
