package zoz.cool.javis.common.util;


import cn.hutool.core.util.StrUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ToolKit {
    public static String randomCode(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append((int) (Math.random() * 10));
        }
        return code.toString();
    }

    public static boolean isValidPhone(String phone) {
        return StrUtil.isNotEmpty(phone) && phone.matches("^1[3-9]\\d{9}$");
    }

    public static boolean isValidEmail(String email) {
        return StrUtil.isNotEmpty(email) && email.matches("^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$");
    }

    public static String parseSecretKey(String key) {
        return key.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\n", "");
    }
}