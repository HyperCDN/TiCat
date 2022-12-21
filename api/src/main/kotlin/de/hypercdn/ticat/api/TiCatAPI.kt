package de.hypercdn.ticat.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

//@SpringBootApplication(exclude=[DataSourceAutoConfiguration::class, HibernateJpaAutoConfiguration::class])
@SpringBootApplication
class ApiApplication

fun main(args: Array<String>) {
    runApplication<ApiApplication>(*args)
}
