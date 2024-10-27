package zerobase.secondhand_transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import zerobase.secondhand_transaction.entity.constant.Authority;

import java.util.Collection;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity(name= "User")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int phone_nb;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Authority authority;


    public User(String email, String nickname, String password, int phone_nb, Authority authority) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.phone_nb = phone_nb;
        this.authority = authority;
    }

    @Override // 권한 반환 : 사용자가 가진 권한
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(authority.name()));
    }

    @Override // 사용자의 고유한 값 반환 -> 여기선 email이 고유값이므로 email 반환
    public String getUsername() {
        return email;
    }

    @Override // 계정 만료 여부 반환 : 만료시 false
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override // 계정 잠금 여부 반환 : 잠금시 false
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override // 패스워드 만료 여부 반환 : 만료시 false
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override // 계정 사용 가능 여부 반환 : 사용불가능시 false
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
