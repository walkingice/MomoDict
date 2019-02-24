package org.zeroxlab.momodict

import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.zeroxlab.momodict.model.Store

@Config(constants = BuildConfig::class)
@RunWith(RobolectricTestRunner::class)
class ControllerTest {

    private var mCtrl: Controller? = null

    @Before
    @Throws(Exception::class)
    fun setUp() {
        val ctx = RuntimeEnvironment.application
        mCtrl = Controller(ctx, mock(Store::class.java))
    }

    @Test
    @Throws(Exception::class)
    fun testCreation() {
        assertNotNull(mCtrl)
    }
}
