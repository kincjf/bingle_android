package net.sourceforge.opencamera.Command.Outgoing;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.sendCommand;

/**
 * Created by KIMSEONHO on 2015-11-16.
 */
public class RequestMotorOn implements ICommand {
    @Override
    public boolean execute(byte[] data) {
        sendCommand(SBGCProtocol.CMD_MOTORS_ON);
        return true;
    }
}
