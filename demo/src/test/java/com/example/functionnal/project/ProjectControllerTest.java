package com.example.functionnal.project;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.DemoApplication;
import com.example.demo.dto.ProjectRequest;
import com.example.demo.model.Project;
import com.example.demo.model.User;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.TaskHistoryRepository;
import com.example.demo.repository.TaskRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = DemoApplication.class)
@AutoConfigureMockMvc
public class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired
    private TaskHistoryRepository taskHistoryRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDb() {
        // 💥 Supprimer dans l’ordre inverse des dépendances
        taskHistoryRepository.deleteAll(); // dépend de Task
        taskRepository.deleteAll();        // dépend de Project et User
        projectRepository.deleteAll();     // dépend de User
        userRepository.deleteAll();        // indépendant (en dernier)
    }

    @Test
    void createProject_shouldReturnProject() throws Exception {
        // Créons un admin existant
        User admin = new User("admin1", "admin@email.com", "1234");
        userRepository.save(admin);

        ProjectRequest request = new ProjectRequest();
        request.setName("Test Project");
        request.setDescription("Un projet de test");
        request.setStartDate("2025-03-24");
        request.setAdminId(admin.getId());

        mockMvc.perform(post("/api/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Project"))
                .andExpect(jsonPath("$.description").value("Un projet de test"))
                .andExpect(jsonPath("$.admin.id").value(admin.getId()));
    }

    @Test
    void getAllProjects_shouldReturnList() throws Exception {
        mockMvc.perform(get("/api/projects"))
            .andExpect(status().isOk());
    }

    @Test
    void getProjectById_shouldReturnProject_whenExists() throws Exception {
        User admin = new User("sidi", "sidi@gmail.com", "123"); 
        admin = userRepository.save(admin) ; 
        Project project = new Project("Nom", "Desc", LocalDate.now(), admin);
        project = projectRepository.save(project);

        mockMvc.perform(get("/api/projects/" + project.getId()))
            .andExpect(status().isOk());
    }

    @Test
    void getProjectById_shouldReturn404_whenNotExists() throws Exception {
        mockMvc.perform(get("/api/projects/999999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteProject_shouldReturn204_whenDeleted() throws Exception {
        User admin = new User("sidi", "sidi@gmail.com", "123"); 
        admin = userRepository.save(admin) ; 
        Project p = projectRepository.save(new Project("X", "Y", LocalDate.now(), admin));

        mockMvc.perform(delete("/api/projects/" + p.getId()))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteProject_shouldReturn404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/api/projects/12345"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getProjectsByUser_shouldReturnList() throws Exception {
        User admin = new User("sidi", "sidi@gmail.com", "123"); 
        admin = userRepository.save(admin) ;
        mockMvc.perform(get("/api/projects/user/" + admin.getId()))
            .andExpect(status().isOk());
    }




}
