package net.sourceforge.opencamera.Command;

import net.sourceforge.opencamera.Data.Serial.ProtocolUtil;

/**
 * Incomming, Outgoing 명령을 command pattern으로 구현함
 * 현재는 Outgoing Action 몀령시 parameter를 byte로 했음.
 * 그래서 불필요한 bytebuffer를 사용하고 변환을 하고 있음. 수정 필요
 * (generic을 잘 모르겠음, 향후 vo data를 이용할 수 있게 구현해야함)
 * Created by KIMSEONHO on 2015-11-16.
 */
public class CommandAction {
    public static final String TAG = "COMMAND_ACTION";
    private CommandFactory factory = new CommandFactory();

    /**
     * board에서 전송된 패킷을 처리함
     * @param data
     * @return
     */
    public boolean IncommandAction(byte[] data) {
        if (data.length > 0 && ProtocolUtil.verifyChecksum(data)) {
            ICommand command = factory.createIncommingCommand(data);
            return command.execute(data);
        } else {
            return false;
        }
    }

    /**
     * board로 보낼 패킷을 처리함
     * @param commandId
     * @param data parameter(optional), parameter가 없을 경우 byte[0]을 보내줘야 함.
     */
    public void OutgoingAction(byte commandId, byte[] data) {
        ICommand command = factory.createOutgoingCommand(commandId);
        command.execute(data);
    }

    /**
     * request param이 없는 경우 활용
     * equals to OutGoing(commandId, new byte[0])
     * @param commandId
     */
    public void OutgoingAction(byte commandId) {
        ICommand command = factory.createOutgoingCommand(commandId);
        command.execute(new byte[0]);
    }
}
