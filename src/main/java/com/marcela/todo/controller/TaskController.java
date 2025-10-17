package com.marcela.todo.controller;

import com.marcela.todo.model.Task;
import com.marcela.todo.service.TaskService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {
  // posso usar public sem final, mas se for private, tem que ser final
  // (estudar mais sobre)
  private final TaskService taskService;

  // Injeção via construtor
  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  // cria nova tarefa
  @PostMapping // transforma o metodo abaixo em um endpoint HTTP do tipo POST
  public ResponseEntity<Task> createTask(@RequestBody Task task) {
    Task savedTask = taskService.createTask(task);
    return ResponseEntity.status(201).body(savedTask);
  }

  // lista todas as tarefas
  @GetMapping
  public ResponseEntity<List<Task>> getAllTasks() {
    List<Task> tasks = taskService.getAllTasks();
    return ResponseEntity.ok(tasks);
  }

  // busca tarefa por ID
  @GetMapping("/{id}")
  public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
    Optional<Task> task = taskService.getTaskById(id);
    return task.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // atualiza tarefa
  @PutMapping("/{id}")
  public ResponseEntity<Task> updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
    Optional<Task> existingTask = taskService.getTaskById(id);

    if(existingTask.isEmpty()) {
      return  ResponseEntity.notFound().build();
    }

    Task task = existingTask.get();
    task.setTitle(updatedTask.getTitle());
    task.setDescription(updatedTask.getDescription());
    task.setDone(updatedTask.isDone());
    task.setCompletedAt(updatedTask.getCompletedAt());

    Task savedTask = taskService.updateTask(task);
    return  ResponseEntity.ok(savedTask);
  }

  // marca como concluída
  @PatchMapping("/{id}/done")
  public ResponseEntity<Task> maskAsDona (@PathVariable Long id) {
    Optional<Task> updatedTask = taskService.markTaskAsDone(id);
    return updatedTask.map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  // deletar tarefa
  @DeleteMapping("/{id}")
  public  ResponseEntity<Void> deleteTask(@PathVariable Long id) {
    boolean deleted = taskService.deleteTask(id);
    if (deleted) {
      return ResponseEntity.noContent().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
