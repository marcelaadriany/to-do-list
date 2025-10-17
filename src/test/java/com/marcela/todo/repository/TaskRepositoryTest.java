package com.marcela.todo.repository;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.marcela.todo.controller.TaskController;
import com.marcela.todo.model.Task;
import com.marcela.todo.service.TaskService;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

// a notacao abaixo é pra remover o aviso de depreciacao do MockBean
// pois ele esta marcado para remocao e será substituido pelo @ReplaceBean
@SuppressWarnings("removal")
@WebMvcTest(TaskController.class)
public class TaskRepositoryTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private TaskService taskService;

  @Autowired
  private ObjectMapper objectMapper; // converte objetos para JSON

  private Task task1;
  private Task task2;

  @BeforeEach
  void setup () {
    task1 = new Task();
    task1.setId(1L);
    task1.setTitle("Comprar leite");
    task1.setDescription("Ir ao supermercado");
    task1.setDone(false);
    task1.setCreatedAt(LocalDateTime.now());

    task2 = new Task();
    task2.setId(2L);
    task2.setTitle("Estudar Java");
    task2.setDescription("Revisar Java");
    task2.setDone(false);
    task2.setCreatedAt(LocalDateTime.now());
  }

  // POST / tasks - sucesso
  @Test
  @DisplayName("POST /tasks - deve criar uma task e retornar 201")
  void createTask_success() throws Exception {
    // Arrange> quando o service salvar, retorna a task com id preenchido
    when(taskService.createTask(any(Task.class))).thenReturn(task1);
    // Act & Assert
    mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(task1))) // envia JSON
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.title").value("Comprar leite"));
  }

}
