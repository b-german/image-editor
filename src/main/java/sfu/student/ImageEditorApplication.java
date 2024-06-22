package sfu.student;

import javax.swing.SwingUtilities;
import sfu.student.gui.MainFrame;

public class ImageEditorApplication {

  public static void main(String[] args) {
    SwingUtilities.invokeLater(MainFrame::new);
  }
}