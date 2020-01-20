package fr.leward.graphdesigner.state;

import fr.leward.graphdesigner.event.EnterAddNodeStateEvent;
import fr.leward.graphdesigner.event.EnterAddRelationshipStateEvent;
import fr.leward.graphdesigner.event.EnterDefaultStateEvent;
import fr.leward.graphdesigner.event.bus.EventConsumer;
import fr.leward.graphdesigner.event.bus.EventStreams;

/**
 * Created by Paul-Julien on 11/02/2015.
 */
public class StateManager {

    private State state;

    public StateManager() {
        registerStateListeners();
        // First state to be used is the default state
        EventStreams.enterDefaultStateEventStream.publish(new EnterDefaultStateEvent());
    }

    public void leaveCurrentState() {
        // TODO: To be checked, seems not a good idea when transitioning to new state
        EventStreams.enterDefaultStateEventStream.publish(new EnterDefaultStateEvent());
    }

    public void registerStateListeners() {
        EventStreams.enterDefaultStateEventStream.subscribe(enterDefaultStateEventConsumer);
        EventStreams.enterAddNodeStateEventStream.subscribe(enterAddNodeStateEventConsumer);
        EventStreams.enterAddRelationshipStateEventStream.subscribe(enterAddRelationshipStateEventConsumer);
    }

    private EventConsumer<EnterDefaultStateEvent> enterDefaultStateEventConsumer = new EventConsumer<EnterDefaultStateEvent>() {
        @Override
        public void consume(EnterDefaultStateEvent event) {
            state = new DefaultState();
            state.enterState();
        }
    };

    private  EventConsumer<EnterAddNodeStateEvent> enterAddNodeStateEventConsumer = new EventConsumer<EnterAddNodeStateEvent>() {
        @Override
        public void consume(EnterAddNodeStateEvent event) {
            state = new AddNodeState();
            state.enterState();
        }
    };

    private EventConsumer<EnterAddRelationshipStateEvent> enterAddRelationshipStateEventConsumer = new EventConsumer<EnterAddRelationshipStateEvent>() {
        @Override
        public void consume(EnterAddRelationshipStateEvent event) {
            state = new AddRelationshipState();
            state.enterState();
        }
    };

    public State getState() {
        return state;
    }
}
