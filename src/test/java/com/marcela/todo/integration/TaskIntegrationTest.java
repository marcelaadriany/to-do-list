package com.marcela.todo.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcela.todo.model.Task;
import com.marcela.todo.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TaskIntegrationTest {

  @LocalServerPort
  private int port;

  @Autowired
  private TestRestTemplate restTemplate;

  @Autowired
  private TaskRepository taskRepository;

  @Autowired
  private ObjectMapper objectMapper;

  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/tasks";
    taskRepository.deleteAll(); // garante que o banco está limpo antes de cada teste
  }

  @Test
  @DisplayName("POST /tasks - deve criar uma task e retornar 201")
  void createTask_success() {
    Task task = new Task();
    task.setTitle("Testar integração");
    task.setDescription("Criar teste de integração");

    ResponseEntity<Task> response = restTemplate.postForEntity(baseUrl, task, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isNotNull();
    assertThat(response.getBody().getTitle()).isEqualTo(task.getTitle());

    // Verifica se a task realmente foi salva no banco
    List<Task> tasks = taskRepository.findAll();
    assertThat(tasks).hasSize(1);
    assertThat(tasks.get(0).getTitle()).isEqualTo("Testar integração");
  }

  @Test
  @DisplayName("GET /tasks - deve retornar lista de tasks")
  void getAllTasks_success() {
    // Arrange
    Task task1 = new Task();
    task1.setTitle("Task 1");
    taskRepository.save(task1);

    Task task2 = new Task();
    task2.setTitle("Task 2");
    taskRepository.save(task2);

    // Act
    ResponseEntity<Task[]> response = restTemplate.getForEntity(baseUrl, Task[].class);

    // Assert
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).hasSize(2);
  }

  @Test
  @DisplayName("PATCH /tasks/{id}/done - deve marcar task como concluída")
  void markTaskAsDone_success() {
    Task task = new Task();
    task.setTitle("Finalizar teste");
    task = taskRepository.save(task);

    String url = baseUrl + "/" + task.getId() + "/done";

    HttpHeaders headers = new HttpHeaders();
    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

    ResponseEntity<Task> response = restTemplate.exchange(url, HttpMethod.PATCH, requestEntity, Task.class);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody().isDone()).isTrue();

    // Verifica no banco
    Task saved = taskRepository.findById(task.getId()).get();
    assertThat(saved.isDone()).isTrue();
  }
}
