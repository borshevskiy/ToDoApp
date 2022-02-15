package com.borshevskiy.todoapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.borshevskiy.todoapp.data.models.Priority
import com.borshevskiy.todoapp.utils.Constants.Companion.TODO_TABLE

@Entity(tableName = TODO_TABLE)
data class ToDoData(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var title: String,
    var priority: Priority,
    var description: String
)
