package org.zeroxlab.momodict;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.zeroxlab.momodict.model.Store;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

@Config(constants = BuildConfig.class)
@RunWith(RobolectricTestRunner.class)
public class ControllerTest {

    private Controller mCtrl;

    @Before
    public void setUp() throws Exception {
        Context ctx = RuntimeEnvironment.application;
        mCtrl = new Controller(ctx, mock(Store.class));
    }

    @Test
    public void testCreation() throws Exception {
        assertNotNull(mCtrl);
    }
}
