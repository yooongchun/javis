package zoz.cool.javis.common.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

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
        BufferedImage img;
        img = ImageIO.read(new ByteArrayInputStream(imageBytes));
        ImageIO.write(img, "png", new File("yzm.png"));
        return img;
    }

    public static Map<String, BufferedImage> splitChannel(BufferedImage origin, int UPPER, int LOWER) {
        BufferedImage imgRed = splitColor(origin, new Color(UPPER, LOWER, LOWER));
        BufferedImage imgBlue = splitColor(origin, new Color(LOWER, LOWER, UPPER));
        BufferedImage imgBlack = splitColor(origin, new Color(LOWER, LOWER, LOWER));
        BufferedImage imgYellow = splitColor(origin, new Color(UPPER, UPPER, LOWER));

        Map<String, BufferedImage> result = new HashMap<>();
        result.put("ori", origin);
        result.put("red", imgRed);
        result.put("yellow", imgYellow);
        result.put("blue", imgBlue);
        result.put("black", imgBlack);
        return null;
    }

    private BufferedImage splitColor(BufferedImage img, Color color) {
        BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                Color pixelColor = new Color(img.getRGB(x, y));
                if (isSimilarColor(pixelColor, color)) {
                    result.setRGB(x, y, pixelColor.getRGB());
                } else {
                    result.setRGB(x, y, Color.WHITE.getRGB());
                }
            }
        }
        return result;
    }

    private boolean isSimilarColor(Color color1, Color color2) {
        return Math.abs(color1.getRed() - color2.getRed()) < LOWER &&
                Math.abs(color1.getGreen() - color2.getGreen()) < LOWER &&
                Math.abs(color1.getBlue() - color2.getBlue()) < LOWER;
    }


}