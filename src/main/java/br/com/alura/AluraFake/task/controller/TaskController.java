package br.com.alura.AluraFake.task.controller;

import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/new/opentext")
    public ResponseEntity<Map<String, Object>> createOpenTextTask(@RequestBody @Valid NewOpenTextTaskDTO dto) {
        Task task = taskService.createOpenTextTask(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", task.getId(),
                        "statement", task.getStatement(),
                        "order", task.getOrderIndex(),
                        "message", "Task successfully created!"
                ));
    }


    @PostMapping("/new/multiplechoice")
    public ResponseEntity<Void> createMultipleChoiceTask() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<?> createSingleChoiceTask(@RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        Task task = taskService.createSingleChoiceTask(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", task.getId(),
                        "statement", task.getStatement(),
                        "order", task.getOrderIndex(),
                        "message", "Task successfully created!"
                ));
    }
}
