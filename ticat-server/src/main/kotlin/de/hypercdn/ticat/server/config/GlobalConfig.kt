package de.hypercdn.ticat.server.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@EnableAspectJAutoProxy
@EnableScheduling
@EnableAsync
@EnableCaching
@ComponentScan
class GlobalConfig