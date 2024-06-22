package sfu.student.gui.component;

import javax.swing.JTextField;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import sfu.student.gui.filter.NumericOnlyDocFilter;

public class NumericInput extends JTextField {

  public NumericInput(int columns, DocumentListener validator) {
    super(columns);
    ((AbstractDocument) getDocument()).setDocumentFilter(new NumericOnlyDocFilter());
    getDocument().addDocumentListener(validator);
  }
}
