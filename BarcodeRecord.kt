package com.fastscanner.app.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "scan_history")
data class BarcodeRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rawValue: String,
    val displayValue: String,
    val format: Int,
    val valueType: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM scan_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<BarcodeRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: BarcodeRecord)

    @Delete
    suspend fun delete(record: BarcodeRecord)

    @Query("DELETE FROM scan_history")
    suspend fun clearAll()

    @Update
    suspend fun update(record: BarcodeRecord)
}

@Database(entities = [BarcodeRecord::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "scanner_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
