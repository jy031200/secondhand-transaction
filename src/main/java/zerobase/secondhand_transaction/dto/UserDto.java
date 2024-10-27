package zerobase.secondhand_transaction.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;
import zerobase.secondhand_transaction.entity.constant.Authority;

public class UserDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Login {

        @NotBlank(message = "이메일을 입력해 주세요")
        private String email;

        @NotBlank(message = "패스워드를 입력해 주세요")
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SignUp {

        @NotBlank(message = "이메일을 작성해주세요")
        @Email(message = "이메일 형식이 잘못되었습니다.")
        private String email;

        @NotBlank(message = "닉네임을 작성해주세요")
        @Size(min = 2, max = 13)
        private String nickname;

        @NotBlank(message = "패스워드를 작성해 주세요")
        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&#])[A-Za-z\\d@$!%*?&#]{8,}$",
                message = "비밀번호 형식이 올바르지 않습니다. 8자 이상, 대소문자 포함, 숫자 및 특수문자(@$!%*?&#) 포함")
        private String password;


        @NotBlank(message = "전화번호를 작성해주세요")
        @Size(min = 11, max = 13)
        @Pattern(regexp = "^\\d{2,3}-\\d{3,4}-\\d{4}$",
                message = "핸드폰 번호의 약식과 맞지 않습니다. xxx-xxxx-xxxx")
        // 받아들이는 문자의 형태를 직접 제한하는 방법 (정규식을 사용)
        // message에는 error가 발생했을 때 출력할 메세지를 적는다.
        private int phone_nb;

        @NotNull(message = "권한을 선택해주세요")
        private Authority authority;
    }
        // 로그인 응답용 dto (패스워드 리턴은 보안상 이유로 x)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginResponse {
        private String email;
        private String username;
        private Authority authority;
    }
}

