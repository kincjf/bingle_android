package net.sourceforge.opencamera.Command.Incomming;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readByte;
import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readWord;
import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readWordUnsigned;
import static net.sourceforge.opencamera.Data.Serial.SBGCProtocol.getRealtimeDataStructure;

/**
 * Created by KIMSEONHO on 2015-11-16.
 * CMD_REALTIME_DATA_3 - Receive real-time data for
 */
public class ParseRealtimeData implements ICommand {
    protected static final float ANGLE_TO_DEGREE = 0.02197266F;

    /*
    * Parses the received real-time data and saves it in the
    * RealtimeDataStructure
    *
    * @param data received data
    * @return
    */

    @Override
    public boolean execute(byte[] data) {
        for (int i = 0; i < 3; i++) {
            getRealtimeDataStructure().setAcc(readWord(data), i);
            getRealtimeDataStructure().setGyro(readWord(data), i);
        }

        for (int i = 0; i < getRealtimeDataStructure().getDebug().length; i++) {
            getRealtimeDataStructure().setDebug(readWord(data), i);
        }
        for (int i = 0; i < getRealtimeDataStructure().getRcData().length; i++) {
            getRealtimeDataStructure().setRcData(readWord(data), i);
        }
        if (SBGCProtocol.BOARD_VERSION_3) {

            for (int i = 0; i < 3; i++) {
                getRealtimeDataStructure().setAngle(
                        (readWord(data) * ANGLE_TO_DEGREE), i);

            }
            for (int i = 0; i < 3; i++) {
                getRealtimeDataStructure().setFrameAngle(
                        (readWord(data) * ANGLE_TO_DEGREE), i);
            }
            for (int i = 0; i < 3; i++)
                getRealtimeDataStructure().setRc_angle(
                        (readWord(data) * ANGLE_TO_DEGREE), i);
        } else {

            for (int i = 0; i < 3; i++) {
                getRealtimeDataStructure().setAngle(
                        (readWord(data) * ANGLE_TO_DEGREE), i);
            }
            for (int i = 0; i < 3; i++) {
                getRealtimeDataStructure().setRc_angle(
                        (readWord(data) * ANGLE_TO_DEGREE), i);
            }
        }

        getRealtimeDataStructure().setCycleTime(readWord(data));
        getRealtimeDataStructure().setI2cErrorCount(readWordUnsigned(data));
        getRealtimeDataStructure().setErrorCode(readByte(data));
        getRealtimeDataStructure().setBatteryValue(readWordUnsigned(data));
        getRealtimeDataStructure().setPowered(readByte(data) > 0);
        if (SBGCProtocol.BOARD_VERSION_3) {
            getRealtimeDataStructure().setCurrentIMU(readByte(data));
        }
        getRealtimeDataStructure().setCurrentProfile(readByte(data));
        for (int i = 0; i < 3; i++) {
            getRealtimeDataStructure().setPower(readByte(data), i);
        }
        // Reset position to first Data-Byte
        ProtocolUtil.readPosition = ProtocolUtil.BODY_DATA_POS;

        return false;
    }
}
