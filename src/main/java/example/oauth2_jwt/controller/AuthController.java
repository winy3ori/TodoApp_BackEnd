package example.oauth2_jwt.controller;

import example.oauth2_jwt.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

    private final JWTUtil jwtUtil;

    @GetMapping("/status")
    public ResponseEntity<String> checkLoginStatus(HttpServletRequest request) {
        String token = getTokenFromRequest(request);
        if (token != null) {
            // JWT 토큰 로그
            System.out.println("Received Token: " + token);

            if (jwtUtil.validateToken(token)) {
                System.out.println("Token is valid");
                return ResponseEntity.ok("User is authenticated");
            } else {
                System.out.println("Token is invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
            }
        } else {
            System.out.println("No token found");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User is not authenticated");
        }
    }

    // 로그아웃 메서드 (쿠키 삭제)
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("Authorization", null);
        cookie.setHttpOnly(true);  // 클라이언트에서 접근할 수 없도록 설정
        cookie.setSecure(true);    // HTTPS 환경에서만 쿠키 전송
        cookie.setPath("/");       // 쿠키가 설정된 경로
        cookie.setMaxAge(0);       // 쿠키 만료 시간 설정 (0으로 설정하면 즉시 만료)

        // 쿠키 삭제
        response.addCookie(cookie);

        return ResponseEntity.ok("Successfully logged out");
    }

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