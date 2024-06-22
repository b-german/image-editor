package sfu.student.constants;

public enum UIIcon {
  COMPUTER("FileView.computerIcon"),
  DIRECTORY("FileView.directoryIcon"),
  ;

  private final String name;

  UIIcon(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
