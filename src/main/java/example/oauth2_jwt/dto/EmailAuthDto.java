package example.oauth2_jwt.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class EmailAuthDto {

    private String email;
    private String verifyCode;

}
