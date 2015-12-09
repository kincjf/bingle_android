package net.sourceforge.opencamera.Command.Outgoing;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.sendCommand;

/**
 * Created by KIMSEONHO on 2015-11-16.
 * CMD_READ_PARAMS == CMD_READ_PARAMS_3 - Request parameter for board
 */
public class RequestBoardParams implements ICommand {

    @Override
    public boolean execute(byte[] data) {
        boolean executed = false;

        if (data.length == 1) {
            sendCommand(SBGCProtocol.CMD_READ_PARAMS, data);
            executed = true;
        } else {
            sendCommand(SBGCProtocol.CMD_READ_PARAMS);
            executed = true;
        }

        return executed;
    }
}
