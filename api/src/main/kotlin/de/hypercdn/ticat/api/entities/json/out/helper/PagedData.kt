package de.hypercdn.ticat.api.entities.json.out.helper

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.domain.Pageable

class PagedData<E>(pageable: Pageable) {

    @JsonProperty(value = "chunkSize", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var chunkSize: Int? = null

    @JsonProperty(value = "page", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var selectedPage: Int? = null

    @JsonProperty(value = "entities", required = true)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var entities: List<E>? = null

    init {
        chunkSize = pageable.pageSize
        selectedPage = pageable.pageNumber
    }

}