package net.sourceforge.opencamera.Command;

/**
 * Created by KIMSEONHO on 2015-11-17.
 */
public class NoCommand implements ICommand {
    @Override
    public boolean execute(byte[] data) {
        return false;
    }
}
