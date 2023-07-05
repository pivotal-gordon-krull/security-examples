package com.example.securityexample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@EnableMethodSecurity
class SecurityServerConfig {

    @Configuration
    class SecurityConfig(private val env: Environment) {
        @Throws(Exception::class)
        @Bean
        public fun filterChain(http: HttpSecurity): SecurityFilterChain {
            http
                .authorizeHttpRequests {
                    it.anyRequest().authenticated()
                }
                .formLogin {}

            return http.build()
        }

        @Bean
        fun users(): UserDetailsService {
        val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
        val encodedPassword = encoder.encode(env["USER_PASSWORD"])
        val users = User.builder()
        val user = users
            .username(env["USER_NAME"])
            .password(encodedPassword)
            .roles("USER")
            .build()

        return InMemoryUserDetailsManager(user)
        }
    }
}
