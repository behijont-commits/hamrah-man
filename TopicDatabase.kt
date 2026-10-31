package com.hamrahman.app

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "topics")
data class TopicEntity(@PrimaryKey val id: String, val name: String, val description: String = "", val linkedTopicIdsJson: String = "[]")

@Entity(tableName = "audit_history")
data class AuditEntity(@PrimaryKey val id: String, val timestampMillis: Long, val action: String, val details: String)

@Entity(tableName = "topic_permissions")
data class PermissionEntity(
    @PrimaryKey val id: String, val topicId: String, val targetTopicId: String,
    val canView: Boolean, val canEdit: Boolean, val canLogProgress: Boolean,
    val canViewDetails: Boolean, val isItemOverride: Boolean = false
)

@Dao
interface TopicDao {
    @Query("SELECT * FROM topics ORDER BY name") fun observeTopics(): Flow<List<TopicEntity>>
    @Insert suspend fun insertTopic(topic: TopicEntity)
    @Query("DELETE FROM topics WHERE id = :id") suspend fun deleteTopic(id: String)
    @Query("SELECT * FROM audit_history ORDER BY timestampMillis DESC") fun observeHistory(): Flow<List<AuditEntity>>
    @Insert suspend fun insertAudit(entry: AuditEntity)
    @Query("SELECT * FROM topic_permissions") fun observePermissions(): Flow<List<PermissionEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun upsertPermission(permission: PermissionEntity)
}

@Database(entities = [TopicEntity::class, AuditEntity::class, PermissionEntity::class], version = 1, exportSchema = false)
abstract class HamrahManDatabase : RoomDatabase() { abstract fun topicDao(): TopicDao }
