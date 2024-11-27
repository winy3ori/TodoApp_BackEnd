package example.oauth2_jwt.service;

import example.oauth2_jwt.dto.EmailAuthDto;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public interface EmailAuthService {
    Map<String, String> sendEmail(String email);

    EmailAuthDto emailCheck(EmailAuthDto emailAuthDto);
}
