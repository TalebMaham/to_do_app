package com.example.functionnal.task;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.example.demo.DemoApplication;
import com.example.demo.dto.TaskRequest;
import com.example.demo.model.Project;
import com.example.demo.model.TaskPriority;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserRepository userRepository;
    @Autowired private ProjectRepository projectRepository;
    @Autowired private TaskRepository taskRepository;
    @Autowired private TaskHistoryRepository taskHistoryRepository;
    

    private User admin;
    private Project project;

    @BeforeEach
    void setup() {
        taskHistoryRepository.deleteAll();
        taskRepository.deleteAll();
        projectRepository.deleteAll();
        userRepository.deleteAll();

        admin = userRepository.save(new User("admin", "admin@mail.com", "pwd"));
        project = projectRepository.save(new Project("Project", "desc", LocalDate.now(), admin));
    }

    @Test
    void createTask_shouldReturnCreatedTask() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setUserId(admin.getId());
        request.setName("Nouvelle tâche");
        request.setDescription("Une tâche cool");
        request.setDueDate(LocalDate.now().plusDays(3));
        request.setPriority(TaskPriority.MEDIUM);

        mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nouvelle tâche"));
    }

    @Test
    void getTasks_shouldReturnEmptyInitially() throws Exception {
        mockMvc.perform(get("/api/projects/" + project.getId() + "/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void getTaskById_shouldReturnTask_whenExists() throws Exception {
        // Crée une tâche
        TaskRequest request = new TaskRequest();
        request.setUserId(admin.getId());
        request.setName("Tâche récupérable");
        request.setDescription("À tester");
        request.setDueDate(LocalDate.now().plusDays(1));
        request.setPriority(TaskPriority.LOW);

        String response = mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/projects/" + project.getId() + "/tasks/" + taskId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Tâche récupérable"));
    }

    @Test
    void getTaskById_shouldReturn404_whenTaskDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/projects/" + project.getId() + "/tasks/999999"))
            .andExpect(status().isNotFound());
    }

    // @Test
    // void assignTask_shouldReturnUpdatedTask() throws Exception {
    //     // Crée une tâche
    //     TaskRequest request = new TaskRequest();
    //     request.setUserId(admin.getId());
    //     request.setName("À assigner");
    //     request.setDescription("Avec assigné");
    //     request.setDueDate(LocalDate.now().plusDays(2));
    //     request.setPriority(TaskPriority.HIGH);

    //     String response = mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks")
    //             .contentType(MediaType.APPLICATION_JSON)
    //             .content(objectMapper.writeValueAsString(request)))
    //             .andReturn().getResponse().getContentAsString();

    //     Long taskId = objectMapper.readTree(response).get("id").asLong();

    //     // Assigne la tâche à l’admin lui-même
    //     mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks/" + taskId + "/assign")
    //             .param("assigneeId", String.valueOf(admin.getId())))
    //             .andExpect(status().isOk())
    //             .andExpect(jsonPath("$.assignee.id").value(admin.getId()));
    // }

    @Test
    void updateTask_shouldModifyField() throws Exception {
        // Crée une tâche
        TaskRequest request = new TaskRequest();
        request.setUserId(admin.getId());
        request.setName("À modifier");
        request.setDescription("Ancienne desc");
        request.setDueDate(LocalDate.now().plusDays(2));
        request.setPriority(TaskPriority.LOW);

        String response = mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        // Effectue la modification
        String patchBody = """
            {
                "description": "Nouvelle description"
            }
            """;

        mockMvc.perform(patch("/api/projects/" + project.getId() + "/tasks/" + taskId)
                .param("userId", String.valueOf(admin.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value("Nouvelle description"));
    }


    @Test
    void getTaskHistory_shouldReturnEmptyListInitially() throws Exception {
        // Crée une tâche
        TaskRequest request = new TaskRequest();
        request.setUserId(admin.getId());
        request.setName("Tâche historique");
        request.setDescription("desc");
        request.setDueDate(LocalDate.now().plusDays(2));
        request.setPriority(TaskPriority.MEDIUM);

        String response = mockMvc.perform(post("/api/projects/" + project.getId() + "/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andReturn().getResponse().getContentAsString();

        Long taskId = objectMapper.readTree(response).get("id").asLong();

        mockMvc.perform(get("/api/projects/" + project.getId() + "/tasks/" + taskId + "/history"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }





}
