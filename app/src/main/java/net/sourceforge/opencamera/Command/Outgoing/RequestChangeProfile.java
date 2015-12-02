package net.sourceforge.opencamera.Command.Outgoing;

import android.util.Log;

import net.sourceforge.opencamera.Command.CommandAction;
import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.SBGCProtocol;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.sendCommand;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * CMD_EXECUTE_MENU - Request execute menu command
 * menu에 등록된 명령을 실행함. 정해진 것은 아니며 조합에 따라 다름.
 */
public class RequestChangeProfile implements ICommand {
    @Override
    public boolean execute(byte[] data) {
        if(data.length == 1) {
            sendCommand(SBGCProtocol.CMD_EXECUTE_MENU, data);
            Log.d(CommandAction.TAG, "changeProfile(" + data[0] + ")");
            return true;
        } else {
            return false;
        }
    }
}
