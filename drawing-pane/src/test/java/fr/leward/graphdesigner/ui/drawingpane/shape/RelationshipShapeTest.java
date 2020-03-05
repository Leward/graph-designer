package fr.leward.graphdesigner.ui.drawingpane.shape;

import javafx.geometry.Point2D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class RelationshipShapeTest {

    @ParameterizedTest
    @MethodSource("testLabelAngleValues")
    public void testLabelAngle(double xA, double yA, double xB, double yB, double expectedAngle) {
        var a = new Point2D(xA, yA);
        var b = new Point2D(xB, yB);
        var angle = RelationshipShape.calculateLabelAngle(a, b);
        Assertions.assertEquals(expectedAngle, Math.round(angle));
    }

    public static Stream<Arguments> testLabelAngleValues() {
        return Stream.of(
                Arguments.of(100, 100, 200, 100, 0),
                Arguments.of(100, 100, 200, 200, 45),
                Arguments.of(100, 100, 100, 200, 90)
                // TODO: Add more test cases
        );
    }

}