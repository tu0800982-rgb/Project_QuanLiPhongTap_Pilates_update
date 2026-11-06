package com.nhom8.pilates;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties={
        "spring.datasource.url=jdbc:h2:mem:api;MODE=MySQL;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa","spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=none","spring.flyway.enabled=true",
        "app.demo-data.enabled=true"
})
class ApiSecurityIntegrationTest {
    @Autowired MockMvc mvc;@Autowired ObjectMapper mapper;

    @Test
    void demoAccountsAuthenticateAndMemberCannotReadMemberAdministration() throws Exception {
        String token=login("member@pilates.com");
        mvc.perform(get("/api/v1/profile").header("Authorization","Bearer "+token))
                .andExpect(status().isOk()).andExpect(jsonPath("$.data.role").value("MEMBER"));
        mvc.perform(get("/api/v1/members").header("Authorization","Bearer "+token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminCanReadReportsAndHealthIsPublic() throws Exception {
        String token=login("admin@pilates.com");
        mvc.perform(get("/api/v1/reports/overview").header("Authorization","Bearer "+token)).andExpect(status().isOk());
        mvc.perform(get("/actuator/health")).andExpect(status().isOk()).andExpect(jsonPath("$.status").value("UP"));
    }

    private String login(String email)throws Exception{
        String json="{\"email\":\""+email+"\",\"password\":\"123456\"}";
        String response=mvc.perform(post("/api/v1/auth/login").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        JsonNode root=mapper.readTree(response);return root.path("data").path("accessToken").asText();
    }
}
