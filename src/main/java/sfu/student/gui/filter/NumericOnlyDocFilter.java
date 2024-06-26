package sfu.student.gui.filter;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

public class NumericOnlyDocFilter extends DocumentFilter {

  @Override
  public void insertString(FilterBypass fb,
      int offset,
      String string,
      AttributeSet attr) throws BadLocationException {
    if (isNumeric(string)) {
      super.insertString(fb, offset, string, attr);
    }
  }

  @Override
  public void replace(FilterBypass fb,
      int offset,
      int length,
      String text,
      AttributeSet attrs) throws BadLocationException {
    if (isNumeric(text)) {
      super.replace(fb, offset, length, text, attrs);
    }
  }

  private static boolean isNumeric(String text) {
    return text != null && text.matches("\\d*");
  }

}