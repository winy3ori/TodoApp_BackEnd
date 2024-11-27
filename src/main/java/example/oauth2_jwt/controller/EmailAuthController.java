package example.oauth2_jwt.controller;


import example.oauth2_jwt.dto.EmailAuthDto;
import example.oauth2_jwt.service.EmailAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/emailAuth")
public class EmailAuthController {

    private final EmailAuthService emailAuthService;

    @PostMapping("/sendEmail")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> emailData) {
        String email = emailData.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(500).body("error");
        }
        try {
            Map<String, String> response = emailAuthService.sendEmail(email);
            return ResponseEntity.ok("인증 이메일 전송 완료" + response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error");
        }
    }

    @PostMapping("/verifyCode")
    public ResponseEntity<?> verifyCode(@RequestBody EmailAuthDto emailAuthDto) {
        try {
            EmailAuthDto response = emailAuthService.emailCheck(emailAuthDto);
            return ResponseEntity.ok("이메일 인증 완료" + response);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("error");
        }
    }


}
