package sfu.student.gui.component;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JTextArea;

public class ErrorTextArea extends JTextArea {

  public ErrorTextArea(int rows, int columns, Color bgColor) {
    super(" ", rows, columns);
    setForeground(Color.RED);
    setWrapStyleWord(true);
    setLineWrap(true);
    setEditable(false);
    setBackground(bgColor);
    setAlignmentX(Component.LEFT_ALIGNMENT);
  }


}
