package vn.Backend.TTCS_PTIT.controller;

import org.springframework.http.MediaType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import vn.Backend.TTCS_PTIT.Entity.ApiResponse;
import vn.Backend.TTCS_PTIT.Entity.User;
import vn.Backend.TTCS_PTIT.Repository.UserRepository;

@IntegrationTest // define annotation
@AutoConfigureMockMvc
@Transactional // spring-framework
public class UserControllerIT {
        @Autowired
        private MockMvc mockMvc;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach // luon luon chay truoc vi neu dung after co the bi fail => after ko chay den
        public void init() {

        }

        @Test
        public void createUser_shouldReturnUser_whenValid() throws Exception {
                User inputUser = new User(null, "anhDuckIT", "anhDuckIT@gmail.com");
                String resultString = this.mockMvc.perform(post("/users").contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsBytes(inputUser))).andExpect(status().isCreated())
                                .andReturn()
                                .getResponse().getContentAsString();
                User outputUser = this.objectMapper.readValue(resultString, User.class);
                assertEquals("anhDuckIT", outputUser.getName());
        }

        @Test
        public void getUsers_shouldReturnUsers() throws Exception {
                User user1 = new User(null, "name1", "name1@gmail.com");
                User user2 = new User(null, "name2", "name2@gmail.com");
                List<User> users = List.of(user1, user2);
                this.userRepository.saveAll(users);
                String resultString = this.mockMvc.perform(get("/users")).andExpect(status().isOk()).andReturn()
                                .getResponse()
                                .getContentAsString();
                List<User> result = this.objectMapper.readValue(resultString, new TypeReference<List<User>>() {
                });
                assertEquals(2, result.size());
                assertEquals("name1", result.get(0).getName());
        }

        @Test
        public void getUserById_shouldReturnUser_whenUserIdValid() throws Exception {
                User user = new User(null, "duckuro", "duckuro@gmail.com");
                User inputUser = this.userRepository.saveAndFlush(user);
                String resultString = this.mockMvc.perform(get("/users/{id}",
                                inputUser.getId())).andExpect(status().isOk())
                                .andReturn().getResponse().getContentAsString();
                User outputUser = this.objectMapper.readValue(resultString, User.class);
                assertEquals("duckuro", outputUser.getName());
                assertEquals("duckuro@gmail.com", outputUser.getEmail());
        }

        @Test
        public void getUserById_shouldReturnError_whenIdNotFound() throws Exception {
                // arrange
                Long nonExistentId = 0L;
                // action
                String resultString = this.mockMvc.perform(get("/users/{id}", nonExistentId))
                                .andExpect(status().isNotFound())
                                .andReturn().getResponse().getContentAsString();
                // assert
                ApiResponse<Object> response = objectMapper.readValue(resultString,
                                new TypeReference<ApiResponse<Object>>() {
                                });
                assertEquals("error", response.getMessage(), "Status phải là 'error'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNull(response.getData(), "Data phải là null khi lỗi");
                assertEquals("USER_NOT_FOUND", response.getErrorCode(), "ErrorCode không đúng");
                assertNotNull(response.getTimestamp(), "Timestamp không được null");
        }

        @Test
        public void updateUser_shouldReturnUser_whenUserExist() throws Exception {
                User user = new User(1L, "duckkk", "duckkk@gmail.com");
                User userInput = this.userRepository.saveAndFlush(user);
                User updateUser = new User(userInput.getId(), "new-duckkk",
                                "new-duckkkk@gmail.com");
                String resultString = this.mockMvc
                                .perform(put("/users/{id}",
                                                userInput.getId()).contentType(MediaType.APPLICATION_JSON)
                                                .content(objectMapper.writeValueAsBytes(updateUser)))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
                ApiResponse<User> response = objectMapper.readValue(resultString,
                                new TypeReference<ApiResponse<User>>() {
                                });
                assertEquals("success", response.getStatus(), "Status phải là 'success'");
                assertNotNull(response.getMessage(), "Message không được null");
                assertNotNull(response.getData(), "Data không được null");
                assertTrue(response.getData() instanceof User,
                                "Data phải là User, nhưng nhận được: " + response.getData().getClass().getSimpleName());
                assertEquals("new-name", response.getData().getName(), "Tên user không khớp");
                assertEquals("new@gmail.com", response.getData().getEmail(), "Email user không khớp");
                assertNull(response.getErrorCode(), "ErrorCode phải là null khi thành công");
                assertNotNull(response.getTimestamp(), "Timestamp không được null");
        }

        @Test
        public void deleteUserById_shouldReturnVoid_whenUserExists() throws Exception {
                User user = new User(1L, "delete", "delete@gmail.com");
                User inputUser = this.userRepository.saveAndFlush(user);
                String resultString = this.mockMvc.perform(delete("/users/{id}",
                                inputUser.getId()).contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
                if (!resultString.isEmpty()) { // Nếu API trả về body
                        ApiResponse<Object> response = objectMapper.readValue(resultString,
                                        new TypeReference<ApiResponse<Object>>() {
                                        });

                        assertEquals("success", response.getStatus(), "Status phải là 'success'");
                        assertNotNull(response.getMessage(), "Message không được null");
                        assertNull(response.getData(), "Data phải là null khi xóa");
                        assertNull(response.getErrorCode(), "ErrorCode phải là null khi thành công");
                        assertNotNull(response.getTimestamp(), "Timestamp không được null");
                }
                Long countDB = this.userRepository.count();
                assertEquals(0, countDB, "Số lượng user trong DB phải là 0 sau khi xóa");
        }

}
