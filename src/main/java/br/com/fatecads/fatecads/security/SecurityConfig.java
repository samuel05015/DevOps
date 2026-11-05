package br.com.fatecads.fatecads.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(
                                                                "/",
                                                                "/login",
                                                                "/cadastro",
                                                                "/esqueci-senha",
                                                                "/recuperar-senha",
                                                                "/css/**",
                                                                "/img/**")
                                                .permitAll()
                                                .requestMatchers("/produtos/imagem/**")
                                                .hasAnyRole("ADMIN", "USER")
                                                .requestMatchers("/admin", "/admin/**", "/alunos/**", "/professores/**",
                                                                "/cursos/**", "/disciplinas/**", "/produtos/**",
                                                                "/pedidos/**", "/usuarios/**")
                                                .hasRole("ADMIN")
                                                .requestMatchers("/loja", "/loja/**")
                                                .hasRole("USER")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler((request, response, authentication) -> {
                                                        boolean admin = authentication.getAuthorities().stream()
                                                                        .anyMatch(authority -> authority.getAuthority()
                                                                                        .equals("ROLE_ADMIN"));
                                                        String destino = admin ? "/admin" : "/loja";
                                                        response.sendRedirect(request.getContextPath() + destino);
                                                })
                                                .permitAll())
                                .exceptionHandling(exception -> exception.accessDeniedPage("/acesso-negado"))
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll());

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
                        throws Exception {
                return config.getAuthenticationManager();
        }

}
