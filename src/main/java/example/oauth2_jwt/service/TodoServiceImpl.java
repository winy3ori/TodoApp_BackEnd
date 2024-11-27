package example.oauth2_jwt.service;

import example.oauth2_jwt.dto.TodoDTO;
import example.oauth2_jwt.entity.TodoEntity;
import example.oauth2_jwt.entity.UserEntity;
import example.oauth2_jwt.jwt.JWTUtil;
import example.oauth2_jwt.repository.TodoRepository;
import example.oauth2_jwt.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TodoServiceImpl implements TodoService {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    // 토큰에서 username을 추출하는 메서드
    public String getUsernameFromToken(String token) {
        if (jwtUtil.validateToken(token)) {
            return jwtUtil.getUsername(token.replace("Bearer ", ""));
        }
        throw new RuntimeException("Invalid token");
    }

    @Override
    public List<TodoDTO> syncTodos(String token, List<TodoDTO> todoDTOList) {
        String username = getUsernameFromToken(token);

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Client -> todoDTOList가 비어있는 경우, 그냥 빈 배열 반환
        if (todoDTOList == null || todoDTOList.isEmpty()) {
            return todoDTOList;  // 비어있으면 아무 작업 없이 그대로 반환
        }

        // DB에서 해당 사용자의 todo 목록을 가져옴
        List<TodoEntity> existingTodos = todoRepository.findByUserId(user.getId());

        // todoDTOList를 TodoEntity로 변환
        List<TodoEntity> todoEntitiesToSave = todoDTOList.stream()
                .map(todoDTO -> {
                    TodoEntity todoEntity = new TodoEntity();
                    todoEntity.setId(todoDTO.getId());
                    todoEntity.setTitle(todoDTO.getTitle());
                    todoEntity.setStatus(todoDTO.getStatus());
                    todoEntity.setTime(todoDTO.getTime());
                    todoEntity.setUser(user); // 사용자 정보 추가
                    return todoEntity;
                })
                .collect(Collectors.toList());


        // 1. 삭제된 Todo는 DB에서 삭제
        for (TodoEntity existingTodo : existingTodos) {
            boolean existsInClient = todoEntitiesToSave.stream()
                    .anyMatch(todo -> todo.getId().equals(existingTodo.getId()));
            if (!existsInClient) {
                todoRepository.delete(existingTodo); // 클라이언트에 없는 항목은 삭제
            }
        }

        // 2. 클라이언트에서 보낸 todoEntitiesToSave는 DB에 저장 또는 업데이트
        for (TodoEntity todoEntity : todoEntitiesToSave) {
            TodoEntity existingTodo = existingTodos.stream()
                    .filter(todo -> todo.getId().equals(todoEntity.getId()))
                    .findFirst()
                    .orElse(null);

            if (existingTodo != null) {
                // 기존 항목이 있다면 업데이트
                existingTodo.setTitle(todoEntity.getTitle());
                existingTodo.setStatus(todoEntity.getStatus());
                existingTodo.setTime(todoEntity.getTime());
                todoRepository.save(existingTodo);
            } else {
                // 새로운 항목이라면 저장
                todoRepository.save(todoEntity);
            }
        }

        // DB에서 최신 Todo 목록을 반환
        List<TodoEntity> updatedTodos = todoRepository.findByUserId(user.getId());

        return updatedTodos.stream()
                .map(todoEntity -> new TodoDTO(todoEntity.getId(), todoEntity.getTitle(), todoEntity.getStatus(), todoEntity.getTime()))
                .collect(Collectors.toList());

    }

    @Override
    public List<TodoDTO> get(String token) {
        String username = getUsernameFromToken(token);

        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        List<TodoEntity> todoEntities = todoRepository.findByUserId(user.getId());

        return todoEntities.stream()
                .map(todoEntity -> new TodoDTO(
                        todoEntity.getId(),
                        todoEntity.getTitle(),
                        todoEntity.getStatus(),
                        todoEntity.getTime()))
                .collect(Collectors.toList());
    }
}
