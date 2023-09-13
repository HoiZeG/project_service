package faang.school.projectservice.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class CoverService {
    @Value("${image.cover.maxHeight}")
    private int maxHeight;
    @Value("${image.cover.maxWidth}")
    private int maxWidth;

    public byte[] resizeCover(MultipartFile multipartFile) {
        try {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());
            int width = image.getWidth();
            int height = image.getHeight();
            getNewSize(image, width, height);
            BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = resizedImage.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ImageIO.write(resizedImage, "jpg", outputStream);
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Error while resizing image: " + e.getMessage());
        }
    }

    public void getNewSize(BufferedImage image, int width, int height) {
        if (width == height) {
            if (width > maxWidth) {
                width = height = maxWidth;

            }
        } else if (width > maxWidth) {
            width = maxWidth;
        } else if (height > maxHeight) {
            height = maxHeight;
        }
    }
}