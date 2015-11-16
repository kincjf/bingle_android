package net.sourceforge.opencamera.Command;

/**
 * Created by KIMSEONHO on 2015-11-16.
 */
public interface ICommand {
    /**
     *
     * @param data
     * @return
     */
    public boolean execute(byte[] data);
}
