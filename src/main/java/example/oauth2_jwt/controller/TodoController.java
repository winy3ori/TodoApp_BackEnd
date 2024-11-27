package example.oauth2_jwt.controller;

import example.oauth2_jwt.dto.TodoDTO;
import example.oauth2_jwt.service.TodoService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todo")
@AllArgsConstructor
public class TodoController {

    private final TodoService todoService;

    @GetMapping("/get")
    public ResponseEntity<?> get(HttpServletRequest request) {
        try {
            String token = getTokenFromRequest(request);
            List<TodoDTO> todoDTOList = todoService.get(token);
            return ResponseEntity.ok(todoDTOList);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Todo 불러오기 실패: " + e.getMessage());
        }
    }

    @PostMapping("/sync")
    public ResponseEntity<?> syncTodos(HttpServletRequest request, @RequestBody List<TodoDTO> todoDTOList) {
        try {
            String token = getTokenFromRequest(request);
            if (todoDTOList == null) {
                return ResponseEntity.ok("Todo is Empty");
            }
            List<TodoDTO> savedTodos = todoService.syncTodos(token, todoDTOList);
            return ResponseEntity.ok(savedTodos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Todo 동기화 실패 : " + e.getMessage());
        }

    }

    // 쿠키에서 token 추출
    private String getTokenFromRequest(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}