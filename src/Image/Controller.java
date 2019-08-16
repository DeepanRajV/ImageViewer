package Image;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class Controller {
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Label total;
    @FXML
    private TextField input;
    @FXML
    private ImageView imageView;
    @FXML
    private Button prev;
    @FXML
    private Button next;
    private static ArrayList<BufferedImage> images;
    private static int curr;
    private static FileChooser fileChooser;

    static {
        curr = -1;
        fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File("D:\\Temp"));
        fileChooser.setTitle("Choose Image");
    }

    @FXML
    private void initialize() {
        total.setText("0");
        input.setText("0");
        input.setDisable(true);
        prev.setDisable(true);
        next.setDisable(true);
    }

    @FXML
    protected void loadImage() {
        try {
            images = new ArrayList<>();

            ImageInputStream imageIStream = ImageIO.createImageInputStream(
                    fileChooser.showOpenDialog(imageView.getScene().getWindow()));
            if (imageIStream == null || imageIStream.length() == 0)
                throw new IOException("Empty File!!");

            Iterator<ImageReader> iterator = ImageIO.getImageReaders(imageIStream);
            if (iterator == null || !iterator.hasNext())
                throw new IOException("Image format not supported!!");

            ImageReader reader = iterator.next();
            reader.setInput(imageIStream);

            int nOfPages = reader.getNumImages(true);
            for (int i = 0; i < nOfPages; i++) {
                images.add(reader.read(i));
            }

            total.setText("" + nOfPages);
            curr = 0;
            input.setDisable(images.size() < 2);
            setImageView();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void setImageView() {

        input.setText("" + (curr + 1));

        prev.setDisable(images.size() < 2 || curr == 0);
        next.setDisable(images.size() < 2 || curr == images.size() - 1);

        Image image = SwingFXUtils.toFXImage(images.get(curr), null);

        double sizeX = anchorPane.getHeight() - 43;
        double sizeY = anchorPane.getWidth() - 2;
        double imageX = image.getHeight();
        double imageY = image.getWidth();

        if (sizeX < imageX || sizeY < imageY)
            if (sizeX / sizeY >= imageX / imageY) {
                imageView.setFitHeight(sizeY / imageY * imageX);
                imageView.setFitWidth(sizeY);
            } else {
                imageView.setFitHeight(sizeX);
                imageView.setFitWidth(sizeX / imageX * imageY);
            }
        else {
            imageView.setFitHeight(imageX);
            imageView.setFitWidth(imageY);
        }
        imageView.setImage(image);

    }

    @FXML
    public void next() {
        if (curr < images.size() - 1) {
            curr++;
            setImageView();
        } else curr = images.size() - 1;
    }

    @FXML
    public void prev() {
        if (curr > 0) {
            curr--;
            setImageView();
        } else curr = 0;
    }

    @FXML
    public void setFromText() {
        try {
            int tmp = Integer.parseInt(input.getText()) - 1;
            if (tmp >= 0 && tmp < images.size()) {
                curr = tmp;
                setImageView();
            } else throw new NumberFormatException();
        } catch (NumberFormatException ignored) {
            input.setText("" + (curr + 1));
        }
    }
}
