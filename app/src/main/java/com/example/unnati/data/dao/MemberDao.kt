package com.example.unnati.data.dao

import androidx.room.*
import com.example.unnati.data.entity.Member
import kotlinx.coroutines.flow.Flow

@Dao
interface MemberDao {
    @Query("SELECT * FROM members WHERE isActive = 1 ORDER BY name ASC")
    fun getAllActiveMembers(): Flow<List<Member>>

    @Query("SELECT * FROM members WHERE id = :id")
    suspend fun getMemberById(id: Int): Member?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: Member): Long

    @Update
    suspend fun updateMember(member: Member)

    @Query("UPDATE members SET isActive = 0 WHERE id = :id")
    suspend fun softDeleteMember(id: Int)
}
