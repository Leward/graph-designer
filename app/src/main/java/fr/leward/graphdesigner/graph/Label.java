package fr.leward.graphdesigner.graph;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by Paul-Julien on 16/02/2015.
 */
public class Label {

    private final String label;
    private Color color = Color.BLACK;

    public static Color[] colors = new Color[] {
        Color.LIGHTCORAL,   Color.LIGHTSTEELBLUE,   Color.LIGHTGREEN,
        Color.GAINSBORO,    Color.PEACHPUFF,        Color.MEDIUMPURPLE
    };

    public Label(String label) {
        this.label = label;
        color = pickRandomColor();
    }

    public Label(String label, Color color) {
        this.label = label;
        this.color = color;
    }

    public String getLabel() {
        return label;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public static Color pickRandomColor() {
        Random random = new Random();
        int index = random.nextInt(colors.length - 1);
        return colors[index];
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Label) && ((Label) obj).getLabel().equals(label);
    }
}
