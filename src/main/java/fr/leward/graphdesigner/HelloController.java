package fr.leward.graphdesigner;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class HelloController
{
//    private static final Logger log = LoggerFactory.getLogger(HelloController.class);

    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private Label messageLabel;

    public void sayHello() {

        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        StringBuilder builder = new StringBuilder();

        if (!firstName.trim().isEmpty()) {
            builder.append(firstName);
        }

        if (!lastName.trim().isEmpty()) {
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(lastName);
        }

        if (builder.length() > 0) {
            String name = builder.toString();
//            log.debug("Saying hello to " + name);
            messageLabel.setText("Hello " + name);
        } else {
//            log.debug("Neither first name nor last name was set, saying hello to anonymous person");
            messageLabel.setText("Hello mysterious person");
        }
    }

}
