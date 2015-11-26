package net.sourceforge.opencamera.Command.Outgoing;

import android.util.Log;

import net.sourceforge.opencamera.Command.CommandAction;
import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.Outgoing.ControlStructure;
import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by KIMSEONHO on 2015-11-16.
 */
public class RequestMoveGimbal implements ICommand {
    protected int oldYaw = 0;
    protected int turnCounter = 0;

    /**
     * @param data
     * @return
     */
    @Override
    public boolean execute(byte[] data) {
        if (data.length == 28) {
            ByteBuffer buff = ByteBuffer.wrap(data);
            buff.order(ByteOrder.LITTLE_ENDIAN);

            int roll = buff.getInt();
            int pitch = buff.getInt();
            int yaw = buff.getInt();
            int rollSpeed = buff.getInt();
            int pitchSpeed = buff.getInt();
            int yawSpeed = buff.getInt();
            int mode = buff.getInt();

            this.turnTo(roll, pitch, yaw, rollSpeed, pitchSpeed, yawSpeed, mode);

            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends a control command (angle mode) to the board with the given
     * parameters and given movement speed [degree/sec].
     *
     * Maps via 'turnTo(roll, pitch, yaw, rollSpeed, pitchSpeed, yawSpeed)' the
     * yaw range from 0 to 360 to the board's own mapping which is -720 to 720.
     * 720 are two full clockwise rotations, -720 are two full counterclockwise
     * rotations.
     *
     * @param roll
     *            [-90 to 90]
     * @param pitch
     *            [-90 to 90]
     * @param yaw
     *            [0 to 360]
     * @param rollSpeed
     *            [0-???]
     * @param pitchSpeed
     *            [0-???]
     * @param yawSpeed
     *            [0-???]
     */
    protected void turnTo(int roll, int pitch, int yaw, int rollSpeed,
                          int pitchSpeed, int yawSpeed, int mode) {

        String md = (mode == ProtocolUtil.MODE_ANGLE) ? "Angle" : "RC";
        Log.d(CommandAction.TAG, "Current Mode = " + md);

        // TODO decide on active profile, which mode to use
        if (mode == ProtocolUtil.MODE_ANGLE) {

            int yawDelta = (yaw - oldYaw);
            int goToYaw = 0;

            // Border Fix && direction
            if (yawDelta <= -180) {
                yawDelta += 360;
            } else if (yawDelta > 180) {
                yawDelta -= 360;
            }

            if (yawDelta < 0) {
                turnCounter -= Math.abs(yawDelta);
            } else {
                turnCounter += Math.abs(yawDelta);
            }
            if (turnCounter >= 1440)
                turnCounter -= 1440;

            // check if even or odd
            if ((turnCounter & 1) == 0) {
                // even...
                if (turnCounter <= 720) {
                    goToYaw = turnCounter % 720;
                } else {
                    goToYaw = (-720 + (turnCounter % 720));
                }
            } else {
                // odd...
                if (turnCounter > 720) {
                    goToYaw = (-720 + (turnCounter % 720));
                } else {
                    goToYaw = turnCounter % 720;
                }

            }
            this.sendTurnCommand(roll, pitch, goToYaw, SBGCProtocol.defaultTurnSpeed,
                    SBGCProtocol.defaultTurnSpeed, SBGCProtocol.defaultTurnSpeed, mode);
            oldYaw = yaw;
        } else if (mode == ProtocolUtil.MODE_RC) {

            int rcYaw = (int) (yaw * 500F / Math.abs((float) SBGCProtocol.profiles[0]
                    .getRcMaxAngleYaw()));
            // Pitch & Roll: assuming RcMin=RcMax
            int rcPitch = (int) (pitch * (500F / Math.abs((float) SBGCProtocol.profiles[0]
                    .getRcMaxAnglePitch())));
            int rcRoll = (int) (roll * (500F / Math.abs((float) SBGCProtocol.profiles[0]
                    .getRcMaxAngleRoll())));
            // 360deg mapping atm only for yaw
            if (yaw > 180) {
                // counterclockwise
                float yawAngleToRC = 500F / Math.abs((float) SBGCProtocol.profiles[0]
                        .getRcMinAngleYaw());
                float pitchAngleToRC = 500F / Math.abs((float) SBGCProtocol.profiles[0]
                        .getRcMinAnglePitch());
                rcYaw = (int) -((180 - (yaw - 180)) * yawAngleToRC);
                rcPitch = (int) (pitch * pitchAngleToRC);
            }

            Log.d(CommandAction.TAG, "RCYaw=" + rcYaw);
            this.sendTurnCommand(rcRoll, rcPitch, rcYaw, SBGCProtocol.defaultTurnSpeed,
                    SBGCProtocol.defaultTurnSpeed, SBGCProtocol.defaultTurnSpeed, mode);
        }
    }

    protected void sendTurnCommand(int roll, int pitch, int yaw, int rollSpeed,
                                   int pitchSpeed, int yawSpeed, int mode) {
        ControlStructure cCmd = new ControlStructure();

        cCmd.setMode(mode);

        cCmd.setAnglePitch(pitch);
        cCmd.setAngleRoll(roll);
        cCmd.setAngleYaw(yaw);

        cCmd.setSpeedPitch(pitchSpeed);
        cCmd.setSpeedRoll(rollSpeed);
        cCmd.setSpeedYaw(yawSpeed);

        ProtocolUtil.sendCommand(SBGCProtocol.CMD_CONTROL, cCmd.getByte());
    }
}
