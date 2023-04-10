package shop.mtcoding.securityapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    
    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //1. CSRF 해제
        http.csrf().disable();

        //2. Form 로그인 설정
        http.formLogin()
        .loginPage("/loginForm")
        //  커스텀마이징 
        .usernameParameter("username")
        .passwordParameter("password")
        // 
        .loginProcessingUrl("/login") // Post
        .defaultSuccessUrl( "/")
        .successHandler((req, resp, authentucation) -> {
            System.out.println("디버그 : 로그인이 완료되었습니다.");
        })
        .failureHandler((req, resp, ex) -> {
            System.out.println("디버그 : 로그인 실패 ->" + ex.getMessage());
        });

        //3. 인증권한 필터설정
        http.authorizeRequests(
            authorize -> authorize.antMatchers("/users/**")
                                    .authenticated()
                                    .antMatchers("/admin/**").hasRole("ADMIN")
                                    .antMatchers("/manager/**").access("hasRole('ADMIN') or hasRole('MANAGER')")
                                    .anyRequest().permitAll()
            );
        return http.build();
    } // 시큐리티 설정 비활성화 ! 왜? 커스텀마이징
}
