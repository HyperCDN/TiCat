package de.hypercdn.ticat.api.endpoints.config

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ConfigHandler @Autowired constructor(
    val mapper: ObjectMapper,
    @Value("\${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    val resourceServerURL: String,
    @Value("\${spring.security.oauth2.resourceserver.jwt.client-id}")
    val resourceServerClientId: String
) {

    @GetMapping("/config")
    fun getFrontendConfigData(): JsonNode {
        return mapper.createObjectNode()
            .set("auth", mapper.createObjectNode()
                .put("resourceServerURL", resourceServerURL)
                .put("clientId", resourceServerClientId) )
    }

}