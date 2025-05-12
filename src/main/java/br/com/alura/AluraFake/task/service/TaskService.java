package br.com.alura.AluraFake.task.service;

import br.com.alura.AluraFake.course.*;
import br.com.alura.AluraFake.task.dto.*;
import br.com.alura.AluraFake.task.model.*;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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
        Course course = findAndValidateCourse(dto.getCourseId());
        validateStatementUniqueness(course, dto.getStatement());

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);
        validateOrder(dto.getOrder(), tasks);

        shiftTasksOrder(tasks, dto.getOrder());
        taskRepository.saveAll(tasks);

        Task newTask = new OpenTextTask(dto.getStatement(), dto.getOrder(), course);
        taskRepository.save(newTask);
    }

    @Transactional
    public Task createSingleChoiceTask(NewSingleChoiceTaskDTO dto) {
        Course course = findAndValidateCourse(dto.getCourseId());
        validateStatementUniqueness(course, dto.getStatement());

        List<SingleChoiceOptionDTO> options = dto.getOptions();
        validateOptions(dto.getStatement(), options);

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);
        validateOrder(dto.getOrder(), tasks);

        shiftTasksOrder(tasks, dto.getOrder());
        taskRepository.saveAll(tasks);

        List<SingleChoiceOption> mappedOptions = options.stream()
                .map(opt -> new SingleChoiceOption(opt.getOption(), opt.getIsCorrect()))
                .toList();

        Task newTask = new SingleChoiceTask(dto.getStatement(), dto.getOrder(), course, mappedOptions);
        return taskRepository.save(newTask);
    }


    private Course findAndValidateCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Curso não encontrado"));
        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Curso não está em modo BUILDING");
        }
        return course;
    }

    private void validateStatementUniqueness(Course course, String statement) {
        if (taskRepository.existsByCourseAndStatement(course, statement)) {
            throw new IllegalArgumentException("Enunciado já existe para este curso");
        }
    }

    private void validateOrder(int newOrder, List<Task> existingTasks) {
        if (newOrder > existingTasks.size() + 1) {
            throw new IllegalArgumentException("A ordem é inválida (quebra sequência)");
        }
    }

    private void shiftTasksOrder(List<Task> tasks, int newOrder) {
        for (Task task : tasks) {
            if (task.getOrderIndex() >= newOrder) {
                task.setOrderIndex(task.getOrderIndex() + 1);
            }
        }
    }

    private void validateOptions(String statement, List<SingleChoiceOptionDTO> options) {
        if (options == null || options.size() < 2 || options.size() > 5) {
            throw new IllegalArgumentException("A atividade deve ter entre 2 e 5 alternativas");
        }

        long correctCount = options.stream().filter(SingleChoiceOptionDTO::getIsCorrect).count();
        if (correctCount != 1) {
            throw new IllegalArgumentException("Deve haver exatamente UMA opção correta");
        }

        Set<String> uniqueTexts = new HashSet<>();
        String normalizedStatement = statement.trim().toLowerCase();

        for (SingleChoiceOptionDTO opt : options) {
            String text = opt.getOption().trim();

            if (text.length() < 4 || text.length() > 80) {
                throw new IllegalArgumentException("Cada alternativa deve ter entre 4 e 80 caracteres");
            }

            if (!uniqueTexts.add(text.toLowerCase())) {
                throw new IllegalArgumentException("As alternativas devem ser únicas entre si");
            }

            if (text.equalsIgnoreCase(normalizedStatement)) {
                throw new IllegalArgumentException("A alternativa não pode ser igual ao enunciado");
            }
        }
    }
}
