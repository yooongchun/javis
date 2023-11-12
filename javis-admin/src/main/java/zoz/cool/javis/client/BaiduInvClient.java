package zoz.cool.javis.client;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import zoz.cool.javis.common.exception.Asserts;
import zoz.cool.javis.common.util.HttpUtil;
import zoz.cool.javis.common.util.ImageUtil;
import zoz.cool.javis.common.util.PdfUtil;
import zoz.cool.javis.dto.FileDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * 获取数据
 */

@Component
@Slf4j
public class BaiduInvClient {
    @Value("${baidu-ocr.appId}")
    private String APP_ID;
    @Value("${baidu-ocr.appSecret}")
    private String APP_SECRET;
    @Value("${baidu-ocr.urlPattern}")
    private String URL_PATTERN;
    @Value("${baidu-ocr.urlBase}")
    private String URL_BASE;
    @Value("${baidu-ocr.urlInv}")
    private String URL_INV;

    private String getAccessToken() {
        String uri = String.format(URL_PATTERN, URL_BASE, APP_ID, APP_SECRET);
        JsonNode rootNode = HttpUtil.get(uri, null);
        return rootNode.get("access_token").asText();
    }

    private JsonNode getRawData(String imgBase64) {
        String accessToken = getAccessToken();
        Asserts.failIfNull(accessToken, "获取accessToken失败");

        String uri = URL_INV + "?access_token=" + accessToken;
        LinkedMultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("image", imgBase64);
        return HttpUtil.post(uri, body);
    }

    public JsonNode getInvoiceInfo(FileDTO file) throws IOException {
        BufferedImage originalImage;
        if (file.getContentType().contains("pdf")) {
            // PDF转图片
            originalImage = PdfUtil.convertPdfToImage(file.getBytes()).get(0);
        } else {
            // Read the image from Bytes
            ByteArrayInputStream inputStream = new ByteArrayInputStream(file.getBytes());
            originalImage = ImageIO.read(inputStream);
        }
        // 图片对象转为base64
        String imgBase64 = ImageUtil.img2base64(originalImage);
        JsonNode rootNode = getRawData(imgBase64);
        return rootNode.get("words_result");
    }
}