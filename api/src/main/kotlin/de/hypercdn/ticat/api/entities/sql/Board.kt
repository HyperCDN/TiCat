package de.hypercdn.ticat.api.entities.sql

import jakarta.persistence.*
import jakarta.persistence.Table
import org.hibernate.annotations.*
import java.time.OffsetDateTime
import java.util.*

@Entity
@Table(name = "boards")
@DynamicInsert
@DynamicUpdate
class Board {

    @Id
    @Column(
        name = "board_id",
        nullable = false,
        updatable = false
    )
    lateinit var id: String

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false
    )
    @ColumnDefault("NOW()")
    @CreationTimestamp
    lateinit var createdAt: OffsetDateTime

    @Column(
        name = "modified_at",
        nullable = false
    )
    @ColumnDefault("NOW()")
    @UpdateTimestamp
    var modifiedAt: OffsetDateTime = OffsetDateTime.now()

    @Column(
        name = "title",
        nullable = false,
        updatable = false
    )
    lateinit var title: String

    @Column(
        name = "description",
        nullable = true
    )
    @ColumnDefault("NULL")
    var description: String? = null

    @Column(
        name = "ownership",
        nullable = false,
        updatable = false
    )
    lateinit var ownerUUID: UUID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ownership", referencedColumnName = "user_uuid",
        insertable = false, updatable = false)
    lateinit var owner: User

    @Column(
        name = "visibility",
        nullable = false
    )
    @ColumnDefault("MEMBERS_ONLY")
    @Enumerated(EnumType.STRING)
    var visibility: Visibility = Visibility.MEMBERS_ONLY

    @Column(
        name = "access_mode",
        nullable = false
    )
    @ColumnDefault("MANUAL_ADD")
    @Enumerated(EnumType.STRING)
    var accessMode: AccessMode = AccessMode.MANUAL_ADD

    enum class AccessMode {
        PUBLIC_JOIN,
        MANUAL_VERIFY,
        MANUAL_ADD
    }

    enum class Visibility {
        ANYONE,
        LOGGED_IN_USER,
        MEMBERS_ONLY
    }

}