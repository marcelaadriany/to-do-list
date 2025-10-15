package com.marcela.todo.service;

import com.marcela.todo.model.Task;
import com.marcela.todo.repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class TaskService {
  private final TaskRepository taskRepository;

  // injeção de dependência via construtor
  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  // cria uma nova task
  public Task createTask(Task task) {
    return taskRepository.save(task);
  }

  // lista todas as tasks
  public List<Task> getAllTasks() {
    return taskRepository.findAll();
  }

  // busca uma task pelo ID
  public Optional<Task> getTaskById(Long id) {
    return taskRepository.findById(id);
  }

  // atualiza uma task
  public Task updateTask(Task task) {
    return taskRepository.save(task);
  }

  // Marca task como concluída
  public Optional<Task> markTaskAsDone(Long id) {
    Optional<Task> optionalTask = taskRepository.findById(id);
    if (optionalTask.isPresent()) {
      Task task = optionalTask.get();
      task.setDone(true);
      task.setCompletedAt(LocalDateTime.now());
      taskRepository.save(task);
      return Optional.of(task);
    }
    return Optional.empty();
  }

  // deleta uma task
  public boolean deleteTask(Long id) {
    if (taskRepository.existsById(id)) {
      taskRepository.deleteById(id);
      return true;
    } else {
      return false;
    }
  }
}
