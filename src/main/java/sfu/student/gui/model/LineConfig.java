package sfu.student.gui.model;

import org.opencv.core.Point;
import org.opencv.core.Scalar;

public record LineConfig(Point start, Point end, Scalar color, Integer thickness) {

}
