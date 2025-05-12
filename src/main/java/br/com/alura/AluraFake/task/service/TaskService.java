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
    public Task createOpenTextTask(NewOpenTextTaskDTO dto) {
        Course course = findAndValidateCourse(dto.getCourseId());
        validateStatementUniqueness(course, dto.getStatement());

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);
        validateOrder(dto.getOrder(), tasks);

        shiftTaskOrder(tasks, dto.getOrder());
        taskRepository.saveAll(tasks);

        Task newTask = new OpenTextTask(dto.getStatement(), dto.getOrder(), course);
        return taskRepository.save(newTask);
    }

    @Transactional
    public Task createSingleChoiceTask(NewSingleChoiceTaskDTO dto) {
        Course course = findAndValidateCourse(dto.getCourseId());
        validateStatementUniqueness(course, dto.getStatement());

        List<SingleChoiceOptionDTO> options = dto.getOptions();
        validateOptions(dto.getStatement(), options);

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);
        validateOrder(dto.getOrder(), tasks);

        shiftTaskOrder(tasks, dto.getOrder());
        taskRepository.saveAll(tasks);

        List<SingleChoiceOption> mappedOptions = options.stream()
                .map(opt -> new SingleChoiceOption(opt.getOption(), opt.getIsCorrect()))
                .toList();

        Task newTask = new SingleChoiceTask(dto.getStatement(), dto.getOrder(), course, mappedOptions);
        return taskRepository.save(newTask);
    }

    private Course findAndValidateCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        if (!course.getStatus().equals(Status.BUILDING)) {
            throw new IllegalStateException("Course is not in BUILDING status");
        }
        return course;
    }

    private void validateStatementUniqueness(Course course, String statement) {
        if (taskRepository.existsByCourseAndStatement(course, statement)) {
            throw new IllegalArgumentException("Statement already exists for this course");
        }
    }

    private void validateOrder(int newOrder, List<Task> existingTasks) {
        if (newOrder > existingTasks.size() + 1) {
            throw new IllegalArgumentException("Invalid order (breaks sequence)");
        }
    }

    private void shiftTaskOrder(List<Task> tasks, int newOrder) {
        for (Task task : tasks) {
            if (task.getOrderIndex() >= newOrder) {
                task.setOrderIndex(task.getOrderIndex() + 1);
            }
        }
    }

    private void validateOptions(String statement, List<SingleChoiceOptionDTO> options) {
        if (options == null || options.size() < 2 || options.size() > 5) {
            throw new IllegalArgumentException("The activity must have between 2 and 5 options");
        }

        long correctCount = options.stream().filter(SingleChoiceOptionDTO::getIsCorrect).count();
        if (correctCount != 1) {
            throw new IllegalArgumentException("There must be exactly ONE correct option");
        }

        Set<String> uniqueTexts = new HashSet<>();
        String normalizedStatement = statement.trim().toLowerCase();

        for (SingleChoiceOptionDTO opt : options) {
            String text = opt.getOption().trim();

            if (text.length() < 4 || text.length() > 80) {
                throw new IllegalArgumentException("Each option must be between 4 and 80 characters");
            }

            if (!uniqueTexts.add(text.toLowerCase())) {
                throw new IllegalArgumentException("Options must be unique");
            }

            if (text.equalsIgnoreCase(normalizedStatement)) {
                throw new IllegalArgumentException("Option text must not match the statement");
            }
        }
    }
}
