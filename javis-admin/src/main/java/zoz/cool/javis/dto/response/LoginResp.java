package zoz.cool.javis.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginResp {
    private String token;
    @JsonProperty("token_head")
    private String tokenHead;
}
