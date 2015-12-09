package net.sourceforge.opencamera.Command.Incomming;

import android.util.Log;

import net.sourceforge.opencamera.Command.CommandAction;
import net.sourceforge.opencamera.Command.ICommand;
import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * CMD_BOARD_INFO - version and board info information
 */
public class ParseBoardInfo implements ICommand {

    @Override
    public boolean execute(byte[] data) {
        String boardI[] = ProtocolUtil.getFirmwareVersion(data).split("v");

        String boardFirmware = boardI[0];
        int boardVersion = Integer.parseInt(boardI[1]);

        if (boardVersion == 3) {
//            setVersion3(true);
        }

        Log.d(CommandAction.TAG, "CMD_BOARD_INFO command recv: " + boardFirmware
                + " boardVersion:" + boardVersion);

        return true;
    }
}