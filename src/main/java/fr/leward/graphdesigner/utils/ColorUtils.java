package fr.leward.graphdesigner.utils;

import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Paul-Julien on 05/04/2015.
 */
public class ColorUtils {

    // Logger
    private static final Logger log = LoggerFactory.getLogger(ColorUtils.class);

    /**
     * Transform a JavaFX Color object into a CSS hexadecimal String such as #FFFFFF
     * @param color
     * @return
     */
    public static String colorToCSSHexaCode(Color color) {
        StringBuilder sb = new StringBuilder("#");

        // Red
        long red = Math.round(color.getRed() * 255);
        String redHexa = Long.toHexString(red);
        if(redHexa.length() == 1) {
            redHexa = "0" + redHexa;
        }
        sb.append(redHexa);

        // Green
        long green = Math.round(color.getGreen() * 255);
        String greenHexa = Long.toHexString(green);
        if(greenHexa.length() == 1) {
            greenHexa = "0" + greenHexa;
        }
        sb.append(greenHexa);

        // Blue
        long blue = Math.round(color.getBlue() * 255);
        String blueHexa = Long.toHexString(blue);
        if(blueHexa.length() == 1) {
            blueHexa = "0" + blueHexa;
        }
        sb.append(blueHexa);

        return sb.toString();
    }

}
