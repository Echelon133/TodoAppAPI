package ml.echelon133.Model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import ml.echelon133.Model.Serializer.TodoListSerializer;

import javax.persistence.*;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@JsonSerialize(using = TodoListSerializer.class)
public class TodoList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Date dateCreated;
    private String name;

    @OrderBy("id")
    @OneToMany(mappedBy = "taskOwner", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
    private Set<Task> tasks;

    @ManyToOne
    @JoinColumn(name="list_owner")
    private User listOwner;

    public TodoList() {
        setDateCreated(new Date());
        setTasks(new LinkedHashSet<>());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Task> getTasks() {
        return tasks;
    }

    public void setTasks(Set<Task> tasks) {
        for (Task task : tasks) {
            task.setTaskOwner(this);
        }
        this.tasks = tasks;
    }

    public void addTask(Task task) {
        task.setTaskOwner(this);
        this.tasks.add(task);
    }

    public User getListOwner() {
        return listOwner;
    }

    public void setListOwner(User listOwner) {
        this.listOwner = listOwner;
    }
}
