package websocket.protocol.hixie;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import websocket.protocol.Frame;


public class Hixie76Frame implements Frame {
	
	public Hixie76Frame(){}
	public Hixie76Frame(byte[] frameData){
		populate(frameData);
	}
	public void setPayloadData(byte[] payloadData) {
		this.payloadData = payloadData;
	}
	public void setPayloadData(String payloadData) {
		this.payloadData = payloadData.getBytes(Charset.forName("UTF-8"));
	}
	private boolean messageFrame=false;
	private byte[] payloadData;
	
	/**
	 * The byte representing the beginning of a WebSocket text frame.
	 */
	public static final byte START_OF_FRAME = (byte) 0x00;
	/**
	 * The byte representing the end of a WebSocket text frame.
	 */
	public static final byte END_OF_FRAME = (byte) 0xFF;

	public byte[] populate(byte[] frameData) {
		System.out.println("populate..");
		boolean readingState=false;
		int bytesRead=0;
		//boolean frameCompleted=false;
		ByteBuffer payloadBuffer = ByteBuffer.allocate(frameData.length);
		for (byte newestByte:frameData){
			bytesRead++;
			if (newestByte == START_OF_FRAME && !readingState) {
				System.out.println("frame start");
				readingState=true;
			}else if(newestByte == END_OF_FRAME && readingState){
				System.out.println("frame end");
				//frameCompleted=true;
				readingState=false;
				payloadData = payloadBuffer.array();
				break;
			}else if(readingState){
				payloadBuffer.put(newestByte);
			}
		}
		System.out.println(payloadData);
		System.out.println("payload len: "+payloadData.length);
		messageFrame = (payloadData.length>0);
		byte[] unprocessedBytes = new byte[frameData.length-bytesRead];
		int j=0;
		for (int i=bytesRead;i<frameData.length;i++){
			unprocessedBytes[j++]=frameData[i];
		}
		/*TODO:  Handle case where frame data was not completely received*/
		return unprocessedBytes;
	}

	public String getPayloadText() {
		return new String(payloadData,Charset.forName("UTF-8"));
	}

	public byte[] getPayloadData() {
		// TODO Auto-generated method stub
		return null;
	}
	public boolean isMessageFrame() {
		return messageFrame;
	}
	public byte[] getFrameBytes() {
		int textLength = payloadData==null?0:payloadData.length;
		ByteBuffer b = ByteBuffer.allocate(textLength + 2);
		b.put(START_OF_FRAME);
		b.put(payloadData);
		b.put(END_OF_FRAME);
		b.rewind();
		return b.array();
	}
}
