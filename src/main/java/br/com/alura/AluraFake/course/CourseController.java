package br.com.alura.AluraFake.course;

import br.com.alura.AluraFake.user.*;
import br.com.alura.AluraFake.util.ErrorItemDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;

    @Autowired
    public CourseController(CourseRepository courseRepository,
                            UserRepository userRepository,
                            CourseService courseService) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
    }

    @Transactional
    @PostMapping("/new")
    public ResponseEntity<?> createCourse(@Valid @RequestBody NewCourseDTO newCourse) {
        Optional<User> possibleAuthor = userRepository
                .findByEmail(newCourse.getEmailInstructor())
                .filter(User::isInstructor);

        if (possibleAuthor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorItemDTO("emailInstructor", "Usuário não é um instrutor"));
        }

        Course course = new Course(newCourse.getTitle(), newCourse.getDescription(), possibleAuthor.get());
        courseRepository.save(course);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<CourseListItemDTO>> getAllCourses() {
        List<CourseListItemDTO> courses = courseRepository.findAll().stream()
                .map(CourseListItemDTO::new)
                .toList();
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publishCourse(@PathVariable("id") Long id) {
        Course publishedCourse = courseService.publishCourse(id);
        return ResponseEntity.ok(new CourseListItemDTO(publishedCourse));
    }
}
