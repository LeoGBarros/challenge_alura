package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private TaskService taskService;
    @MockBean
    private TaskRepository taskRepository;
    @MockBean
    private CourseRepository courseRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void should_return_bad_request_when_statement_is_too_short() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("abc"); // too short
        dto.setOrder(1);
        dto.setCourseId(1L);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("statement"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void should_return_bad_request_when_order_is_negative() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("Valid statement");
        dto.setOrder(-1); // invalid order
        dto.setCourseId(1L);

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].field").value("order"))
                .andExpect(jsonPath("$[0].message").isNotEmpty());
    }

    @Test
    void should_return_bad_request_when_course_status_is_not_building() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("Valid statement");
        dto.setOrder(1);
        dto.setCourseId(99L);

        doThrow(new IllegalStateException("Curso não está em modo BUILDING"))
                .when(taskService).createOpenTextTask(any());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Curso não está em modo BUILDING"));
    }

    @Test
    void should_return_bad_request_when_statement_is_duplicate() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("Duplicated statement");
        dto.setOrder(1);
        dto.setCourseId(1L);

        doThrow(new IllegalArgumentException("Enunciado já existe para este curso"))
                .when(taskService).createOpenTextTask(any());

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enunciado já existe para este curso"));
    }
}
