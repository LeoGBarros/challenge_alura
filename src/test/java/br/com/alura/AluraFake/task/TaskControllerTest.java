package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceOptionDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
        dto.setOrder(-1);
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

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new IllegalStateException("Curso não está em modo BUILDING"));

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

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new IllegalArgumentException("Enunciado já existe para este curso"));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Enunciado já existe para este curso"));
    }

    @Test
    void should_return_bad_request_when_singlechoice_has_too_few_options() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Valid question?");
        dto.setOrder(1);
        dto.setOptions(List.of(new SingleChoiceOptionDTO("Only one", true)));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_no_correct_option() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Which language?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", false),
                new SingleChoiceOptionDTO("Python", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("Deve haver exatamente UMA opção correta"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Deve haver exatamente UMA opção correta"));
    }

    @Test
    void should_return_bad_request_when_duplicate_options() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Pick one");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Java", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("As alternativas devem ser únicas entre si"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("As alternativas devem ser únicas entre si"));
    }

    @Test
    void should_return_bad_request_when_option_equals_statement() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Java");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Python", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("A alternativa não pode ser igual ao enunciado"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("A alternativa não pode ser igual ao enunciado"));
    }
}
