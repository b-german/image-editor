package sfu.student.gui.dialog;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.time.temporal.ValueRange;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import sfu.student.gui.component.ErrorTextArea;
import sfu.student.gui.component.NumericInput;
import sfu.student.gui.model.LineConfig;

public class DrawLineDialog extends JDialog {

  private final NumericInput startX;
  private final NumericInput startY;
  private final NumericInput endX;
  private final NumericInput endY;
  private final NumericInput thickness;
  private final JTextArea errorTextArea;
  private final JButton confirmButton;
  private transient LineConfig result;
  private final int maxX;
  private final int maxY;

  private DrawLineDialog(Frame owner, int maxX, int maxY) {
    super(owner, "Настройка линии", true);
    this.maxX = maxX - 1;
    this.maxY = maxY - 1;
    setSize(400, 300);
    setLocationRelativeTo(owner);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    startX = new NumericInput(5, getValidator());
    startY = new NumericInput(5, getValidator());
    endX = new NumericInput(5, getValidator());
    endY = new NumericInput(5, getValidator());
    thickness = new NumericInput(5, getValidator());
    errorTextArea = new ErrorTextArea(3, 30, getBackground());
    confirmButton = new JButton("Применить");

    add(initMainPanel());
    add(initErrorTextArea());
    add(Box.createVerticalGlue());
    add(initButtonsPanel());
  }

  public static LineConfig open(JFrame owner, int maxX, int maxY) {
    DrawLineDialog dialog = new DrawLineDialog(owner, maxX, maxY);
    dialog.setVisible(true);
    return dialog.result;
  }

  private JPanel initMainPanel() {
    JPanel mainPanel = new JPanel();
    mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
    mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

    Stream.of(getRowPanel(Stream.of(new JLabel("Начало"), new JLabel("Конец"))),
        Box.createRigidArea(new Dimension(20, 0)),
        getRowPanel(Stream.of(startX, startY, endX, endY)),
        getRowPanel(Stream.of(new JLabel("Толщина"))),
        getRowPanel(Stream.of(thickness))).forEachOrdered(mainPanel::add);
    return mainPanel;
  }

  private JPanel initErrorTextArea() {
    JPanel errorPanel = new JPanel();
    errorPanel.add(errorTextArea);
    return errorPanel;
  }

  private JPanel initButtonsPanel() {
    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    confirmButton.setEnabled(false);
    JButton cancelButton = new JButton("Отмена");

    confirmButton.addActionListener(e -> {
      if (validateInput()) {
        result = getResult();
        setVisible(false);
      }
    });

    cancelButton.addActionListener(e -> {
      result = null;
      setVisible(false);
    });

    buttonPanel.add(confirmButton);
    buttonPanel.add(cancelButton);
    return buttonPanel;
  }

  private static JPanel getRowPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new GridLayout(1, 0, 10, 0));
    return panel;
  }

  private static JPanel getRowPanel(Stream<Component> componentStream) {
    JPanel panel = getRowPanel();
    componentStream.forEachOrdered(panel::add);
    return panel;
  }

  private DocumentListener getValidator() {
    return new DocumentListener() {
      @Override
      public void insertUpdate(DocumentEvent e) {
        validateInput();
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
        validateInput();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
        validateInput();
      }
    };
  }

  private LineConfig getResult() {
    final Scalar green = new Scalar(0, 255, 0);
    return new LineConfig(getStart(), getEnd(), green, getThickness());
  }

  private Point getStart() {
    return getPoint(startX.getText(), startY.getText());
  }

  private Point getEnd() {
    return getPoint(endX.getText(), endY.getText());
  }

  private Integer getThickness() {
    return thickness.getText().isEmpty() ? null : Integer.parseInt(thickness.getText());
  }

  private Point getPoint(String coordinateX, String coordinateY) {
    if (coordinateX.isEmpty() || coordinateY.isEmpty()) {
      return null;
    }
    return new Point(Double.parseDouble(coordinateX), Double.parseDouble(coordinateY));
  }

  private boolean validateInput() {
    StringBuilder sb = new StringBuilder();

    Point start = getStart();
    Point end = getEnd();
    if (nonNull(start) && isPointOutOfRange(start)) {
      sb.append("Точка начала должна быть в следующих рамках: x:[0, %s], y:[0, %s]".formatted(maxX,
          maxY));
    }
    if (nonNull(end) && isPointOutOfRange(end)) {
      if (!sb.isEmpty()) {
        sb.append(System.lineSeparator());
      }
      sb.append(
          "Точка конца должна быть в следующих рамках: x:[0, %s], y:[0, %s]".formatted(maxX, maxY));
    }

    Integer thickness = getThickness();
    if (nonNull(thickness) && !ValueRange.of(1, 50).isValidValue(thickness)) {
      if (!sb.isEmpty()) {
        sb.append(System.lineSeparator());
      }
      sb.append("Толщина линии должна быть не меньше 1 и не больше 50");
    }

    if (isNull(start) || isNull(end) || isNull(thickness)) {
      if (!sb.isEmpty()) {
        errorTextArea.setText(sb.toString());
      } else {
        errorTextArea.setText(" ");
      }
      confirmButton.setEnabled(false);
      return false;
    }

    if (sb.isEmpty()) {
      errorTextArea.setText(" ");
      confirmButton.setEnabled(true);
      return true;
    } else {
      errorTextArea.setText(sb.toString());
      confirmButton.setEnabled(false);
      return false;
    }
  }

  private boolean isPointOutOfRange(Point point) {
    return !isPointValid((int) point.x, (int) point.y);
  }

  private boolean isPointValid(int x, int y) {
    return ValueRange.of(0, maxX).isValidValue(x) && ValueRange.of(0, maxY).isValidValue(y);
  }
}
