package com.example.neurosmg.questionnaires

import androidx.lifecycle.ViewModel
import com.example.neurosmg.testsPage.TestItem

class QuestionnaireListViewModel: ViewModel() {
    private val questionnaires = listOf(
        QuestionnaireItem("AUDIT"),
        QuestionnaireItem("Другой опросник"),
        QuestionnaireItem("Еще опросник"),
    )

    fun getQuestionnaires(): List<QuestionnaireItem> {
        return questionnaires
    }
}