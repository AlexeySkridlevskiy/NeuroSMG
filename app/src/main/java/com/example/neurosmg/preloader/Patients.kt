package com.example.neurosmg.preloader

data class PersonData(
    val data: List<Person>,
)

data class Person(
    val id: Int,
    val attributes: Attributes
)

data class Attributes(
    val Birthday: String,
    val Gender: String,
    val LeadHand: String,
    val Comment: String,
    val createdAt: String,
    val updatedAt: String,
    val publishedAt: String
)
