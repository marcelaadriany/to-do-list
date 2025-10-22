package com.marcela.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcela.todo.model.Task;
import com.marcela.todo.service.TaskService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @Autowired
  private ObjectMapper objectMapper; // para converter objetos para JSON

  private Task task1;
  private Task task2;

  @BeforeEach
  void setup() {
    task1 = new Task();
    task1.setId(1L);
    task1.setTitle("Comprar leite");
    task1.setDescription("Ir ao supermercado");
    task1.setDone(false);
    task1.setCreatedAt(LocalDateTime.now());

    task2 = new Task();
    task2.setId(2L);
    task2.setTitle("Estudar Java");
    task2.setDescription("Revisar Spring Boot");
    task2.setDone(false);
    task2.setCreatedAt(LocalDateTime.now());
  }

  // -----------------------
  // POST /tasks - sucesso
  // -----------------------
  @Test
  @DisplayName("POST /tasks - deve criar uma task e retornar 201")
  void createTask_success() throws Exception {
    // Arrange: quando o service salvar, retorna a task com id preenchido
    when(taskService.createTask(any(Task.class))).thenReturn(task1);

    // Act & Assert
    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task1))) // envia JSON
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Comprar leite"));
  }

  // -----------------------
  // POST /tasks - validação (observação)
  // -----------------------
  @Test
  @DisplayName("POST /tasks - se não usar @Valid no controller, validação JSR-380 não é aplicada automaticamente")
  void createTask_validationNote() throws Exception {
    Task invalid = new Task();
    invalid.setDescription("sem título");
    invalid.setTitle(""); // título em branco - inválido por causa do @NotBlank

    // Arrange (mesmo que o service seja mockado, o @Valid impede que o fluxo chegue até ele
    when(taskService.createTask(any(Task.class))).thenReturn(invalid);

    // Act & Assert
    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalid)))
        .andExpect(status().isBadRequest());
        // implementar parte abaixo quando eu incluir tratamento de erro global (GlobalExceptionHandler)
        //.andExpect(jsonPath("$.errors").exists())
        //.andExpect(jsonPath("$.errors[0].defaultMessage").value("Título é obrigatório"));

  }

  // -----------------------
  // GET /tasks - sucesso
  // -----------------------
  @Test
  @DisplayName("GET /tasks - retorna lista de tasks")
  void getAllTasks_success() throws Exception {
    when(taskService.getAllTasks()).thenReturn(List.of(task1, task2));

    mockMvc.perform(get("/tasks"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()", is(2)))
        .andExpect(jsonPath("$[0].title").value("Comprar leite"))
        .andExpect(jsonPath("$[1].title").value("Estudar Java"));
  }

  // -----------------------
  // GET /tasks/{id} - sucesso / not found
  // -----------------------
  @Test
  @DisplayName("GET /tasks/{id} - quando existe retorna 200 e a task")
  void getTaskById_success() throws Exception {
    when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));

    mockMvc.perform(get("/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Comprar leite"));
  }

  @Test
  @DisplayName("GET /tasks/{id} - quando não existe retorna 404")
  void getTaskById_notFound() throws Exception {
    when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/tasks/99"))
        .andExpect(status().isNotFound());
  }

  // -----------------------
  // PUT /tasks/{id} - sucesso / not found
  // -----------------------
  @Test
  @DisplayName("PUT /tasks/{id} - atualiza quando existe")
  void updateTask_success() throws Exception {
    Task updated = new Task();
    updated.setId(1L);
    updated.setTitle("Comprar ovos");
    updated.setDescription("Ir ao mercado");
    updated.setDone(false);

    when(taskService.getTaskById(1L)).thenReturn(Optional.of(task1));
    when(taskService.updateTask(any(Task.class))).thenReturn(updated);

    mockMvc.perform(put("/tasks/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updated)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Comprar ovos"))
        .andExpect(jsonPath("$.description").value("Ir ao mercado"));
  }

  @Test
  @DisplayName("PUT /tasks/{id} - retorna 404 quando id não existe")
  void updateTask_notFound() throws Exception {
    when(taskService.getTaskById(99L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/tasks/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task1)))
        .andExpect(status().isNotFound());
  }

  // -----------------------
  // PATCH /tasks/{id}/done - sucesso / not found
  // -----------------------
  @Test
  @DisplayName("PATCH /tasks/{id}/done - marca como concluída quando existe")
  void markAsDone_success() throws Exception {
    Task doneTask = new Task();
    doneTask.setId(1L);
    doneTask.setTitle(task1.getTitle());
    doneTask.setDone(true);
    doneTask.setCompletedAt(LocalDateTime.now());

    when(taskService.markTaskAsDone(1L)).thenReturn(Optional.of(doneTask));

    mockMvc.perform(patch("/tasks/1/done"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.done").value(true))
        .andExpect(jsonPath("$.completedAt").isNotEmpty());
  }

  @Test
  @DisplayName("PATCH /tasks/{id}/done - retorna 404 quando id não existe")
  void markAsDone_notFound() throws Exception {
    when(taskService.markTaskAsDone(99L)).thenReturn(Optional.empty());

    mockMvc.perform(patch("/tasks/99/done"))
        .andExpect(status().isNotFound());
  }

  // -----------------------
  // DELETE /tasks/{id} - sucesso / not found
  // -----------------------
  @Test
  @DisplayName("DELETE /tasks/{id} - retorna 204 quando deletado")
  void deleteTask_success() throws Exception {
    when(taskService.deleteTask(1L)).thenReturn(true);

    mockMvc.perform(delete("/tasks/1"))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /tasks/{id} - retorna 404 quando id inexistente")
  void deleteTask_notFound() throws Exception {
    when(taskService.deleteTask(99L)).thenReturn(false);

    mockMvc.perform(delete("/tasks/99"))
        .andExpect(status().isNotFound());
  }
}
