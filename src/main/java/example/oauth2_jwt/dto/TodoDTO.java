package example.oauth2_jwt.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor  // 모든 필드를 받는 생성자 자동 생성
public class TodoDTO {

    private String id;
    private String title;
    private String status;

    private String time;

    // 기본 생성자는 Lombok이 자동으로 생성하지 않으므로 명시적으로 추가해야 할 수 있음
    public TodoDTO() {}
}