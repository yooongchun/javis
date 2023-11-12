package zoz.cool.javis.client;

import com.microsoft.playwright.*;
import zoz.cool.javis.common.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class BrowserClient {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(false);
            Browser browser = playwright.chromium().launch(options);
            Browser.NewContextOptions contextOptions = new Browser.NewContextOptions().setIgnoreHTTPSErrors(true);
            BrowserContext context = browser.newContext(contextOptions);
            Page page = context.newPage();
            page.navigate("https://inv-veri.chinatax.gov.cn/index.html");

            fillBase(page);
            page.offRequestFinished((request) -> {
                System.out.printf("request finished: %s\n", request.response().status());
            });
            page.offRequestFailed((request) -> {
                System.out.printf("request failed: %s\n", request.response().status());
            });
            String base64Img = getVerifyImg(page);
            System.out.printf("yzm image: %s\n", base64Img);
//            ImageUtil.base642Image(base64Img);
            Path path = Paths.get("example.png");
            System.out.println("Save to " + path.toAbsolutePath());
            page.screenshot(new Page.ScreenshotOptions().setPath(path));
            System.out.println(page.title());
            try {
                Thread.sleep(10000);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 填充基本信息
     */
    private static void fillBase(Page page) {
        page.fill("id=fpdm", "033002300511");
        page.fill("id=fphm", "14137752");
        page.fill("id=kprq", "20231103");
        page.fill("id=kjje", "407320");
    }

    /**
     * 获取验证码图片
     */
    private static String getVerifyImg(Page page) {
        page.click("id=yzm_img");
        String DEFAULT_YZM_STR = "images/code.png";
        for (int i = 0; i < 5; i++) {
            System.out.println("try get yzm image " + (i + 1) + " times");
            Locator yzmImg = page.locator("id=yzm_img");
            String base64Str = yzmImg.getAttribute("src");
            if (!DEFAULT_YZM_STR.equals(base64Str)) {
                return base64Str;
            }
            try {
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("get yzm image failed");
    }


}