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
        Assertions.assertEquals(expectedAngle, angle);
    }

    public static Stream<Arguments> testLabelAngleValues() {
        return Stream.of(
                Arguments.of(100, 100, 200, 100, 0),
                Arguments.of(100, 100, 200, 200, 45),
                Arguments.of(100, 100, 100, 200, 90),
                Arguments.of(200, 100, 100, 100, 0),
                Arguments.of(200, 200, 100, 100, 45),
                Arguments.of(200, 200, 200, 100, 90),
                Arguments.of(100, 50, 0, 0, 26.57) // https://www.wolframalpha.com/input/?i=VectorAngle%5B%7B1%2C+0%7D%2C+%7B100%2C+50%7D%5D
        );
    }

}