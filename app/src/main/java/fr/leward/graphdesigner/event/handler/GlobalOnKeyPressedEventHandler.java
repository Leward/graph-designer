package fr.leward.graphdesigner.event.handler;

import fr.leward.graphdesigner.MainController;
import fr.leward.graphdesigner.event.EnterAddNodeStateEvent;
import fr.leward.graphdesigner.event.EnterAddRelationshipStateEvent;
import fr.leward.graphdesigner.event.EnterDefaultStateEvent;
import fr.leward.graphdesigner.event.LeaveCurrentStateEvent;
import fr.leward.graphdesigner.event.bus.EventStreams;
import fr.leward.graphdesigner.state.AddNodeState;
import fr.leward.graphdesigner.state.AddRelationshipState;
import fr.leward.graphdesigner.state.StateManager;
import fr.leward.graphdesigner.ui.Selection;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class GlobalOnKeyPressedEventHandler implements EventHandler<KeyEvent> {

//    private static final Logger log = LoggerFactory.getLogger(GlobalOnKeyPressedEventHandler.class);

    private MainController mainController;
    private Selection selection;
    private StateManager stateManager;

    public GlobalOnKeyPressedEventHandler() {
        mainController = MainController.getInstance();
        selection = mainController.getSelection();
//        stateManager = mainController.getStateManager();
    }

    @Override
    public void handle(KeyEvent event) {
        if(event.getCode() == KeyCode.DELETE) {
            mainController.deleteSelection();
        }
        else if(event.getCode() == KeyCode.N) {
            if(!selection.isSelectionLocked() && !(stateManager.getState() instanceof AddNodeState)) {
                // Leave current state
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                // Enter add node state
                mainController.getCreateNodeButton().setSelected(true);
                EventStreams.enterAddNodeStateEventStream.publish(new EnterAddNodeStateEvent());
            }
        }
        else if(event.getCode() == KeyCode.R) {
            if(!selection.isSelectionLocked() && !(stateManager.getState() instanceof AddRelationshipState)) {
                // Leave current state
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                // Enter add relationship state
                mainController.getCreateRelationshipButton().setSelected(true);
                EventStreams.enterAddRelationshipStateEventStream.publish(new EnterAddRelationshipStateEvent());
            }
        }
        else if(event.getCode() == KeyCode.ESCAPE) {
            if(stateManager.getState() instanceof AddNodeState) {
                // Leave current state
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                mainController.getCreateNodeButton().setSelected(false);
                // Enter default state
                EventStreams.enterDefaultStateEventStream.publish(new EnterDefaultStateEvent());
            }
            else if(stateManager.getState() instanceof AddRelationshipState) {
                // Leave current state
                EventStreams.leaveCurrentStateEventStream.publish(new LeaveCurrentStateEvent());
                mainController.getCreateRelationshipButton().setSelected(false);
                // Enter default state
                EventStreams.enterDefaultStateEventStream.publish(new EnterDefaultStateEvent());
            }
        }
    }
}
