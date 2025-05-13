package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.task.model.*;
import br.com.alura.AluraFake.task.repository.TaskRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final TaskRepository taskRepository;

    public CourseService(CourseRepository courseRepository, TaskRepository taskRepository) {
        this.courseRepository = courseRepository;
        this.taskRepository = taskRepository;
    }

    @Transactional
    public Course publishCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));

        if (course.getStatus() != Status.BUILDING) {
            throw new IllegalStateException("Course must be in BUILDING status to be published");
        }

        List<Task> tasks = taskRepository.findByCourseOrderByOrderIndex(course);

        boolean hasOpen = tasks.stream().anyMatch(t -> t instanceof OpenTextTask);
        boolean hasSingle = tasks.stream().anyMatch(t -> t instanceof SingleChoiceTask);
        boolean hasMultiple = tasks.stream().anyMatch(t -> t instanceof MultipleChoiceTask);

        if (!(hasOpen && hasSingle && hasMultiple)) {
            throw new IllegalStateException("Course must have at least one task of each type");
        }

        List<Integer> orders = tasks.stream().map(Task::getOrderIndex).sorted().toList();
        for (int i = 0; i < orders.size(); i++) {
            if (orders.get(i) != i + 1) {
                throw new IllegalStateException("Task orders must be sequential starting from 1");
            }
        }

        course.setStatus(Status.PUBLISHED);
        course.setPublishedAt(LocalDateTime.now());

        return courseRepository.save(course);
    }
}
