package sfu.student.gui;

import static java.util.Objects.isNull;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;
import static sfu.student.constants.UIIcon.COMPUTER;
import static sfu.student.constants.UIIcon.DIRECTORY;
import static sfu.student.constants.UIText.MENU_CHANNEL;
import static sfu.student.constants.UIText.MENU_CHANNEL_BLUE;
import static sfu.student.constants.UIText.MENU_CHANNEL_GREEN;
import static sfu.student.constants.UIText.MENU_CHANNEL_RED;
import static sfu.student.constants.UIText.MENU_CHANNEL_RGB;
import static sfu.student.constants.UIText.MENU_EDIT;
import static sfu.student.constants.UIText.MENU_EDIT_DRAW_LINE;
import static sfu.student.constants.UIText.MENU_EDIT_GAUSS;
import static sfu.student.constants.UIText.MENU_EDIT_TO_GRAYSCALE;
import static sfu.student.constants.UIText.MENU_FILE;
import static sfu.student.constants.UIText.MENU_FILE_OPEN;
import static sfu.student.constants.UIText.MENU_FILE_REPLACE_DEFAULT;
import static sfu.student.constants.UIText.MENU_FILE_TAKE_PHOTO;
import static sfu.student.constants.UIText.TITLE;

import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.random.RandomGenerator;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import sfu.student.ImageEditorApplication;
import sfu.student.gui.component.ImageComponent;
import sfu.student.gui.component.TopBarManuComponent;
import sfu.student.gui.dialog.DrawLineDialog;
import sfu.student.gui.dialog.KernelSizeDialog;
import sfu.student.gui.model.LineConfig;

public class MainFrame extends JFrame {

  static {
    OpenCV.loadLocally();
  }

  private ImageComponent image;

  private transient Mat currentImage;

  public MainFrame() {
    super(TITLE.getText());
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setSize(1024, 768);

    initImage();
    initTopBarMenu();

    setVisible(true);
  }

  private void initImage() {
    currentImage = getRandomExampleImage();
    image = new ImageComponent(currentImage);
    add(image);
  }

  private Mat getRandomExampleImage() {
    List<String> examples = List.of("1.jpg", "2.png", "3.png", "4.jpg", "5.jpg");
    int idx = RandomGenerator.getDefault().nextInt(0, examples.size());
    ByteArrayOutputStream baos;
    try (InputStream in = ImageEditorApplication.class
        .getClassLoader()
        .getResourceAsStream(examples.get(idx))) {
      baos = new ByteArrayOutputStream();
      in.transferTo(baos);
      return Imgcodecs.imdecode(new MatOfByte(baos.toByteArray()), Imgcodecs.IMREAD_UNCHANGED);
    } catch (IOException e) {
      showMessageDialog(this, "Невозможно прочитать случайную картинку", "Ошибка",
          ERROR_MESSAGE);
    }
    return currentImage;
  }

  private void initTopBarMenu() {
    TopBarManuComponent topBarMenu = new TopBarManuComponent();

    topBarMenu.add(MENU_FILE, MENU_FILE_OPEN, DIRECTORY, this::openFromExplorer);
    topBarMenu.add(MENU_FILE, MENU_FILE_TAKE_PHOTO, COMPUTER, this::takeWebcamPhoto);
    topBarMenu.add(MENU_FILE, MENU_FILE_REPLACE_DEFAULT, this::replaceDefaultImage);

    topBarMenu.add(MENU_CHANNEL, MENU_CHANNEL_RED, this::channelToRed);
    topBarMenu.add(MENU_CHANNEL, MENU_CHANNEL_GREEN, this::channelToGreen);
    topBarMenu.add(MENU_CHANNEL, MENU_CHANNEL_BLUE, this::channelToBlue);
    topBarMenu.add(MENU_CHANNEL, MENU_CHANNEL_RGB, this::channelToDefault);

    topBarMenu.add(MENU_EDIT, MENU_EDIT_TO_GRAYSCALE, this::toGrayscale);
    topBarMenu.add(MENU_EDIT, MENU_EDIT_GAUSS, this::applyGaussianBlur);
    topBarMenu.add(MENU_EDIT, MENU_EDIT_DRAW_LINE, this::drawLine);

    setJMenuBar(topBarMenu);
  }

  private void drawLine(ActionEvent actionEvent) {
    if (canNotContinueProcessingImage()) {
      return;
    }
    LineConfig config = DrawLineDialog.open(this, currentImage.rows(), currentImage.cols());
    if (isNull(config)) {
      return;
    }
    Imgproc.line(currentImage, config.start(), config.end(), config.color(), config.thickness());
    image.setImage(currentImage);
  }

  private void applyGaussianBlur(ActionEvent actionEvent) {
    Integer kernelSize = KernelSizeDialog.open(this);
    if (isNull(kernelSize)) {
      return;
    }

    Imgproc.GaussianBlur(currentImage, currentImage, new Size(kernelSize, kernelSize), 0);
    image.setImage(currentImage);
  }


  private void toGrayscale(ActionEvent actionEvent) {
    if (canNotContinueProcessingImage()) {
      return;
    }
    Mat grayImage = new Mat();
    Imgproc.cvtColor(currentImage, grayImage, Imgproc.COLOR_BGR2GRAY);
    currentImage = grayImage;
    image.setImage(grayImage);
  }

  private void takeWebcamPhoto(ActionEvent e) {
    VideoCapture camera = new VideoCapture(0);

    if (!camera.isOpened()) {
      showMessageDialog(this, "Камера недоступна", "Ошибка",
          ERROR_MESSAGE);
      return;
    }

    Mat frame = new Mat();
    camera.read(frame);

    if (!frame.empty()) {
      currentImage = frame;
      image.setImage(currentImage);
    } else {
      showMessageDialog(this, "Камера вернула пустой кадр", "Ошибка",
          ERROR_MESSAGE);
    }
    camera.release();
  }

  public Mat fileToMat(File file) {
    try {
      byte[] fileContent = Files.readAllBytes(file.toPath());
      MatOfByte matOfByte = new MatOfByte(fileContent);
      return Imgcodecs.imdecode(matOfByte, Imgcodecs.IMREAD_COLOR);
    } catch (IOException ex) {
      showMessageDialog(this, "Ошибка при чтении файла", "Ошибка",
          ERROR_MESSAGE);
    }
    return getRandomExampleImage();
  }

  private Mat getSpecificChannelMat(int channel) {
    if (canNotContinueProcessingImage()) {
      return currentImage;
    }

    List<Mat> channels = new ArrayList<>();
    Core.split(currentImage, channels);

    Mat emptyChannel = Mat.zeros(channels.get(0).size(), channels.get(0).type());
    List<Mat> singleChannelList = switch (channel) {
      case 0 -> List.of(channels.get(0), emptyChannel, emptyChannel);
      case 1 -> List.of(emptyChannel, channels.get(1), emptyChannel);
      case 2 -> List.of(emptyChannel, emptyChannel, channels.get(2));
      default -> List.of(emptyChannel, emptyChannel, emptyChannel);
    };

    Mat resultMat = new Mat();
    Core.merge(singleChannelList, resultMat);
    return resultMat;
  }

  private void channelToDefault(ActionEvent actionEvent) {
    image.setImage(currentImage);
  }

  private void channelToRed(ActionEvent e) {
    changeImageChannel(2);
  }

  private void channelToGreen(ActionEvent e) {
    changeImageChannel(1);
  }

  private void channelToBlue(ActionEvent e) {
    changeImageChannel(0);
  }

  private void changeImageChannel(int channel) {
    image.setImage(getSpecificChannelMat(channel));
  }

  private void openFromExplorer(ActionEvent e) {
    JFileChooser fileChooser = new JFileChooser();
    FileNameExtensionFilter filter = new FileNameExtensionFilter("Изображения",
        "jpg",
        "jpeg",
        "png");
    fileChooser.setFileFilter(filter);
    int returnValue = fileChooser.showOpenDialog(this);

    if (returnValue == JFileChooser.APPROVE_OPTION) {
      image.setImage(fileChooser.getSelectedFile());
      currentImage = fileToMat(fileChooser.getSelectedFile());
    }
  }

  private void replaceDefaultImage(ActionEvent e) {
    currentImage = getRandomExampleImage();
    image.setImage(currentImage);
  }

  private boolean canNotContinueProcessingImage() {
    List<Mat> channels = new ArrayList<>();
    Core.split(currentImage, channels);

    if (channels.size() != 3) {
      showMessageDialog(this,
          "У изображения количество каналов отличается от 3",
          "Ошибка",
          ERROR_MESSAGE);
      return true;
    }

    return false;
  }

}