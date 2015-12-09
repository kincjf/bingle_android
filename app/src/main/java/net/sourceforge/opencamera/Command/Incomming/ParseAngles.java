package net.sourceforge.opencamera.Command.Incomming;

import android.util.Log;

import net.sourceforge.opencamera.Command.CommandAction;
import net.sourceforge.opencamera.Command.ICommand;

import static net.sourceforge.opencamera.Data.Serial.ProtocolUtil.getConfirmValue;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * CMD_GET_ANGLES - Information about actual RC control state
 * It's not implement method, need to be implement!
 */
public class ParseAngles implements ICommand {

    @Override
    public boolean execute(byte[] data) {
        char val = getConfirmValue(data);
        Log.d(CommandAction.TAG, "CMD_GET_ANGLES command recv: " + val);

        return true;
    }
}
