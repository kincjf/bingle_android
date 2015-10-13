package net.sourceforge.opencamera.test;

import junit.framework.Test;
import junit.framework.TestSuite;

public class PhotoTests {
	// Tests related to taking photos; note that tests to do with photo mode that don't take photos are still part of MainTests
	public static Test suite() {
		TestSuite suite = new TestSuite(MainTests.class.getName());
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhoto"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoNoAutofocus"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoNoThumbnail"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoFlashBug"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoFrontCamera"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoLockedFocus"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoManualFocus"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoExposureCompensation"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoManualISOExposure"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoLockedLandscape"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoLockedPortrait"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoSAF"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoPreviewPaused"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoPreviewPausedSAF"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoPreviewPausedTrash"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoPreviewPausedTrashSAF"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoQuickFocus"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoRepeatFocus"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoRepeatFocusLocked"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoAlt"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoAutoLevel"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoAutoLevelAngles"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTimerBackground"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTimerSettings"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTimerPopup"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testTakePhotoBurst"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testLocationOn"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testLocationDirectionOn"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testLocationOff"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testDirectionOn"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testPhotoStamp"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolder1"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolder2"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolder3"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolder4"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolderUnicode"));
		suite.addTest(TestSuite.createTest(MainActivityTest.class, "testCreateSaveFolderEmpty"));
        return suite;
    }
}
