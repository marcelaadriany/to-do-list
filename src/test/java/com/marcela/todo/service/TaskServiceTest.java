package com.marcela.todo.service;

import com.marcela.todo.model.Task;
import com.marcela.todo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

  @Mock
  private TaskRepository taskRepository;

  @InjectMocks
  private TaskService taskService;

  private Task task;

  @BeforeEach
  void setup() {
    task = new Task();
    task.setId(1L);
    task.setTitle("Estudar Spring");
    task.setDescription("Revisar testes unitários");
  }

  // ------------------- CREATE -------------------

  @Test
  @DisplayName("createTask - deve criar uma task com sucesso")
  void createTask_success() {
    // Arrange
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    // Act
    Task result = taskService.createTask(task);

    // Assert
    assertNotNull(result);
    assertEquals("Estudar Spring", result.getTitle());
    verify(taskRepository, times(1)).save(task);
  }

  // ------------------- READ (GET ALL / BY ID) -------------------

  @Test
  @DisplayName("getAllTasks - deve retornar todas as tasks")
  void getAllTasks_success() {
    // Arrange
    when(taskRepository.findAll()).thenReturn(List.of(task));

    // Act
    List<Task> result = taskService.getAllTasks();

    // Assert
    assertEquals(1, result.size());
    verify(taskRepository).findAll();
  }

  @Test
  @DisplayName("getTaskById - deve retornar task quando existir")
  void getTaskById_found() {
    // Arrange
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

    // Act
    Optional<Task> result = taskService.getTaskById(1L);

    // Assert
    assertTrue(result.isPresent());
    assertEquals("Estudar Spring", result.get().getTitle());
    verify(taskRepository).findById(1L);
  }

  @Test
  @DisplayName("getTaskById - deve retornar Optional.empty() quando não existir")
  void getTaskById_notFound() {
    // Arrange
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<Task> result = taskService.getTaskById(1L);

    // Assert
    assertTrue(result.isEmpty());
    verify(taskRepository).findById(1L);
  }

  // ------------------- UPDATE -------------------

  @Test
  @DisplayName("updateTask - deve atualizar e retornar a task")
  void updateTask_success() {
    // Arrange
    task.setTitle("Atualizado");
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    // Act
    Task updated = taskService.updateTask(task);

    // Assert
    assertEquals("Atualizado", updated.getTitle());
    verify(taskRepository).save(task);
  }

  // ------------------- MARK AS DONE -------------------

  @Test
  @DisplayName("markTaskAsDone - deve marcar como concluída quando existir")
  void markTaskAsDone_found() {
    // Arrange
    when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
    when(taskRepository.save(any(Task.class))).thenReturn(task);

    // Act
    Optional<Task> result = taskService.markTaskAsDone(1L);

    // Assert
    assertTrue(result.isPresent());
    assertTrue(result.get().isDone());
    assertNotNull(result.get().getCompletedAt());
    verify(taskRepository).findById(1L);
    verify(taskRepository).save(task);
  }

  @Test
  @DisplayName("markTaskAsDone - deve retornar Optional.empty() se ID não existir")
  void markTaskAsDone_notFound() {
    // Arrange
    when(taskRepository.findById(1L)).thenReturn(Optional.empty());

    // Act
    Optional<Task> result = taskService.markTaskAsDone(1L);

    // Assert
    assertTrue(result.isEmpty());
    verify(taskRepository).findById(1L);
    verify(taskRepository, never()).save(any(Task.class));
  }

  // ------------------- DELETE -------------------

  @Test
  @DisplayName("deleteTask - deve deletar e retornar true se task existir")
  void deleteTask_exists() {
    // Arrange
    when(taskRepository.existsById(1L)).thenReturn(true);

    // Act
    boolean result = taskService.deleteTask(1L);

    // Assert
    assertTrue(result);
    verify(taskRepository).deleteById(1L);
  }

  @Test
  @DisplayName("deleteTask - deve retornar false se task não existir")
  void deleteTask_notExists() {
    // Arrange
    when(taskRepository.existsById(1L)).thenReturn(false);

    // Act
    boolean result = taskService.deleteTask(1L);

    // Assert
    assertFalse(result);
    verify(taskRepository, never()).deleteById(any());
  }
}
