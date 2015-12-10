package net.sourceforge.opencamera.Data.Serial;

import android.util.Log;

import net.sourceforge.opencamera.Command.CommandAction;
import net.sourceforge.opencamera.Data.Serial.InComming.ProfileStructure;
import net.sourceforge.opencamera.Data.Serial.InComming.RealtimeDataStructure;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * Simple BGC(Board) Serial Communication을 쉽게 할 수 있도록
 * command별 interface를 구성함
 */
public class SBGCProtocol extends ProtocolUtil {
    private static final String TAG = "SBGC_Protocol";

    public static CommandAction action = new CommandAction();

    protected static RealtimeDataStructure realtimeData = RealtimeDataStructure.getRealtimeData();

    public static ProfileStructure[] profiles = { new ProfileStructure(),
            new ProfileStructure(), new ProfileStructure() };

    public static final byte CMD_READ_PARAMS = 'R';
    protected static final byte CMD_WRITE_PARAMS = 'W';
    public static final byte CMD_REALTIME_DATA = 'D';
    public static final byte CMD_BOARD_INFO = 'V';
    protected static final byte CMD_CALIB_ACC = 'A';
    protected static final byte CMD_CALIB_GYRO = 'g';
    protected static final byte CMD_CALIB_EXT_GAIN = 'G';
    protected static final byte CMD_USE_DEFAULTS = 'F';
    protected static final byte CMD_CALIB_POLES = 'P';
    protected static final byte CMD_RESET = 'r';
    protected static final byte CMD_HELPER_DATA = 'H';
    protected static final byte CMD_CALIB_OFFSET = 'O';
    protected static final byte CMD_CALIB_BAT = 'B';
    public static final byte CMD_MOTORS_ON = 'M';
    public static final byte CMD_MOTORS_OFF = 'm';
    public static final byte CMD_CONTROL = 'C';
    protected static final byte CMD_TRIGGER_PIN = 'T';
    public static final byte CMD_EXECUTE_MENU = 'E';
    public static final byte CMD_GET_ANGLES = 'I';
    public static final byte CMD_CONFIRM = 'C';

    // Board v3.x only (not tested!)
    protected static final byte CMD_BOARD_INFO_3 = 20;
    protected static final byte CMD_READ_PARAMS_3 = 21;
    protected static final byte CMD_WRITE_PARAMS_3 = 22;
    public static final byte CMD_REALTIME_DATA_3 = 23;
    protected static final byte CMD_REALTIME_DATA_4 = 25;
    protected static final byte CMD_SELECT_IMU_3 = 24;
    protected static final byte CMD_READ_PROFILE_NAMES = 28;
    protected static final byte CMD_WRITE_PROFILE_NAMES = 29;
    protected static final byte CMD_QUEUE_PARAMS_INFO_3 = 30;
    protected static final byte CMD_SET_ADJ_VARS_VAL = 31;
    protected static final byte CMD_SAVE_PARAMS_3 = 32;
    protected static final byte CMD_READ_PARAMS_EXT = 33;
    protected static final byte CMD_WRITE_PARAMS_EXT = 34;
    protected static final byte CMD_AUTO_PID = 35;
    protected static final byte CMD_SERVO_OUT = 36;
    protected static final byte CMD_I2C_WRITE_REG_BUF = 39;
    protected static final byte CMD_I2C_READ_REG_BUF = 40;
    protected static final byte CMD_WRITE_EXTERNAL_DATA = 41;
    protected static final byte CMD_READ_EXTERNAL_DATA = 42;
    protected static final byte CMD_READ_ADJ_VARS_CFG = 43;
    protected static final byte CMD_WRITE_ADJ_VARS_CFG = 44;
    protected static final byte CMD_API_VIRT_CH_CONTROL = 45;
    protected static final byte CMD_ADJ_VARS_STATE = 46;
    protected static final byte CMD_EEPROM_WRITE = 47;
    protected static final byte CMD_EEPROM_READ = 48;
    protected static final byte CMD_BOOT_MODE_3 = 51;
    protected static final byte CMD_READ_FILE = 53;
    protected static final byte CMD_WRITE_FILE = 54;
    protected static final byte CMD_FS_CLEAR_ALL = 55;
    protected static final byte CMD_AHRS_HELPER = 56;
    protected static final byte CMD_RUN_SCRIPT = 57;
    protected static final byte CMD_SCRIPT_DEBUG = 58;
    protected static final byte CMD_CALIB_MAG = 59;
    protected static final byte CMD_GET_ANGLES_EXT = 61;
    protected static final byte CMD_READ_PARAMS_EXT2 = 62;
    protected static final byte CMD_WRITE_PARAMS_EXT2 = 63;
    protected static final byte CMD_GET_ADJ_VARS_VAL = 64;
    protected static final byte CMD_DEBUG_VARS_INFO_3 = (byte) 253;
    protected static final byte CMD_DEBUG_VARS_3 = (byte) 254;
    protected static final byte CMD_ERROR = (byte) 255;

    protected static final byte MAGIC_BYTE = '>';
    public static boolean BOARD_VERSION_3 = false;

    protected static int currentMode = 0;
    static String boardFirmware = "unknown";
    static int boardVersion = 0;
    public static int defaultTurnSpeed = 30;
    /**
     * Requests the board information (firmware version)
     */
    public static boolean initSBGCProtocol() {

        requestBoardInfo();

        // we are starting in RC Mode
//		setCurrentMode(MODE_RC);
        setCurrentMode(MODE_ANGLE);

        return (boardFirmware != "unknown");
    }

    /**
     * helper function for waiting a certain time
     * */
    public static void wait(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * [WRAPPER FOR turnTo()] Sends a control command (angle mode) to the board
     * with the given parameters, movement speed uses default settings [30
     * degree/sec].
     *
     * Maps via 'turnTo(roll, pitch, yaw)' the yaw range from 0 to 360 to the
     * board's own mapping which is -720 to 720. 720 are two full clockwise
     * rotations, -720 are two full counterclockwise rotations.
     *
     * @param roll
     *            [-90 to 90]
     * @param pitch
     *            [-90 to 90]
     * @param yaw
     *            [0 to 360]
     */
    public static void requestMoveGimbalTo(int roll, int pitch, int yaw) {
        requestMoveGimbalTo(roll, pitch, yaw,
                defaultTurnSpeed, defaultTurnSpeed, defaultTurnSpeed, currentMode);
    }


    /**
     * [WRAPPER FOR turnTo()] Sends a control command (angle mode) to the board
     * with the given parameters and given movement speed [degree/sec].
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
    public static void requestMoveGimbalTo(int roll, int pitch, int yaw,
                                    int rollSpeed, int pitchSpeed, int yawSpeed, int mode) {
        ByteBuffer buff = ByteBuffer.allocate((Integer.SIZE / 8) * 7);
        buff.order(ByteOrder.LITTLE_ENDIAN);

        buff.putInt(roll);
        buff.putInt(pitch);
        buff.putInt(yaw);
        buff.putInt(rollSpeed);
        buff.putInt(pitchSpeed);
        buff.putInt(yawSpeed);
        buff.putInt(mode);
        buff.flip();

        action.OutgoingAction(CMD_CONTROL, buff.array());
    }

    /**
     * Requests board information like firmware
     */
    public static void requestBoardInfo() {
        action.OutgoingAction(CMD_BOARD_INFO);
    }

    /**
     * Requests board information like firmware
     */
    public static void requestReadParams(int profileID) {
        byte profileByte[] = new byte[1];
        profileByte[0] = (byte) (profileID);
        action.OutgoingAction(CMD_READ_PARAMS, profileByte);
    }

    /**
     * Requests board information Extended format
     * CFG - 2b, ms > 0
     */
    public static void requestBoardInfo(int ms) {
        byte[] word = new byte[2];
        word[0] = (byte) ((short) ms & 0xffff);
        word[1] = (byte) (((short) ms >>> 8) & 0xffff);

        action.OutgoingAction(CMD_BOARD_INFO, word);
    }

    /**
     * Requests board params like profiles
     */
    public static void requestBoardParams() {
        action.OutgoingAction(CMD_READ_PARAMS);
    }

    /**
     * Returns the current active board profile
     *
     * @return active profile
     */
    public int getActiveProfile() {
        return getRealtimeDataStructure().getCurrentProfile();
    }

    /**
     * Sends a command to the board to switch to the given profile number
     *
     * @param profileID
     *            profile number [1, 2 or 3]
     */
    public void requestSwitchToProfile(int profileID) {
        changeProfile(profileID);
        Log.d(TAG, "ProfileChange");
    }

    /**
     * Sends a command to the board to switch to the first profile
     */
    public void requestSwitchToFirstProfile() {
        changeProfile(1);
    }

    /**
     * Sends a command to the board to switch to the second profile
     */
    public void requestSwitchToSecondProfile() {
        changeProfile(2);
    }

    /**
     * Sends a command to the board to switch to the third profile
     */
    public void requestSwitchToThirdProfile() {
        changeProfile(3);
    }

    /**
     * Sends a command to the board to turn on the motors
     */
    public void requestMotorOn() {
        action.OutgoingAction(CMD_MOTORS_ON);
    }

    /**
     * Sends a command to the board to turn off the motors
     */
    public void requestMotorOff() {
        action.OutgoingAction(CMD_MOTORS_OFF);
    }

    /**
     * Requests the board current parameters
     */
    public void requestBoardParameters() {
        action.OutgoingAction(CMD_READ_PARAMS);
    }

    /**
     * Requests real-time sensor data, should be polled in a continuous loop at
     * a specific frequency
     */
    public void requestRealtimeData() {
        action.OutgoingAction(CMD_REALTIME_DATA_3);		// change CMD_REALTIME_DATA to CMD_READTIME_DATA_3
    }

    /**
     * changes the profile
     * @param profileID number of the profile [1,2,3]
     *
     **/
    protected void changeProfile(int profileID) {
        byte profileByte[] = new byte[1];
        profileByte[0] = (byte) (profileID);
        Log.d(TAG, "changeProfile(" + profileID + ")");
        action.OutgoingAction(CMD_EXECUTE_MENU, profileByte);
    }

    /**
     * Getter for RealtimeDataStructure
     * @return RealtimeDataStructure
     */
    public static RealtimeDataStructure getRealtimeDataStructure() {
        return realtimeData;
    }

    /**
     * Setter for RealtimeDataStructure
     * @param struc RealtimeDataStructure
     */
    public static void setRealtimeDataStructure(RealtimeDataStructure struc) {
        realtimeData = struc;
    }

    public static boolean isVersion3() {
        return BOARD_VERSION_3;
    }
    public static void setVersion3(boolean isVersion3) {
        BOARD_VERSION_3 = isVersion3;
    }
    public int getCurrentMode() {
        return currentMode;
    }

    public static void setCurrentMode(int mode) {
        currentMode = mode;
    }
    public static void setCurrentModeRC() {
        currentMode = MODE_RC;
    }
    public static void setCurrentModeAngle() {
        currentMode = MODE_ANGLE;
    }

}