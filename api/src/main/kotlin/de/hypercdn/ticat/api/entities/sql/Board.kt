package de.hypercdn.ticat.api.entities.sql

import de.hypercdn.ticat.api.entities.sql.enums.BoardAccessMode
import de.hypercdn.ticat.api.entities.sql.enums.BoardVisibility
import jakarta.persistence.*
import org.hibernate.annotations.*
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "boards")
@DynamicInsert
@DynamicUpdate
class Board() {

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
    @Generated(GenerationTime.INSERT)
    @ColumnDefault("NOW()")
    lateinit var createdAt: LocalDateTime

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
    var visibility: BoardVisibility = BoardVisibility.MEMBERS_ONLY

    @Column(
        name = "access_mode",
        nullable = false
    )
    @ColumnDefault("MANUAL_ADD")
    @Enumerated(EnumType.STRING)
    var accessMode: BoardAccessMode = BoardAccessMode.MANUAL_ADD

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    var members: List<Member> = emptyList()

}