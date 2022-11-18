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
            .csrf().disable()
            .cors().disable()
            .anonymous().disable()
            .authorizeHttpRequests {
                // no auth or optional
                it.requestMatchers( HttpMethod.GET,"/config", "/u/*", "/b", "/b/*", "/t/*", "/t/*/*", "/m/*", "/m/*/*").permitAll()
                it.requestMatchers( HttpMethod.POST,"" ).permitAll()
                it.requestMatchers( HttpMethod.PATCH,"" ).permitAll()
                it.requestMatchers( HttpMethod.DELETE,"" ).permitAll()
                // auth required
                it.requestMatchers( HttpMethod.GET,"" ).authenticated()
                it.requestMatchers( HttpMethod.POST,"/b", "/i/*", "/i/*/*" ).authenticated()
                it.requestMatchers( HttpMethod.PATCH,"/b/*", "/m/*/*" ).authenticated()
                it.requestMatchers( HttpMethod.DELETE,"/b/*", "/m/*/*" ).authenticated()
            }
            .oauth2ResourceServer {
                it.jwt {  }
            }

        return http.build()
    }

}