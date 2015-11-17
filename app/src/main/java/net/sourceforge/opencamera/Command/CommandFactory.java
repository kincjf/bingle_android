package net.sourceforge.opencamera.Command;

import android.util.Log;

import net.sourceforge.opencamera.Command.Incomming.ParseAngles;
import net.sourceforge.opencamera.Command.Incomming.ParseBoardInfo;
import net.sourceforge.opencamera.Command.Incomming.ParseBoardParam;
import net.sourceforge.opencamera.Command.Incomming.ParseConfirmValue;
import net.sourceforge.opencamera.Command.Incomming.ParseRealtimeData;
import net.sourceforge.opencamera.Command.Outgoing.RequestBoardInfo;
import net.sourceforge.opencamera.Command.Outgoing.RequestBoardParams;
import net.sourceforge.opencamera.Command.Outgoing.RequestChangeProfile;
import net.sourceforge.opencamera.Command.Outgoing.RequestMotorOff;
import net.sourceforge.opencamera.Command.Outgoing.RequestMotorOn;
import net.sourceforge.opencamera.Command.Outgoing.RequestRealtimeData;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

/**
 * Created by KIMSEONHO on 2015-11-16.
 */
public class CommandFactory {
    public ICommand createIncommingCommand(byte[] data) {
        ICommand command = null;
        byte key = data[1];

        if (key == SBGCProtocol.CMD_BOARD_INFO) {
            command = new ParseBoardInfo();
        } else if (key == SBGCProtocol.CMD_READ_PARAMS) {
            command = new ParseBoardParam();
            Log.d(CommandAction.TAG, "CMD_READ_PARAMS command recv");
        } else if (key == SBGCProtocol.CMD_REALTIME_DATA) {
            SBGCProtocol.setVersion3(false);
            command = new ParseRealtimeData();
            Log.d(CommandAction.TAG, "CMD_REALTIME_DATA command recv");
        } else if (key == SBGCProtocol.CMD_REALTIME_DATA_3) {
            SBGCProtocol.setVersion3(true);
            command = new ParseRealtimeData();
            Log.d(CommandAction.TAG, "CMD_REALTIME_DATA_3 command recv");
        } else if (key == SBGCProtocol.CMD_CONFIRM) {
            command = new ParseConfirmValue();
        } else if (key == SBGCProtocol.CMD_GET_ANGLES) {
            command = new ParseAngles();
        } else {
            command = new NullCommand();
        }

        return command;
    }

    public ICommand createOutgoingCommand(byte key) {
        ICommand command = null;

        if (key == SBGCProtocol.CMD_BOARD_INFO ) {
            command = new RequestBoardInfo();
        } else if (key == SBGCProtocol.CMD_READ_PARAMS) {
            command = new RequestBoardParams();
        } else if (key == SBGCProtocol.CMD_EXECUTE_MENU) {
            command = new RequestChangeProfile();
        } else if (key == SBGCProtocol.CMD_REALTIME_DATA_3) {
            command = new RequestRealtimeData();
        } else if (key == SBGCProtocol.CMD_MOTORS_ON) {
            command = new RequestMotorOn();
        } else if (key == SBGCProtocol.CMD_MOTORS_OFF) {
            command = new RequestMotorOff();
        } else {
            command = new NullCommand();
        }

        return command;
    }
}

