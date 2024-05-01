package ru.chernyukai.projects.dating.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.chernyukai.projects.dating.model.UserAuthority;

@Slf4j
@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(expressionInterceptUrlRegistry ->
                        expressionInterceptUrlRegistry
                                .requestMatchers("/reg", "/login").permitAll() // разрешить всем регистрацию
                                .requestMatchers(HttpMethod.GET,"/").permitAll() // разрешить всем домашнюю страницу
                                .requestMatchers(HttpMethod.GET, "/profiles").hasAuthority(UserAuthority.DEFAULT_USER.getAuthority()) //профили обычных юзеров только на чтение
                                .requestMatchers(HttpMethod.GET, "/profiles/**").hasAuthority(UserAuthority.DEFAULT_USER.getAuthority()) //конкретный профиль только на чтение
                                .requestMatchers("/myprofile").hasAuthority(UserAuthority.DEFAULT_USER.getAuthority()) //для своего профиля полный доступ
                                .anyRequest().hasAuthority(UserAuthority.ADMIN.getAuthority())) //админам можно всё
                .formLogin(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

}