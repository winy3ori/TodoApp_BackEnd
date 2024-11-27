package example.oauth2_jwt.service;

import example.oauth2_jwt.dto.EmailAuthDto;
import example.oauth2_jwt.entity.EmailEntity;
import example.oauth2_jwt.enums.EmailAuthStatus;
import example.oauth2_jwt.repository.EmailAuthRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.management.RuntimeErrorException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailAuthServiceImpl implements EmailAuthService {

    private final JavaMailSender javaMailSender;
    private final EmailAuthRepository emailAuthRepository;
    private final EmailAuthRepository memberRepository;

    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    // 인증 코드 생성
    private String generateRandomCode() {
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(CHARACTERS.charAt(secureRandom.nextInt(CHARACTERS.length())));
        }
        return code.toString();
    }

    // 이메일 메시지 생성 및 인증 정보 저장
    private String prepareAndSaveEmailAuth(String email) {
        String code = generateRandomCode();
        EmailEntity emailAuth = EmailEntity.builder()
                .email(email)
                .verityCode(code)
                .regDt(LocalDateTime.now())
                .emailAuthStatus(EmailAuthStatus.UNVERIFIED)
                .build();
        emailAuthRepository.save(emailAuth);
        return code;
    }

    // 이메일 전송
    @Override
    public Map<String, String> sendEmail(String email) {

        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("사용자 없음 ");
        }

        String code = prepareAndSaveEmailAuth(email);
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, true);
            messageHelper.setTo(email);
            messageHelper.setSubject("이메일 인증 코드");
            messageHelper.setText("인증 코드: " + code);
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
        Map<String, String> response = new HashMap<>();
        response.put("email", email);
        response.put("verityCode", code);
        return response;
    }

    @Override
    public EmailAuthDto emailCheck(EmailAuthDto emailAuthDto) {
        EmailEntity emailAuth = emailAuthRepository.findByEmail(emailAuthDto.getEmail())
                .orElseThrow(() -> new RuntimeErrorException(new Error("error")));

        if (!emailAuth.getVerityCode().equals(emailAuthDto.getVerifyCode())) {
            throw new RuntimeException("error");
        }

        emailAuth.setEmailAuthStatus(EmailAuthStatus.VERIFIED);
        emailAuthRepository.save(emailAuth);
        return emailAuthDto;
    }

}
