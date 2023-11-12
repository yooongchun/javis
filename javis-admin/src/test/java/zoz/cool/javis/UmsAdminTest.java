package zoz.cool.javis;

import cn.hutool.json.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
public class UmsAdminTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLogin() throws Exception {
        Map<String, String> body = new HashMap<>();
        body.put("email", "admin@163.com");
        body.put("password", "macro123");
        mockMvc.perform(MockMvcRequestBuilders.post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper(body).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.token_head").value("Authorization"));
    }
}
