package example.oauth2_jwt.service;

import example.oauth2_jwt.dto.TodoDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface TodoService {
    List<TodoDTO> syncTodos(String token, List<TodoDTO> todoDTOList);

    List<TodoDTO> get(String  token);
}
