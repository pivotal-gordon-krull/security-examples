package com.example.securityexample

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.get
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType.H2
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.security.provisioning.UserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import javax.sql.DataSource

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
                .httpBasic {}

            return http.build()
        }

        @Bean
        fun users(dataSource: DataSource): UserDetailsManager {
            val encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
            val encodedPassword = encoder.encode(env["USER_PASSWORD"])
            val user = User.builder()
                .username(env["USER_NAME"])
                .password(encodedPassword)
                .roles("USER")
                .build()
            val users = JdbcUserDetailsManager(dataSource)
            users.createUser(user)
            return users
        }

        @Bean
        fun dataSource(): DataSource {
            return EmbeddedDatabaseBuilder()
                .setType(H2)
                .addScript(JdbcDaoImpl.DEFAULT_USER_SCHEMA_DDL_LOCATION)
                .build()
        }
    }
}
