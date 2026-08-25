package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import com.example.model.Association
import kotlinx.coroutines.flow.Flow

@Dao
interface AssociationDao {
    @Query("SELECT * FROM associations ORDER BY startDate DESC")
    fun getAllAssociations(): Flow<List<Association>>

    @Query("SELECT * FROM associations WHERE id = :id")
    fun getAssociationById(id: Int): Flow<Association?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssociation(association: Association): Long

    @Update
    suspend fun updateAssociation(association: Association)

    @Delete
    suspend fun deleteAssociation(association: Association)
}
