package net.sourceforge.opencamera.BluetoothController;

public interface BlueToothInterface {

	//angle control..
	void turnClockWise(int yaw,int pitch);
	void turnCounterClockWise();
	void turnUp();
	void turnDown();
	void turnStartPoint();


	//connection..
	public void connect();
	void enableBluetooth();



	//network
	void receive();
	void send();
	void encodePacket();
	void decodePacket();






}
