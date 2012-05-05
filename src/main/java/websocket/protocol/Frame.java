package websocket.protocol;

public interface Frame {
	public byte[] populate(byte[] frameData);
	public String getPayloadText();
	public byte[] getPayloadData();
	public byte[] getFrameBytes();
}
