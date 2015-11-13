package net.sourceforge.opencamera.Data.Serial.Header;

/**
 * Created by WG on 2015-11-13.
 */
public class HeaderCommand {
    private static final String TAG = "CommandId";

    private static final byte CmdReadParams = 82;
    private static final byte CmdWriteParams = 87;
    private static final byte CmdRealtimeData = 68;
    private static final byte CmdBoardInfo = 86;
    private static final byte CmdCalibAcc = 65;
    private static final byte CmdCalibGyro = 103;
    private static final byte CmdCalibExtGain = 71;
    private static final byte CmdUseDefaults = 70;
    private static final byte CmdCalibPoles = 80;
    private static final byte CmdReset = 114;
    private static final byte CmdHelperData = 72;
    private static final byte CmdCalibOffset = 79;
    private static final byte CmdCalibBat = 66;
    private static final byte CmdMotorsOn = 77;
    private static final byte CmdMotorsOff = 109;
    private static final byte CmdControl = 67;
    private static final byte CmdTriggerPin = 84;
    private static final byte CmdExecuteMenu = 69;
    private static final byte CmdGetAngles = 73;
    private static final byte CmdConfirm = 67;

    // Board v3.x only
    private static final byte CmdBoardInfo3 = 20;
    private static final byte CmdReadParams3 = 21;
    private static final byte CmdWriteParams3 = 22;
    private static final byte CmdRealtimeData3 = 23;
    private static final byte CmdRealtimeData4 = 25;
    private static final byte CmdSelectImu3 = 24;
    private static final byte CmdReadProfileNames = 28;
    private static final byte CmdWriteProfileNames = 29;
    private static final byte CmdQueueParamsInfo3 = 30;
    private static final byte CmdSetAdjVarsVal = 31;
    private static final byte CmdSaveParams3 = 32;
    private static final byte CmdReadParamsExt = 33;
    private static final byte CmdWriteParamsExt = 34;
    private static final byte CmdAutoPid = 35;
    private static final byte CmdServoOut = 36;
    private static final byte CmdI2cWriteRegBuf = 39;
    private static final byte CmdI2cReadRegBuf = 40;
    private static final byte CmdWirteExternalData = 41;
    private static final byte CmdReadExternalData = 42;
    private static final byte CmdReadAdjVarsCfg = 43;
    private static final byte CmdWriteAdjVarsCfg = 44;
    private static final byte CmdApiVirtChControl = 45;
    private static final byte CmdAdjVarsState = 46;
    private static final byte CmdEepromWrite = 47;
    private static final byte CmdEepromRead = 48;
    private static final byte CmdBootMode3 = 51;
    private static final byte CmdReadFile = 53;
    private static final byte CmdWriteFile = 54;
    private static final byte CmdFsClearAll = 55;
    private static final byte CmdAhrsHelper = 56;
    private static final byte CmdRunScript = 57;
    private static final byte CmdScriptDebug = 58;
    private static final byte CmdCalibMag = 59;
    private static final byte CmdGetAnglesExt = 61;
    private static final byte CmdReadParamsExt2 = 62;
    private static final byte CmdWriteParamsExt2 = 63;
    private static final byte CmdGetAdjVarsVal = 64;
    private static final byte CmdDebugVarsInfo3 = (byte) 253;
    private static final byte CmdDebugVars3 = (byte) 254;
    private static final byte CmdError = (byte) 255;

    private static final byte CharacterByte = '>';
}
