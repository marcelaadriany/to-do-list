package com.marcela.todo.repository;

import com.marcela.todo.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

  @Autowired
  private TaskRepository taskRepository;

  @Test
  @DisplayName("Deve salvar uma task no banco")
  void saveTask_success() {
    Task task = new Task();
    task.setTitle("Repository test");
    task.setDescription("Testando salvar task");

    Task saved = taskRepository.save(task);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getTitle()).isEqualTo(task.getTitle());
  }

  @Test
  @DisplayName("Deve retornar lista de tasks")
  void findAllTasks_success() {
    Task task1 = new Task();
    task1.setTitle("Task 1");
    taskRepository.save(task1);

    Task task2 = new Task();
    task2.setTitle("Task 2");
    taskRepository.save(task2);

    List<Task> tasks = taskRepository.findAll();

    assertThat(tasks).hasSize(2);
  }

  @Test
  @DisplayName("Deve buscar task por ID")
  void findById_success() {
    Task task = new Task();
    task.setTitle("Buscar por ID");
    task = taskRepository.save(task);

    Optional<Task> found = taskRepository.findById(task.getId());

    assertThat(found).isPresent();
    assertThat(found.get().getTitle()).isEqualTo("Buscar por ID");
  }

  @Test
  @DisplayName("Deve deletar task por ID")
  void deleteById_success() {
    Task task = new Task();
    task.setTitle("Task a deletar");
    task = taskRepository.save(task);

    taskRepository.deleteById(task.getId());

    Optional<Task> deleted = taskRepository.findById(task.getId());
    assertThat(deleted).isEmpty();
  }
}
