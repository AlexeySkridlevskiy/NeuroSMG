package com.example.neurosmg.common

import androidx.fragment.app.Fragment
import com.example.neurosmg.tests.cbt.CBTTest
import com.example.neurosmg.tests.fot.FOTTest
import com.example.neurosmg.tests.gng.GNGTest
import com.example.neurosmg.tests.iat.IATTest
import com.example.neurosmg.tests.mrt.MRTTest
import com.example.neurosmg.tests.rat.RATTest
import com.example.neurosmg.tests.sct.SCTTest
import com.example.neurosmg.tests.tmt.TMTTest

fun String.toFragment(): Fragment =
    when (this) {
        "FOT" -> FOTTest.newInstance()
        "RAT" -> RATTest.newInstance()
        "IAT" -> IATTest.newInstance()
        "GNG" -> GNGTest.newInstance()
        "SCT" -> SCTTest.newInstance()
        "TMT" -> TMTTest.newInstance()
        "CBT" -> CBTTest.newInstance()
        "MRT" -> MRTTest.newInstance()

        else -> throw RuntimeException("Unknown test name")
    }
