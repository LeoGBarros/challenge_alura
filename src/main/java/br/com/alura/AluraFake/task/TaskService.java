package br.com.alura.AluraFake.task;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final CourseRepository courseRepository;

    public TaskService(TaskRepository taskRepository, CourseRepository courseRepository) {
        this.taskRepository = taskRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void createOpenTextTask(NewOpenTextTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (!course.getStatus().equals(br.com.alura.AluraFake.course.Status.BUILDING)) {
            throw new IllegalStateException("Curso não está em modo BUILDING");
        }

        if (taskRepository.existsByCourseAndStatement(course, dto.getStatement())) {
            throw new IllegalArgumentException("Enunciado já existe para este curso");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);

        if (dto.getOrder() > tasks.size() + 1) {
            throw new IllegalArgumentException("A ordem é inválida (quebra sequência)");
        }

        // Desloca ordens se necessário
        for (Task task : tasks) {
            if (task.getOrderIndex() >= dto.getOrder()) {
                task.setOrderIndex(task.getOrderIndex() + 1);
            }
        }

        taskRepository.saveAll(tasks); // atualiza ordens deslocadas

        Task newTask = new OpenTextTask(dto.getStatement(), dto.getOrder(), course);
        taskRepository.save(newTask);
    }
}
