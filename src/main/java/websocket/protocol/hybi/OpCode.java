package websocket.protocol.hybi;

public enum OpCode {
	CONTINUATION_FRAME((byte)0x0),
	TEXT_FRAME((byte)0x01),
	BINARY_FRAME((byte)0x02),
	CONNECTION_CLOSE((byte)0x08),
	PING((byte)0x09),
	PONG((byte)0x0A);
	byte code;
	OpCode(byte code){
		this.code= code;
	}
}
