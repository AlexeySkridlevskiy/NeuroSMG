package com.example.neurosmg.common

import androidx.fragment.app.Fragment
import com.example.neurosmg.questionnaires.QuestionnaireAudit
import com.example.neurosmg.tests.cbt.CBTTest
import com.example.neurosmg.tests.fot.FOTTest
import com.example.neurosmg.tests.gng.GNGTest
import com.example.neurosmg.tests.iat.IATTest
import com.example.neurosmg.tests.iat2.IATTest2
import com.example.neurosmg.tests.mrt.MRTTest
import com.example.neurosmg.tests.rat.RATTest
import com.example.neurosmg.tests.sct.SCTTest
import com.example.neurosmg.tests.tmt.TMTTest

fun String.toFragment(patientId: Int): Fragment =
    when (this) {
        "FOT" -> FOTTest.newInstance(patientId = patientId)
        "RAT" -> RATTest.newInstance(patientId = patientId)
        "IAT" -> IATTest.newInstance(patientId = patientId)
        "IAT 2" -> IATTest2.newInstance(patientId = patientId)
        "GNG" -> GNGTest.newInstance(patientId = patientId)
        "SCT" -> SCTTest.newInstance(patientId = patientId)
        "TMT" -> TMTTest.newInstance(patientId = patientId)
        "CBT" -> CBTTest.newInstance(patientId = patientId)
        "MRT" -> MRTTest.newInstance(patientId = patientId)
        else -> QuestionnaireAudit.newInstance(patientId = patientId)
    }
