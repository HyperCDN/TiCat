package de.hypercdn.ticat.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain? {
        http
            .csrf().disable().cors().disable()
            .authorizeHttpRequests {
                // no auth or optional
                it.requestMatchers( HttpMethod.GET,
                    "/config",
                    "/board/*",
                    "/boards/popular",
                    "/search/boards"
                ).permitAll()
                it.requestMatchers( HttpMethod.POST,
                    ""
                ).permitAll()
                it.requestMatchers( HttpMethod.PUT,
                    ""
                ).permitAll()
                it.requestMatchers( HttpMethod.DELETE,
                    ""
                ).permitAll()
                // auth required
                it.requestMatchers( HttpMethod.GET,
                    "/user/test",
                    "/user/info",
                    "/search/users",
                    "/boards/owned",
                    "/boards/memberof"
                ).authenticated()
                it.requestMatchers( HttpMethod.POST,
                    "/user/sync",
                    "/board"
                ).authenticated()
                it.requestMatchers( HttpMethod.PUT,
                    "/board"
                ).authenticated()
                it.requestMatchers( HttpMethod.DELETE,
                    "/board/*"
                ).authenticated()
            }
            .oauth2ResourceServer {
                it.jwt {  }
            }

        return http.build()
    }

}