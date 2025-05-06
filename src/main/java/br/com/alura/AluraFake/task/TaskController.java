package br.com.alura.AluraFake.task;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/new/singlechoice")
    public ResponseEntity<Void> newSingleChoice() {
        return ResponseEntity.ok().build();
    }

    @PostMapping("/new/multiplechoice")
    public ResponseEntity<Void> newMultipleChoice() {
        return ResponseEntity.ok().build();
    }
}
