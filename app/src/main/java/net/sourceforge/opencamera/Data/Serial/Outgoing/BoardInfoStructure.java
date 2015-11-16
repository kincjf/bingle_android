package net.sourceforge.opencamera.Data.Serial.Outgoing;

import net.sourceforge.opencamera.Data.Serial.IStructure;

/**
 * Created by KIMSEONHO on 2015-11-17.
 * CMD_BOARD_INFO - request board and firmware information
 * CFG - 2b, ms > 0
 */
public class BoardInfoStructure implements IStructure {
    public short ms;

    public BoardInfoStructure(short ms) {
        this.ms = ms;
    }

    @Override
    public byte[] getByte() {
        byte[] word = new byte[2];
        word[0] = (byte) ((short) ms & 0xffff);
        word[1] = (byte) (((short) ms >>> 8) & 0xffff);

        return word;
    }
}
