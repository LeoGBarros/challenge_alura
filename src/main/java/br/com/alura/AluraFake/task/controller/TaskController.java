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
    public ResponseEntity<Void> newOpenTextExercise(@RequestBody @Valid NewOpenTextTaskDTO dto) {
        taskService.createOpenTextTask(dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new/singlechoice")
    public ResponseEntity<?> newSingleChoice(@RequestBody @Valid NewSingleChoiceTaskDTO dto) {
        Task task = taskService.createSingleChoiceTask(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "id", task.getId(),
                        "statement", task.getStatement(),
                        "order", task.getOrderIndex(),
                        "message", "Atividade criada com sucesso!"
                ));
    }


}

