package zoz.cool.javis.common.constants;

import lombok.Getter;

@Getter
public enum InvStatus {
    INIT(0, "初始化"),
    PROCESSING(1, "处理中"),
    SUCCESS(2, "成功"),
    FAIL(-1, "失败");

    private final int code;
    private final String status;

    InvStatus(int code, String status) {
        this.code = code;
        this.status = status;
    }
}
