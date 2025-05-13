package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.task.controller.TaskController;
import br.com.alura.AluraFake.task.dto.NewMultipleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceOptionDTO;
import br.com.alura.AluraFake.task.model.MultipleChoiceTask;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import br.com.alura.AluraFake.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
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
        dto.setStatement("abc");
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
    void should_return_bad_request_when_course_is_not_in_building_status() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("Valid statement");
        dto.setOrder(1);
        dto.setCourseId(99L);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new IllegalStateException("Course is not in BUILDING status"));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Course is not in BUILDING status"));
    }

    @Test
    void should_return_bad_request_when_statement_is_duplicate() throws Exception {
        NewOpenTextTaskDTO dto = new NewOpenTextTaskDTO();
        dto.setStatement("Duplicated statement");
        dto.setOrder(1);
        dto.setCourseId(1L);

        when(taskService.createOpenTextTask(any()))
                .thenThrow(new IllegalArgumentException("Statement already exists for this course"));

        mockMvc.perform(post("/task/new/opentext")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Statement already exists for this course"));
    }

    @Test
    void should_return_bad_request_when_singlechoice_has_less_than_2_options() throws Exception {
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
    void should_return_bad_request_when_no_correct_option_provided() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Which language?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", false),
                new SingleChoiceOptionDTO("Python", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("There must be exactly ONE correct option"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("There must be exactly ONE correct option"));
    }

    @Test
    void should_return_bad_request_when_options_are_not_unique() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Pick one");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Java", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("Options must be unique"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Options must be unique"));
    }

    @Test
    void should_return_bad_request_when_option_matches_statement() throws Exception {
        NewSingleChoiceTaskDTO dto = new NewSingleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Java");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Python", false)
        ));

        when(taskService.createSingleChoiceTask(any()))
                .thenThrow(new IllegalArgumentException("Option text must not match the statement"));

        mockMvc.perform(post("/task/new/singlechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Option text must not match the statement"));
    }

    @Test
    void should_return_bad_request_when_multiplechoice_has_less_than_3_options() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("What did we learn?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Ruby", false)
        ));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_multiplechoice_has_no_incorrect_option() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("Choose topics:");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Spring", true),
                new SingleChoiceOptionDTO("JPA", true)
        ));

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$[0].message").value("The option 'JPA' each option must be between 4 and 80 characters."));
    }


    @Test
    void should_return_created_when_multiplechoice_is_valid() throws Exception {
        NewMultipleChoiceTaskDTO dto = new NewMultipleChoiceTaskDTO();
        dto.setCourseId(1L);
        dto.setStatement("What did we learn?");
        dto.setOrder(1);
        dto.setOptions(List.of(
                new SingleChoiceOptionDTO("Java", true),
                new SingleChoiceOptionDTO("Spring", true),
                new SingleChoiceOptionDTO("Ruby", false)
        ));

        Task mockTask = new MultipleChoiceTask(dto.getStatement(), dto.getOrder(), new Course(), List.of());
        ReflectionTestUtils.setField(mockTask, "id", 999L);

        when(taskService.createMultipleChoiceTask(any())).thenReturn(mockTask);

        mockMvc.perform(post("/task/new/multiplechoice")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(999L))
                .andExpect(jsonPath("$.statement").value("What did we learn?"))
                .andExpect(jsonPath("$.message").value("Multiple choice activity created successfully!"));
    }

}
