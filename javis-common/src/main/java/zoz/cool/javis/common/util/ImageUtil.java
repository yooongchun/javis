package zoz.cool.javis.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ImageUtil {
    public static String img2base64(BufferedImage originalImage, float maxSize) throws IOException {
        // Resize the image
        int height = originalImage.getHeight();
        int width = originalImage.getWidth();
        float ratio = Math.max(height / maxSize, width / maxSize);
        BufferedImage resizedImage = null;
        if (ratio > 1) {
            height = (int) (height / ratio);
            width = (int) (width / ratio);
            resizedImage = new BufferedImage(width, height, originalImage.getType());
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, width, height, null);
            g.dispose();
        }
        // Convert the image to grayscale
        BufferedImage grayImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        Graphics g2 = grayImage.getGraphics();
        g2.drawImage(resizedImage == null ? originalImage : resizedImage, 0, 0, null);
        g2.dispose();

        // Convert the grayscale image to byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(grayImage, "jpg", out);
        byte[] bytes = out.toByteArray();

        // Encode the byte array to Base64
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static String img2base64(BufferedImage image) throws IOException {
        return img2base64(image, 1000);
    }

    public static BufferedImage base642Image(String base64_img_str) throws IOException {
        String base64String = base64_img_str.replaceFirst("^data:image/\\w+;base64,", "");
        byte[] imageBytes = Base64.getDecoder().decode(base64String);
        return ImageIO.read(new ByteArrayInputStream(imageBytes));
    }
}