package sfu.student.gui.dialog;

import static java.util.Objects.nonNull;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
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
import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import sfu.student.gui.component.ErrorTextArea;
import sfu.student.gui.component.NumericInput;

public class KernelSizeDialog extends JDialog {

  private final JTextField input;
  private final JTextArea error;
  private final JButton select;
  private String result;

  private KernelSizeDialog(Frame owner) {
    super(owner, "Выбор размера ядра", true);
    setSize(300, 200);
    setLocationRelativeTo(owner);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    input = new NumericInput(20, getValidator());
    input.setMaximumSize(new Dimension(Integer.MAX_VALUE, input.getPreferredSize().height));
    error = new ErrorTextArea(3, 30, getBackground());

    JPanel inputPanel = new JPanel();
    inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
    inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    Component spacer = Box.createRigidArea(new Dimension(0, 5));
    Stream.of(new JLabel("Введите размер ядра:"), spacer, input, spacer, error)
        .forEachOrdered(inputPanel::add);

    select = new JButton("Применить");
    select.addActionListener(this::select);
    JButton cancelButton = new JButton("Отмена");
    cancelButton.addActionListener(this::cancel);

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
    buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
    buttonPanel.add(select);
    buttonPanel.add(cancelButton);

    add(inputPanel);
    add(Box.createVerticalGlue());
    add(buttonPanel);

  }

  private DocumentListener getValidator() {
    return new DocumentListener() {
      @Override
      public void insertUpdate(javax.swing.event.DocumentEvent e) {
        validateInput();
      }

      @Override
      public void removeUpdate(javax.swing.event.DocumentEvent e) {
        validateInput();
      }

      @Override
      public void changedUpdate(javax.swing.event.DocumentEvent e) {
        validateInput();
      }
    };
  }

  public static Integer open(JFrame owner) {
    KernelSizeDialog dialog = new KernelSizeDialog(owner);
    dialog.setVisible(true);
    return nonNull(dialog.result) ? Integer.parseInt(dialog.result) : null;
  }

  private void select(ActionEvent e) {
    if (validateInput()) {
      result = input.getText();
      setVisible(false);
    }
  }

  private void cancel(ActionEvent e) {
    result = null;
    setVisible(false);
  }

  private boolean validateInput() {
    if (input.getText().isEmpty()) {
      error.setText(" ");
      select.setEnabled(true);
      return true;
    }
    int kernelSize = Integer.parseInt(input.getText());
    if (1 > kernelSize || kernelSize >= 100) {
      error.setText("Размер ядра должен быть не меньше 1 и не больше 99");
      select.setEnabled(false);
      return false;
    } else if (kernelSize % 2 == 0) {
      error.setText("Размер ядра должен быть нечетным.");
      select.setEnabled(false);
      return false;
    }
    error.setText(" ");
    select.setEnabled(true);
    return true;
  }
}
