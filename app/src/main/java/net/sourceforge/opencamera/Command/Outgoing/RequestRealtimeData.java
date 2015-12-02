package net.sourceforge.opencamera.Command.Outgoing;

import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

/**
 * Created by KIMSEONHO on 2015-11-16.
 * CMD_REALTIME_DATA_3 == CMD_REALTIME_DATA - Request realtime data, response is CMD_REALTIME_DATA_3
 */
public class RequestRealtimeData implements ICommand {

    /*
    * Parses the received real-time data and saves it in the
    * RealtimeDataStructure
    *
    * @param data received data
    * @return
    */
    @Override
    public boolean execute(byte[] data) {
        ProtocolUtil.sendCommand(SBGCProtocol.CMD_REALTIME_DATA_3);		// change CMD_REALTIME_DATA to CMD_READTIME_DATA_3
        return true;
    }
}
