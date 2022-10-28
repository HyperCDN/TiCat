package de.hypercdn.ticat.api.endpoints.root

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class RootHandler {

    @GetMapping(path = ["/"])
    fun hello() {}

}