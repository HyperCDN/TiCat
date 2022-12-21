package de.hypercdn.ticat.api.entities.sql.repo

import de.hypercdn.ticat.api.entities.sql.entities.Audit
import org.springframework.data.repository.CrudRepository

interface AuditLogRepository : CrudRepository<Audit, Long> {
}