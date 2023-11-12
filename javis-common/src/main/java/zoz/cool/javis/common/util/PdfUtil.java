package zoz.cool.javis.common.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class PdfUtil {

    public static List<BufferedImage> convertPdfToImage(byte[] pdfData) {
        List<BufferedImage> images = new ArrayList<>();
        try (InputStream in = new ByteArrayInputStream(pdfData)) {
            PDDocument document = PDDocument.load(in);
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300);
                images.add(bim);
            }
            document.close();
        } catch (IOException e) {
            throw new RuntimeException("PDF转图片失败:" + e.getMessage());
        }
        return images;
    }
}