package com.example.miniproject.data.model

data class User(
    val id: String,
    val username: String,
    val password: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val employeeId: String,
    val department: String,
    val position: String,
    val hireDate: Long,
    val salary: Double,
    val role: String
)


