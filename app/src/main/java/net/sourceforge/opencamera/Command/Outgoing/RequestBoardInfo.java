package net.sourceforge.opencamera.Command.Outgoing;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * CMD_BOARD_INFO - version and board info information
 */
public class RequestBoardInfo implements ICommand {

    @Override
    public boolean execute(byte[] data) {
        if (data.length == 0) {
            ProtocolUtil.sendCommand(SBGCProtocol.CMD_BOARD_INFO);
        } else {
            ProtocolUtil.sendCommand(SBGCProtocol.CMD_BOARD_INFO, data);
        }

        return true;
    }
}
