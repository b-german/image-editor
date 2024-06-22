package sfu.student.gui.component;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.util.Optional;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.opencv.core.Mat;

public class ImageComponent extends JLabel {

  public ImageComponent(Mat matImage) {
    setHorizontalAlignment(SwingConstants.CENTER);
    setVerticalAlignment(SwingConstants.CENTER);
    setImage(matImage);
  }

  public void setImage(BufferedImage image) {
    setIcon(new ImageIcon(image));
    updateUI();
  }

  public void setImage(File image) {
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(image);
    } catch (IOException e) {
      showMessageDialog(this, "Ошибка при чтении файла", "Ошибка",
          ERROR_MESSAGE);
    }
    Optional.ofNullable(bufferedImage)
        .map(ImageIcon::new)
        .ifPresent(this::setIcon);
    updateUI();
  }

  public void setImage(Mat mat) {
    setImage(matToBufferedImage(mat));
  }

  private BufferedImage matToBufferedImage(Mat mat) {
    int type = BufferedImage.TYPE_BYTE_GRAY;
    if (mat.channels() > 1) {
      type = BufferedImage.TYPE_3BYTE_BGR;
    }
    BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
    mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData());
    return image;
  }
}
