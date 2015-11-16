package net.sourceforge.opencamera.Data.Serial;

import android.util.Log;

import net.sourceforge.opencamera.Data.Serial.Outgoing.ControlCommandStructure;
import net.sourceforge.opencamera.Data.Serial.InComming.ProfileStructure;
import net.sourceforge.opencamera.Data.Serial.InComming.RealtimeDataStructure;

import java.io.IOException;

/**
 * @author Author: KIMSEONHO, PastelPlus
 * Referenced by https://github.com/fxpal-polly/Android-SimpleBGC
 * All rights reserved.
 * @see SimpleBGC 2.5 serial protocol rev. 0.8
 */
public class ProtocolUtil {
	private static final String TAG = "ProtocolUtil";

	protected static final float ANGLE_TO_DEGREE = 0.02197266F;

	public static final int MODE_NO_CONTROL = 0;
	public static final int MODE_SPEED = 1;
	public static final int MODE_ANGLE = 2;
	public static final int MODE_SPEED_ANGLE = 3;
	public static final int MODE_RC = 4;

	// fixed data[] positions
	protected static final int MAGIC_BYTE_POS = 0;
	protected static final int COMMAND_ID_POS = 1;
	protected static final int DATA_SIZE_POS = 2;
	protected static final int HEADER_CHECKSUM_POS = 3;
	public static final int BODY_DATA_POS = 4;

	public static int readPosition = BODY_DATA_POS; // must be 4 because of
														// the header

	/**
	 * Reads the next word in the data array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return read bytes or -1 on failure
	 */
	public static int readWord(byte[] data) {
		if (data.length >= readPosition + 2) {
			return (data[(readPosition++)] & 0xFF)
					+ (data[(readPosition++)] << 8);
		}
		return -1;
	}

	/**
	 * Reads the next unsigned word in the data array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return read bytes or -1 on failure
	 */
	public static int readWordUnsigned(byte[] data) {
		if (data.length >= readPosition + 2) {
			return (data[(readPosition++)] & 0xFF)
					+ ((data[(readPosition++)] & 0xFF) << 8);
		}
		return -1;

	}

	/**
	 * Reads the next byte in the data array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return read byte or -1 on failure
	 */
	public static int readByte(byte[] data) {
		if (readPosition < data.length) {
			return data[(readPosition++)] & 0xFF;
		}
		return -1;

	}

	/**
	 * Reads the next signed byte in the data array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return read byte or -1 on failure
	 * @throws IOException
	 */
	public static int readByteSigned(byte[] data) {

		if (readPosition < data.length) {
			return data[(readPosition++)];
		}

		return -1;

	}

	public static boolean readBoolean(byte[] data) {
		return readByte(data) == 1;
	}

	/**
	 * Returns a (readable) String representation of the byte array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return bytes as a String
	 */
	static String byteArrayToString(byte[] data) {
		StringBuilder sb = new StringBuilder();
		for (byte b : data) {

			sb.append(Integer.toString(b));
		}
		return sb.toString();
	}

	/**
	 * Basic wrapper function for commands without payload
	 *
	 * @param commandID
	 *            command to send
	 */
	public static void sendCommand(byte commandID) {

		sendCommand(commandID, new byte[0]);
	}

	/**
	 * Verifies the checksum of the given data array
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return true if valid, else false
	 */
	public static boolean verifyChecksum(byte[] data) {
		if (data.length <= 4)
			return false;

		boolean headerOK = false;
		boolean bodyOK = false;

		if (data[MAGIC_BYTE_POS] == SBGCProtocol.MAGIC_BYTE
				&& ((int) (0xff & data[COMMAND_ID_POS]) + (int) (0xff & data[DATA_SIZE_POS])) % 256 == (0xff & data[HEADER_CHECKSUM_POS])) {
			headerOK = true;
		} else {
			Log.d(TAG, "verifyChecksum(): HEADER BAD");
		}

		int bodyChksm = 0;
		for (int i = 4; i < data.length - 1; i++) {
			bodyChksm += (0xff & data[i]);
		}

		if ((bodyChksm % 256) == (0xff & data[data.length - 1])) {
			bodyOK = true;
		} else {
			Log.d(TAG, "verifyChecksum(): BODY BAD");
		}

		return (headerOK && bodyOK);
	}

	/**
	 * This method should be used to send the commands it takes care of the
	 * whole header & checksum things
	 * 
	 * @param commandID
	 *            is the Command ID character
	 * 
	 * @param rawData
	 *            is the raw data / payload
	 * 
	 */
	public static void sendCommand(byte commandID, byte rawData[]) {
		byte bodyDataSize = (byte) rawData.length;
		byte headerChecksum = (byte) (((int) commandID + (int) bodyDataSize) % 256);
		int rawBodyChecksum = 0;
		int cnt = 0;

		do {
			if (cnt >= bodyDataSize) {		// BodyChecksum을 계산한 뒤에 마지막에 header를 합쳐서 보냄
				byte bodyChecksum = (byte) (rawBodyChecksum % 256);
				byte headerArray[] = new byte[4];

				headerArray[MAGIC_BYTE_POS] = SBGCProtocol.MAGIC_BYTE;		// ">"
				headerArray[COMMAND_ID_POS] = (byte) (commandID & 0xff);
				headerArray[DATA_SIZE_POS] = (byte) (bodyDataSize & 0xff);
				headerArray[HEADER_CHECKSUM_POS] = (byte) (headerChecksum & 0xff);

				byte headerAndBodyArray[] = new byte[1 + (headerArray.length + rawData.length)];
				System.arraycopy(headerArray, 0, headerAndBodyArray, 0,
						headerArray.length);
				System.arraycopy(rawData, 0, headerAndBodyArray,
						headerArray.length, rawData.length);
				headerAndBodyArray[headerArray.length + rawData.length] = (byte) (bodyChecksum & 0xff);

				if (verifyChecksum(headerAndBodyArray)) {
					// connector 부분만 연결해주기
					SBGCConnector.bluetooth.sendViaBT(headerAndBodyArray);
				} else {
					Log.d(TAG, "Bad Checksum: "
							+ byteArrayToString(headerAndBodyArray));
				}
				return;
			}
			rawBodyChecksum += rawData[cnt];
			cnt++;
		} while (true);
	}

	/**
	 * Returns the confirmation value Not error safe, should only be called when
	 * a confirmation is received
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return confirmation values
	 */
	public static char getConfirmValue(byte[] data) {

		char x = (char) (data[data.length - 2] & 0x000000FF);
		return x;
	}

	/**
	 * Returns the firmware version
	 * 
	 * @param data
	 *            complete data array [header+body]
	 * @return firmware versions
	 */
	public static String getFirmwareVersion(byte[] data) {
		if (data.length < 6)
			return "ERROR";

		int boardVersion = (data[4] & 0xFF) / 10;

		int index = 1;
		byte[] buffer = { data[5], data[6] };
		int first = ((int) (buffer[index--])) & 0x000000FF;
		int second = ((int) (buffer[index--])) & 0x000000FF;
		int ushort = ((int) first << 8 | second) & 0xFFFF;
		String rawVer = Integer.toString(ushort);

		String ver = rawVer.substring(0, 1) + "." + rawVer.substring(1, 3)
				+ "b" + rawVer.substring(3, 4) + "v" + boardVersion;
		return ver;
	}
}
