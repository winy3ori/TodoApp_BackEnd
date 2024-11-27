package example.oauth2_jwt.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TodoEntity {

    @Id
    private String id; // 프론트에서 전달된 UUID를 그대로 사용
    private String title; // 할 일 제목
    private String status; // 상태 (e.g., complete, incomplete)

    private String time; // 생성 또는 업데이트 시간 (LocalDateTime으로 저장)

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity user; // 할 일과 관련된 사용자 (로그인한 사용자)
}