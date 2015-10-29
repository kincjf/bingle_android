package net.sourceforge.opencamera.test;

import android.graphics.BitmapFactory;
import android.os.Environment;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

import com.adobe.xmp.XMPException;
import com.adobe.xmp.XMPMeta;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.xmp.XmpDirectory;

import junit.framework.TestCase;

import net.sourceforge.opencamera.Util.XmpUtil;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * Test Add XmpMeta in /sdcard/Sample jpg files
 * @link https://developers.google.com/streetview/spherical-metadata#metadata_properties
 *
 * Created by KIMSEONHO on 2015-10-29.
 */
public class XmpCoreTest extends TestCase {
    final String TAG = "XmpCoreTest";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

    }

    @MediumTest
    public void testXmlUtil(){
        XmpDirectory _directory;
        final String dirName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Sample";

        File dir = new File(dirName);
        String[] extensions = new String[] { "jpg" };

        try {
            Log.i(TAG, "Getting all jpg files in " + dir.getCanonicalPath()
                    + " including those in subdirectories");
            List<File> files = (List<File>) FileUtils.listFiles(dir, extensions, true);

            for (File file : files) {
                Log.i(TAG, "-------------------------------------");
                Log.i(TAG, "file: " + file.getCanonicalPath());
                String absolutePath = file.getAbsolutePath();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(absolutePath, options);

                int imageWidth = options.outWidth;
                int imageHeight = options.outHeight;

                XMPMeta xmpMeta = XmpUtil.extractOrCreateXMPMeta(absolutePath);
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "UsePanoramaViewer", true);
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "ProjectionType", "equirectangular");
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "PoseHeadingDegrees", 0);

                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "CroppedAreaImageWidthPixels", imageWidth);
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "CroppedAreaImageHeightPixels", imageHeight);

                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "FullPanoWidthPixels", imageWidth);
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "FullPanoHeightPixels", imageHeight);

                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "CroppedAreaLeftPixels", 0);
                xmpMeta.setProperty(XmpUtil.GOOGLE_PANO_NAMESPACE, "CroppedAreaTopPixels", 0);

                XmpUtil.writeXMPMeta(absolutePath, xmpMeta);

                Metadata metadata = ImageMetadataReader.readMetadata(file);
                Collection<XmpDirectory> xmpDirectories = metadata.getDirectoriesOfType(XmpDirectory.class);

                assertNotNull("xmpDirectory is null", xmpDirectories);

                _directory = xmpDirectories.iterator().next();
                assertNotNull("xmpDirectory not getXMPMeta", _directory.getXMPMeta());

                print(metadata);
            }
        } catch (IOException e) {
            Log.e(TAG, "IOException");
            e.printStackTrace();
        } catch (XMPException e) {
            Log.e(TAG, "XMPException");
            e.printStackTrace();
        } catch (ImageProcessingException e) {
            Log.e(TAG, "ImageProcessingException");
            e.printStackTrace();
        }
    }

//    @MediumTest
//    public void testGetXmpProperties() throws Exception
//    {
//        Map<String,String> propertyMap = _directory.getXmpProperties();
//
//        assertEquals(179, propertyMap.size());
//
//        assertTrue(propertyMap.containsKey("photoshop:Country"));
//        assertEquals("Deutschland", propertyMap.get("photoshop:Country"));
//
//        assertTrue(propertyMap.containsKey("tiff:ImageLength"));
//        assertEquals("900", propertyMap.get("tiff:ImageLength"));
//    }

    private void print(Metadata metadata)
    {
        Log.i(TAG, "-------------------------------------");

        // Iterate over the data and print to System.out

        //
        // A Metadata object contains multiple Directory objects
        //
        for (Directory directory : metadata.getDirectories()) {

            //
            // Each Directory stores values in Tag objects
            //
            for (Tag tag : directory.getTags()) {
                Log.i(TAG, String.valueOf(tag));
            }

            //
            // Each Directory may also contain error messages
            //
            if (directory.hasErrors()) {
                for (String error : directory.getErrors()) {
                    Log.e(TAG, "ERROR: " + error);
                }
            }
        }
    }
}
