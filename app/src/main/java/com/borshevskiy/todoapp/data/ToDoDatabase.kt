package com.borshevskiy.todoapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ToDoData::class], version = 1, exportSchema = false)
abstract class ToDoDatabase: RoomDatabase() {

    abstract fun toDoDao(): ToDoDao

}