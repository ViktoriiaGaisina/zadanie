package com.otp.zadanie;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otp.zadanie.repository.RoleRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerIT {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    RoleRepository roleRepository;

    private String toJson(Object body) throws Exception {
        return objectMapper.writeValueAsString(body);
    }

    @Test
    @DisplayName("POST -> GET -> PUT -> DELETE happy path and 404 after delete")
    void crudEndpoints_workAsExpected() throws Exception {
        var createBody = Map.of(
                "fio", "Ivan Ivanov",
                "phoneNumber", "+71234567890",
                "avatar", "https://example.com/a.png",
                "role", "Manager"
        );
        var createRes = mvc.perform(post("/api/createNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userID").exists())
                .andExpect(jsonPath("$.fio").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.phoneNumber").value("+71234567890"))
                .andExpect(jsonPath("$.avatar").value("https://example.com/a.png"))
                .andExpect(jsonPath("$.role").value("Manager"))
                .andReturn();

        var created = objectMapper.readTree(createRes.getResponse().getContentAsString());
        String id = created.get("userID").asText();

        mvc.perform(get("/api/users").param("userID", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userID").value(id))
                .andExpect(jsonPath("$.fio").value("Ivan Ivanov"))
                .andExpect(jsonPath("$.role").value("Manager"));

        var updateBody = Map.of(
                "userID", UUID.fromString(id).toString(),
                "fio", "Ivan Petrov",
                "phoneNumber", "+79876543210",
                "avatar", "https://example.com/b.png",
                "role", "Director"
        );
        mvc.perform(put("/api/userDetailsUpdate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fio").value("Ivan Petrov"))
                .andExpect(jsonPath("$.phoneNumber").value("+79876543210"))
                .andExpect(jsonPath("$.avatar").value("https://example.com/b.png"))
                .andExpect(jsonPath("$.role").value("Director"));

        mvc.perform(delete("/api/users").param("userID", id))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/users").param("userID", id))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Role is deleted when no other users reference it; retained if still referenced")
    void roleDeletionBehavior() throws Exception {
        var r1 = Map.of(
                "fio", "User One",
                "phoneNumber", "+70000000001",
                "avatar", "https://example.com/1.png",
                "role", "R1"
        );
        var res1 = mvc.perform(post("/api/createNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(r1)))
                .andExpect(status().isCreated())
                .andReturn();
        String id1 = objectMapper.readTree(res1.getResponse().getContentAsString()).get("userID").asText();

        var r2 = Map.of(
                "fio", "User Two",
                "phoneNumber", "+70000000002",
                "avatar", "https://example.com/2.png",
                "role", "R1"
        );
        var res2 = mvc.perform(post("/api/createNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(r2)))
                .andExpect(status().isCreated())
                .andReturn();
        String id2 = objectMapper.readTree(res2.getResponse().getContentAsString()).get("userID").asText();

        mvc.perform(delete("/api/users").param("userID", id1))
                .andExpect(status().isNoContent());
        assertThat(roleRepository.findByRoleNameIgnoreCase("R1")).isPresent();

        mvc.perform(delete("/api/users").param("userID", id2))
                .andExpect(status().isNoContent());
        assertThat(roleRepository.findByRoleNameIgnoreCase("R1")).isNotPresent();
    }

    @Test
    @DisplayName("Validation errors return 400 with details via ControllerAdvice")
    void validationErrors() throws Exception {
        var bad = Map.of(
                "fio", "Bad User",
                "phoneNumber", "123", // too short, not matching pattern
                "avatar", "not-a-url",
                "role", "X"
        );
        mvc.perform(post("/api/createNewUser")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
