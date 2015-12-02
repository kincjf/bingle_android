package net.sourceforge.opencamera.Command.Incomming;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.InComming.ProfileStructure;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readBoolean;
import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readByte;
import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readByteSigned;
import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.readWord;

/**
 * Created by KIMSEONHO on 2015-11-16.
 * CMD_READ_PARAM - Receive parameters for single profile together with general parameters
 */
public class ParseBoardParam implements ICommand {

    @Override
    public boolean execute(byte[] data) {
        int profileId = data[0];
        ProfileStructure p = SBGCProtocol.profiles[profileId];

        for (int i = 0; i < 3; i++) {
            p.P[i] = readByte(data);
            p.I[i] = readByte(data);
            p.D[i] = readByte(data);
            p.power[i] = readByte(data);
            p.invert[i] = readBoolean(data);
            p.poles[i] = readByte(data);
        }
        p.accLimiter = readByte(data);

        p.extFcGainPitch = readByteSigned(data);
        p.extFcGainPitch = readByteSigned(data);

        for (int i = 0; i < 3; i++) {
            p.rcMinAngle[i] = readWord(data);
            p.rcMaxAngle[i] = readWord(data);
            p.rcMode[i] = readByte(data);
            p.rcLpf[i] = readByte(data);
            p.rcSpeed[i] = readByte(data);
            p.rcFollow[i] = readByteSigned(data);
        }

        p.gyroTrust = readByte(data);
        p.useModel = readBoolean(data);
        p.pwmFreq = readByte(data);
        p.serialSpeed = readByte(data);
        p.rcTrimRoll = readByteSigned(data);
        p.rcTrimPitch = readByteSigned(data);
        p.rcTrimYaw = readByteSigned(data);

        p.rcDeadband = readByte(data);
        p.rcExpoRate = readByte(data);
        p.rcVirtMode = readByte(data);
        p.rcMapRoll = readByte(data);

        p.rcMapPitch = readByte(data);
        p.rcMapYaw = readByte(data);
        p.rcMapCmd = readByte(data);
        p.rcMapFcRoll = readByte(data);
        p.rcMapFcPitch = readByte(data);
        p.rcMixFcRoll = readByte(data);
        p.rcMixFcPitch = readByte(data);

        p.followMode = readByte(data);
        p.followDeadband = readByte(data);
        p.followExpoRate = readByte(data);

        p.followOffsetRoll = readByteSigned(data);
        p.followOffsetPitch = readByteSigned(data);
        p.followOffsetYaw = readByteSigned(data);

        p.axisTop = readByteSigned(data);
        p.axisRight = readByteSigned(data);
        if (SBGCProtocol.BOARD_VERSION_3) {
            p.frameAxisTop = readByteSigned(data);
            p.frameAxisRight = readByteSigned(data);
            p.frameImuPos = readByte(data);
        }
        p.gyroLpf = readByte(data);
        p.gyroSens = readByte(data);
        p.i2cInternalPullups = readBoolean(data);
        p.skipGyroCalib = readBoolean(data);

        p.rcCmdLow = readByte(data);
        p.rcCmdMid = readByte(data);
        p.rcCmdHigh = readByte(data);

        for (int i = 0; i < p.menuCmd.length; i++) {
            p.menuCmd[i] = readByte(data);
        }
        p.menuCmdLong = readByte(data);

        p.outputRoll = readByte(data);
        p.outputPitch = readByte(data);
        p.outputYaw = readByte(data);

        p.batThresholdAlarm = readWord(data);
        p.batThresholdMotors = readWord(data);
        p.batCompRef = readWord(data);
        p.beeperModes = readByte(data);
        p.followRollMixStart = readByte(data);
        p.followRollMixRange = readByte(data);

        p.boosterPowerRoll = readByte(data);
        p.boosterPowerPitch = readByte(data);
        p.boosterPowerYaw = readByte(data);

        p.followSpeedRoll = readByte(data);
        p.followSpeedPitch = readByte(data);
        p.followSpeedYaw = readByte(data);

        p.frameAngleFromMotors = readBoolean(data);

        if (SBGCProtocol.BOARD_VERSION_3) {
            for (int i = 0; i < 25; i++) {
                p.reservedBytes[i] = readByte(data);
            }
            p.curIMU = readByte(data);
        }

        p.curProfileId = readByte(data);

        return true;
    }
}
