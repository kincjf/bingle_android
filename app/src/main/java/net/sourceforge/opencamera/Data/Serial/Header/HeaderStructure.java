package net.sourceforge.opencamera.Data.Serial.Header;

/**
 * Created by WG on 2015-11-13.
 */
public class HeaderStructure {
    public static final String TAG = "HeaderStructure";

    //CommandId Start
    public static final byte CmdReadParams = 82;
    public static final byte CmdWriteParams = 87;
    public static final byte CmdRealtimeData = 68;
    public static final byte CmdBoardInfo = 86;
    public static final byte CmdCalibAcc = 65;
    public static final byte CmdCalibGyro = 103;
    public static final byte CmdCalibExtGain = 71;
    public static final byte CmdUseDefaults = 70;
    public static final byte CmdCalibPoles = 80;
    public static final byte CmdReset = 114;
    public static final byte CmdHelperData = 72;
    public static final byte CmdCalibOffset = 79;
    public static final byte CmdCalibBat = 66;
    public static final byte CmdMotorsOn = 77;
    public static final byte CmdMotorsOff = 109;
    public static final byte CmdControl = 67;
    public static final byte CmdTriggerPin = 84;
    public static final byte CmdExecuteMenu = 69;
    public static final byte CmdGetAngles = 73;
    public static final byte CmdConfirm = 67;

    // Board v3.x only
    public static final byte CmdBoardInfo3 = 20;
    public static final byte CmdReadParams3 = 21;
    public static final byte CmdWriteParams3 = 22;
    public static final byte CmdRealtimeData3 = 23;
    public static final byte CmdRealtimeData4 = 25;
    public static final byte CmdSelectImu3 = 24;
    public static final byte CmdReadProfileNames = 28;
    public static final byte CmdWriteProfileNames = 29;
    public static final byte CmdQueueParamsInfo3 = 30;
    public static final byte CmdSetAdjVarsVal = 31;
    public static final byte CmdSaveParams3 = 32;
    public static final byte CmdReadParamsExt = 33;
    public static final byte CmdWriteParamsExt = 34;
    public static final byte CmdAutoPid = 35;
    public static final byte CmdServoOut = 36;
    public static final byte CmdI2cWriteRegBuf = 39;
    public static final byte CmdI2cReadRegBuf = 40;
    public static final byte CmdWirteExternalData = 41;
    public static final byte CmdReadExternalData = 42;
    public static final byte CmdReadAdjVarsCfg = 43;
    public static final byte CmdWriteAdjVarsCfg = 44;
    public static final byte CmdApiVirtChControl = 45;
    public static final byte CmdAdjVarsState = 46;
    public static final byte CmdEepromWrite = 47;
    public static final byte CmdEepromRead = 48;
    public static final byte CmdBootMode3 = 51;
    public static final byte CmdReadFile = 53;
    public static final byte CmdWriteFile = 54;
    public static final byte CmdFsClearAll = 55;
    public static final byte CmdAhrsHelper = 56;
    public static final byte CmdRunScript = 57;
    public static final byte CmdScriptDebug = 58;
    public static final byte CmdCalibMag = 59;
    public static final byte CmdGetAnglesExt = 61;
    public static final byte CmdReadParamsExt2 = 62;
    public static final byte CmdWriteParamsExt2 = 63;
    public static final byte CmdGetAdjVarsVal = 64;
    public static final byte CmdDebugVarsInfo3 = (byte) 253;
    public static final byte CmdDebugVars3 = (byte) 254;
    public static final byte CmdError = (byte) 255;
    //CommandId End

    public static final byte CharacterByte = '>';

    // Header fixed data[] positions
    public static final int CharacterBytePos = 0;
    public static final int CommandIdPos = 1;
    public static final int DataSizePos = 2;
    public static final int HeaderChecksumPos = 3;

}
