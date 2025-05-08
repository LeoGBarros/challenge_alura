package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.Course;
import br.com.alura.AluraFake.course.CourseRepository;
import br.com.alura.AluraFake.course.Status;
import br.com.alura.AluraFake.task.dto.NewOpenTextTaskDTO;
import br.com.alura.AluraFake.task.dto.NewSingleChoiceTaskDTO;
import br.com.alura.AluraFake.task.dto.SingleChoiceOptionDTO;
import br.com.alura.AluraFake.task.model.OpenTextTask;
import br.com.alura.AluraFake.task.model.SingleChoiceOption;
import br.com.alura.AluraFake.task.model.SingleChoiceTask;
import br.com.alura.AluraFake.task.model.Task;
import br.com.alura.AluraFake.task.repository.TaskRepository;
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

        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Curso não está em modo BUILDING");
        }

        if (taskRepository.existsByCourseAndStatement(course, dto.getStatement())) {
            throw new IllegalArgumentException("Enunciado já existe para este curso");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);

        if (dto.getOrder() > tasks.size() + 1) {
            throw new IllegalArgumentException("A ordem é inválida (quebra sequência)");
        }

        for (Task task : tasks) {
            if (task.getOrderIndex() >= dto.getOrder()) {
                task.setOrderIndex(task.getOrderIndex() + 1);
            }
        }

        taskRepository.saveAll(tasks);

        Task newTask = new OpenTextTask(dto.getStatement(), dto.getOrder(), course);
        taskRepository.save(newTask);
    }

    @Transactional
    public void createSingleChoiceTask(NewSingleChoiceTaskDTO dto) {
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));

        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Curso não está em modo BUILDING");
        }

        if (taskRepository.existsByCourseAndStatement(course, dto.getStatement())) {
            throw new IllegalArgumentException("Enunciado já existe para este curso");
        }

        List<String> optionTexts = dto.getOptions().stream()
                .map(SingleChoiceOptionDTO::getOption)
                .toList();

        if (dto.getOptions().stream().filter(SingleChoiceOptionDTO::isCorrect).count() != 1) {
            throw new IllegalArgumentException("Deve haver exatamente UMA opção correta");
        }

        if (optionTexts.stream().distinct().count() != optionTexts.size()) {
            throw new IllegalArgumentException("As opções devem ser únicas entre si");
        }

        if (optionTexts.stream().anyMatch(opt -> opt.equalsIgnoreCase(dto.getStatement()))) {
            throw new IllegalArgumentException("Opção não pode ser igual ao enunciado");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);

        if (dto.getOrder() > tasks.size() + 1) {
            throw new IllegalArgumentException("A ordem é inválida (quebra sequência)");
        }

        for (Task task : tasks) {
            if (task.getOrderIndex() >= dto.getOrder()) {
                task.setOrderIndex(task.getOrderIndex() + 1);
            }
        }

        taskRepository.saveAll(tasks);

        List<SingleChoiceOption> options = dto.getOptions().stream()
                .map(opt -> new SingleChoiceOption(opt.getOption(), opt.isCorrect()))
                .toList();

        Task newTask = new SingleChoiceTask(dto.getStatement(), dto.getOrder(), course, options);
        taskRepository.save(newTask);
    }
}
