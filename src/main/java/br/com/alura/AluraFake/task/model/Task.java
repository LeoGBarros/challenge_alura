package br.com.alura.AluraFake.task.model;

import br.com.alura.AluraFake.course.Course;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "task_type")
public abstract class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(min = 4, max = 255)
    private String statement;

    @NotNull
    @Column(name = "order_index")
    private Integer orderIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Deprecated
    protected Task() {
    }

    public Task(String statement, Integer orderIndex, Course course) {
        this.statement = statement;
        this.orderIndex = orderIndex;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public String getStatement() {
        return statement;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public Course getCourse() {
        return course;
    }

    public void setOrderIndex(Integer newOrder) {
        this.orderIndex = newOrder;
    }
}
