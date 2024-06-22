package sfu.student.gui.component;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import sfu.student.constants.UIIcon;
import sfu.student.constants.UIText;

public class TopBarManuComponent extends JMenuBar {

  private final Map<String, JMenu> topBarMenuMap;

  public TopBarManuComponent() {
    topBarMenuMap = new HashMap<>();
  }

  public void add(UIText menuName,
      UIText optionName,
      ActionListener actionListener) {
    add(menuName, optionName, null, actionListener);
  }

  public void add(UIText menuName,
      UIText optionName,
      UIIcon icon,
      ActionListener actionListener) {
    JMenuItem optionItem = new JMenuItem(optionName.getText(), Optional.ofNullable(icon)
        .map(UIIcon::getName)
        .map(UIManager::getIcon).orElse(null));
    optionItem.addActionListener(actionListener);

    JMenu jMenu = topBarMenuMap.computeIfAbsent(menuName.getText(), s -> add(new JMenu(s)));
    jMenu.add(optionItem);
  }
}
