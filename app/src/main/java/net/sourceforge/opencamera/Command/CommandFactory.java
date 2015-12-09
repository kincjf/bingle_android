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
import net.sourceforge.opencamera.Command.Outgoing.RequestMoveGimbal;
import net.sourceforge.opencamera.Command.Outgoing.RequestRealtimeData;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

/**
 * Created by KIMSEONHO on 2015-11-16.
 * key에 대한 command instance 생성을 관리함
 * (MVC2 패턴에서 front controller이라고 생각하면 될듯)
 */
public class CommandFactory {
    public ICommand createIncommingCommand(byte[] data) {
        ICommand command = null;
        byte key = data[1];

        switch (key) {
            case SBGCProtocol.CMD_BOARD_INFO:
                command = new ParseBoardInfo();
                break;
            case SBGCProtocol.CMD_READ_PARAMS:
                command = new ParseBoardParam();
                Log.d(CommandAction.TAG, "CMD_READ_PARAMS command recv");
                break;
            case SBGCProtocol.CMD_REALTIME_DATA:
                SBGCProtocol.setVersion3(false);
                Log.d(CommandAction.TAG, "CMD_REALTIME_DATA command recv");
            case SBGCProtocol.CMD_REALTIME_DATA_3:  // no response for command id CMD_REALTIME_DATA
                SBGCProtocol.setVersion3(true);
                command = new ParseRealtimeData();
                Log.d(CommandAction.TAG, "CMD_REALTIME_DATA_3 command recv");
                break;
            case SBGCProtocol.CMD_CONFIRM:
                command = new ParseConfirmValue();
                Log.d(CommandAction.TAG, "CMD_CONFIRM command recv");
                break;
            case SBGCProtocol.CMD_GET_ANGLES:
                command = new ParseAngles();
                Log.d(CommandAction.TAG, "CMD_GET_ANGLES command recv");
                break;
            default:
                command = new NullCommand();
                Log.d(CommandAction.TAG, "NULL command recv");
                break;
        }

        return command;
    }

    public ICommand createOutgoingCommand(byte key) {
        ICommand command = null;

        switch (key) {
            case SBGCProtocol.CMD_BOARD_INFO:
                command = new RequestBoardInfo();
                break;
            case SBGCProtocol.CMD_READ_PARAMS:
                command = new RequestBoardParams();
                break;
            case SBGCProtocol.CMD_EXECUTE_MENU:
                command = new RequestChangeProfile();
                break;
            case SBGCProtocol.CMD_REALTIME_DATA_3:
                command = new RequestRealtimeData();
                break;
            case SBGCProtocol.CMD_MOTORS_ON:
                command = new RequestMotorOn();
                break;
            case SBGCProtocol.CMD_MOTORS_OFF:
                command = new RequestMotorOff();
                break;
            case SBGCProtocol.CMD_CONTROL:
                command = new RequestMoveGimbal();
                break;
            default:
                command = new NullCommand();
                break;
        }

        return command;
    }
}

