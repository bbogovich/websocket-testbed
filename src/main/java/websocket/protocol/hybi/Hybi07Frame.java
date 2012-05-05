package websocket.protocol.hybi;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import websocket.protocol.Frame;


public class Hybi07Frame implements Frame{
	private enum PayloadLengthType {SHORT_7BIT,EXTENDED_16BIT,EXTENDED_64BIT};

	boolean finalFragment=true;
	boolean rsv1=false;
	boolean rsv2=false;
	boolean rsv3=false;
	OpCode opCode=OpCode.TEXT_FRAME;
	boolean masked=false;
	//int payloadLength;
	long maskingKey;
	byte[] payloadData;
	byte[] extensionData;
	byte[] applicationData;
	byte[] unprocessedData;
	/**
	 * For a parsed frame, the number of bytes read from the incoming byte array
	 */
	//int bytesRead;
	
	public Hybi07Frame(){}
	public Hybi07Frame(byte[] frameData){
		this.populate(frameData);
	}
	
	public boolean isFinalFragment() {
		return finalFragment;
	}
	public void setFinalFragment(boolean finalFragment) {
		this.finalFragment = finalFragment;
	}
	public boolean isRsv1() {
		return rsv1;
	}
	public void setRsv1(boolean rsv1) {
		this.rsv1 = rsv1;
	}
	public boolean isRsv2() {
		return rsv2;
	}
	public void setRsv2(boolean rsv2) {
		this.rsv2 = rsv2;
	}
	public boolean isRsv3() {
		return rsv3;
	}
	public void setRsv3(boolean rsv3) {
		this.rsv3 = rsv3;
	}
	public OpCode getOpCode() {
		return opCode;
	}
	public void setOpCode(OpCode opCode) {
		this.opCode = opCode;
	}
	public boolean isMasked() {
		return masked;
	}
	public void setMasked(boolean masked) {
		this.masked = masked;
	}
	public long getMaskingKey() {
		return maskingKey;
	}
	public void setMaskingKey(long maskingKey) {
		this.maskingKey = maskingKey;
	}
	public byte[] getExtensionData() {
		return extensionData;
	}
	public void setExtensionData(byte[] extensionData) {
		this.extensionData = extensionData;
	}
	public byte[] getApplicationData() {
		return applicationData;
	}
	public void setApplicationData(byte[] applicationData) {
		this.applicationData = applicationData;
	}
	public void setPayloadData(byte[] payloadData) {
		this.payloadData = payloadData;
	}
	public void setPayloadData(String payloadData) {
		this.payloadData = payloadData.getBytes(Charset.forName("UTF-8"));
	}
	public void setUnprocessedData(byte[] unprocessedData) {
		this.unprocessedData = unprocessedData;
	}
	public byte[] getPayloadData(){
		return payloadData;
	}
	
	public String getFrameText(){
		return new String(payloadData,Charset.forName("UTF-8"));
	}
	
	/**
	 * @return The number of bytes in the frame
	 */
	public int getFrameLength(){
		return -1;
	}
	
	/**
	 * @return For a frame populated with a received byte array, return the
	 * array of unused bytes following the end of frame
	 */
	public byte[] getUnprocessedData(){
		return unprocessedData;
	}
	
	public byte[] populate(byte[] frameData) {
		int frame_byte_count=0;
		byte[] frame_masking_key_buffer = new byte[4];
		int frame_masking_key_byte_count=0;
		long frame_payload_length=0;
		boolean frame_payload_length_overflow=false;
		byte[] frame_extended_payload_length_buffer=null;
		PayloadLengthType frame_payload_length_type=null;
		long payload_frame_position=0;
		boolean readingState=false;

		StringBuffer frame_payload_text=new StringBuffer();
		ByteBuffer frame_payload = null;
		
		for (byte newestByte:frameData){
			Charset UTF8_CHARSET = Charset.forName("UTF-8");
			if(!readingState){
				frame_byte_count=1;
				frame_masking_key_byte_count=0;
				frame_payload_length=0;
				frame_payload_length_overflow=false;
				frame_masking_key_buffer = new byte[4];
				payload_frame_position=0;
				frame_payload_text = new StringBuffer();
				System.out.println("Start reading for a frame fragment for draft 07");
				/*
				 * first byte is FIN,RSV1,RSV2,RSV3,OPCODE(4 bits)
				 * */
				finalFragment = (newestByte & 0x80) == 0x80;
				if(finalFragment){
					System.out.println("FIN is set to 1, this is the last fragment");
				}else{
					System.out.println("FIN is set to 0, there area additional frame fragments");
				}
				rsv1 = (newestByte&0x40)==0x40;
				if(rsv1){
					System.out.println("RSV1 is set to 1");
				}else{
					System.out.println("RSV1 is set to 0");
				}
				rsv2 = (newestByte&0x20)==0x20;
				if(rsv2){
					System.out.println("RSV2 is set to 1");
				}else{
					System.out.println("RSV2 is set to 0");
				}
				rsv3 = (newestByte&0x10)==0x10;
				if(rsv3){
					System.out.println("RSV3 is set to 1");
				}else{
					System.out.println("RSV3 is set to 0");
				}
				short opcode = (short)(newestByte & 0x0F);
				if(opcode==0x08){
					System.out.println("Opcode: Connection Close");
					//websocket.getSocket().close();
					this.opCode=OpCode.CONNECTION_CLOSE;
				}else if(opcode==0x02){
					System.out.println("Opcode: Binary Frame");
					this.opCode=OpCode.BINARY_FRAME;
				}else if(opcode==0x01){
					System.out.println("Opcode: Text Frame");
					this.opCode=OpCode.TEXT_FRAME;
				}else if(opcode==0x00){
					System.out.println("Opcode: Continuation Frame");
					this.opCode=OpCode.CONTINUATION_FRAME;
				}else if(opcode==0x0A){
					System.out.println("Opcode: Ping");
					this.opCode=OpCode.PING;
				}else if(opcode==0x0B){
					System.out.println("Opcode: Pong");
					this.opCode=OpCode.PONG;
				}else{
					System.out.println("Unknown Opcode: "+Integer.toString(opcode,2));
					this.opCode=null;
				}
				readingState=true;
			}else{
				frame_byte_count++;
				System.out.println("Reading byte "+frame_byte_count+" for a frame fragment already in progress for draft 07");
				if(frame_byte_count==2){
					System.out.println("Byte is mask + payload");
					masked=(newestByte & 0x80)==0x80;
					if(masked){
						System.out.println("Payload is masked");
					}else{
						System.out.println("Payload is not masked");
					}
					int payloadLength=0x07F&newestByte;
					System.out.println("Payload length = "+payloadLength);
					int payload_length=0x7F&newestByte;
					if(payload_length==126){
						System.out.println("Payload length is extended 16 bit");
						frame_payload_length_type=PayloadLengthType.EXTENDED_16BIT;
						frame_extended_payload_length_buffer = new byte[2];
					}else if(payload_length==127){
						System.out.println("Payload length is extended 64 bit");
						frame_payload_length_type=PayloadLengthType.EXTENDED_64BIT;
						frame_extended_payload_length_buffer = new byte[8];
					}else{
						System.out.println("Final Payload length is "+payloadLength);
						frame_payload_length=payload_length;
						frame_payload = ByteBuffer.allocate((int)frame_payload_length);
					}
				}else if(frame_byte_count<=4&&frame_payload_length_type==PayloadLengthType.EXTENDED_16BIT){
					System.out.println("Processing an extended-16 payload length");
					frame_extended_payload_length_buffer[frame_byte_count-3]=newestByte;
					if(frame_byte_count==4){
						System.out.println("Frame payload length complete");
						//process payload length buffer as an unsigned integer
						//cast the bytes to ints so that we don't get negative numbers
						int firstByte=0;
						int secondByte=0;
						firstByte = (0x000000FF & ((int)frame_extended_payload_length_buffer[0]));
						secondByte = (0x000000FF & ((int)frame_extended_payload_length_buffer[1]));
						frame_payload_length = (long)(firstByte<<8|secondByte);
						frame_payload = ByteBuffer.allocate((int)frame_payload_length);
						System.out.println("Payload Length = "+frame_payload_length);
					}
				}else if(frame_byte_count<=10&&frame_payload_length_type==PayloadLengthType.EXTENDED_64BIT){
					System.out.println("Processing an extended-64 payload length");
					frame_extended_payload_length_buffer[frame_byte_count-3]=newestByte;
					if(frame_byte_count==10){
						System.out.println("Frame payload length complete");
						//process payload length buffer as an unsigned integer
						//Problem - java longs are 64-bit signed; the incoming wire data is 64-bit bytes
						//this causes an issue in that we can potentially lose the last byte on a maximum
						//sized frame.	Handle it by processing extra bytes from the payload if the
						//payload length is negative.
						int byte0=(0x000000FF & ((int)frame_extended_payload_length_buffer[0]));
						int byte1=(0x000000FF & ((int)frame_extended_payload_length_buffer[1]));
						int byte2=(0x000000FF & ((int)frame_extended_payload_length_buffer[2]));
						int byte3=(0x000000FF & ((int)frame_extended_payload_length_buffer[3]));
						int byte4=(0x000000FF & ((int)frame_extended_payload_length_buffer[4]));
						int byte5=(0x000000FF & ((int)frame_extended_payload_length_buffer[5]));
						int byte6=(0x000000FF & ((int)frame_extended_payload_length_buffer[6]));
						int byte7=(0x000000FF & ((int)frame_extended_payload_length_buffer[7]));
						frame_payload_length = (long)(byte0<<54|byte1<<48|byte2<<40|byte3<<32|byte4<<24|byte5<<16|byte6<<8|byte7);
						if(frame_payload_length<0){
							frame_payload_length = frame_payload_length*-1;
							frame_payload_length_overflow=true;
							System.out.println("Frame payload length (extended-64bit) overflow!	TODO: HANDLE OVERFLOW CASE");
						}
						frame_payload = ByteBuffer.allocate((int)frame_payload_length);
					}
					if(frame_payload_length_overflow){
					    System.out.println("Somehow the frame payload was longer than a 2^64 characters.");
					}
				}else if(masked&&frame_masking_key_byte_count<4){ //still reading masking key bytes
					System.out.println("Setting frame masking key buffer byte "+frame_masking_key_byte_count);
					frame_masking_key_buffer[frame_masking_key_byte_count++]=newestByte;
					if(frame_masking_key_byte_count==4){ //end of masking key
						System.out.println("Finished processing frame masking key");
						long byte0=(0x000000FF & ((int)frame_masking_key_buffer[0]));
						int byte1=(0x000000FF & ((int)frame_masking_key_buffer[1]));
						int byte2=(0x000000FF & ((int)frame_masking_key_buffer[2]));
						int byte3=(0x000000FF & ((int)frame_masking_key_buffer[3]));
						maskingKey=(long)(byte0<<24|byte1<<16|byte2<<8|byte3);
						System.out.println("Masking key:	"+Long.toString(maskingKey,2));
					}
				}else if (masked){
					System.out.println("Processing masked payload data "+newestByte);
					int mask_byte_index = (int) (payload_frame_position%4);
					System.out.println("Applying mask transformation for index "+mask_byte_index+" to byte");
					byte transformedByte = (byte) (newestByte ^ frame_masking_key_buffer[mask_byte_index]);
					System.out.println("Transformed byte: "+(char)transformedByte);
					frame_payload_text.append(new String(new byte[] {transformedByte},UTF8_CHARSET));
					frame_payload.put(transformedByte);
					payload_frame_position++;
					if(frame_payload_text.length()==frame_payload_length){
						System.out.println("End of text frame.	Payload Data: "+frame_payload_text.toString());
						readingState=false;
						String frame_text = frame_payload_text.toString();
						frame_payload_text=null;
						System.out.println("Frame Text:  "+frame_text);
						//this.wsl.onMessage(this, frame_text);
					}
				}else{
					System.out.println("Processing unmasked payload data "+newestByte);
					frame_payload_text.append(new String(new byte[] {newestByte},Charset.forName("UTF-8")));
					frame_payload.put(newestByte);
					payload_frame_position++;
				}
			}
		}
		if(frame_payload!=null){
			payloadData = frame_payload.array();
		}
		if(frameData.length>frame_byte_count){
			unprocessedData = new byte[frameData.length-frame_byte_count];
			int j=0;
			for (int i=frame_byte_count;i<frameData.length;i++){
				unprocessedData[j++] = frameData[i];
			}
		}else{
			unprocessedData = null;
		}
		return unprocessedData;
	}
	
	public String getPayloadText() {
		return new String(payloadData,Charset.forName("UTF-8"));
	}
	
	public byte[] getFrameBytes(){
		ByteBuffer b;
		int payloadLength=(payloadData==null?0:payloadData.length);
		long frameLength=2+payloadLength; //base header is two bytes
		PayloadLengthType payloadLengthType;
		byte payloadLengthByte=0;
		byte[] extendedPayloadLengthBytes=null;
		if(payloadLength<126){
			payloadLengthType=PayloadLengthType.SHORT_7BIT;
			payloadLengthByte=(byte)payloadLength;
		}else if(payloadLength<65536){
			payloadLengthType=PayloadLengthType.EXTENDED_16BIT;
			payloadLengthByte=0x07E; //if payload length byte = 127, the client will expect a 16-bit extended payload length field
			extendedPayloadLengthBytes = new byte[2];
			extendedPayloadLengthBytes[1] = (byte)(payloadLength & 0x00FF);
			extendedPayloadLengthBytes[0] = (byte)((payloadLength & 0xFF00)>>8);
			frameLength+=2;
		}else{
			payloadLengthType=PayloadLengthType.EXTENDED_64BIT;
			payloadLengthByte=0x07F; //if payload length byte = 127, the client will expect a 64-bit extended payload length field
			extendedPayloadLengthBytes = new byte[8];
			extendedPayloadLengthBytes[7] = (byte)(payloadLength & 0x00FF);
			extendedPayloadLengthBytes[6] = (byte)((payloadLength>>8) & 0x00FF);
			extendedPayloadLengthBytes[5] = (byte)((payloadLength>>16) & 0x00FF);
			extendedPayloadLengthBytes[4] = (byte)((payloadLength>>24) & 0x00FF);
			extendedPayloadLengthBytes[3] = (byte)((payloadLength>>32) & 0x00FF);
			extendedPayloadLengthBytes[2] = (byte)((payloadLength>>40) & 0x00FF);
			extendedPayloadLengthBytes[1] = (byte)((payloadLength>>48) & 0x00FF);
			extendedPayloadLengthBytes[0] = (byte)((payloadLength>>54) & 0x00FF);
			frameLength+=8;
		}
		System.out.println("Payload type detected as:"+payloadLengthType);
		//Byte 1 - set FIN to 1, RSV1,2,3 to 0, opcode to this.opcode
		//Byte 2 - Mask is 0 - by protocol definition server messages are unmasked, remaining value based on payload length
		/*
		 * TODO: Add support for payload masking so the frame class can be reused for sending client messages
		 * */
		b = ByteBuffer.allocate((int)frameLength);
		b.put((byte)(0x80|this.opCode.code));
		b.put(payloadLengthByte);
		if(extendedPayloadLengthBytes!=null){
			for (byte lengthByte:extendedPayloadLengthBytes){
				b.put(lengthByte);
			}
		}
		b.put(payloadData);
		b.rewind();
		return b.array();
	}
}
