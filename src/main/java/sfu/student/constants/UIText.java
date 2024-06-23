package sfu.student.constants;

public enum UIText {
  TITLE("Программа для редактирования изображений"),
  MENU_FILE("Файл"),
  MENU_FILE_OPEN("Открыть"),
  MENU_FILE_TAKE_PHOTO("Сфотографировать"),
  MENU_FILE_REPLACE_DEFAULT("Заменить случайным изображением"),
  MENU_CHANNEL("Канал"),
  MENU_CHANNEL_RED("Красный"),
  MENU_CHANNEL_GREEN("Зелёный"),
  MENU_CHANNEL_BLUE("Синий"),
  MENU_CHANNEL_RGB("По умолчанию"),
  MENU_EDIT("Редактировать"),
  MENU_EDIT_GAUSS("Размытие по Гауссу"),
  MENU_EDIT_TO_GRAYSCALE("В оттенках серого"),
  MENU_EDIT_DRAW_LINE("Нарисовать линию");
  private final String text;

  UIText(String text) {
    this.text = text;
  }

  public String getText() {
    return text;
  }
}
