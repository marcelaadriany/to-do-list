package com.marcela.todo.repository;

import com.marcela.todo.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
  // aqui eu posso criar métodos customizados
  // exemplo: List<Task> findByDoneFalse(); busca as tasks que não foram finalizadas
}
