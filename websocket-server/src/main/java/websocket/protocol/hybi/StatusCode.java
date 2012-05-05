package websocket.protocol.hybi;

public enum StatusCode {
	NORMAL_CLOSURE(1000),
	ENDPOINT_GOING_AWAY(1001),
	PROTOCOL_ERROR(1002),
	UNSUPPORTED_DATA_RECEIVED(1003),
	INCONSISTENT_TYPE(1007),
	POLICY_VIOLATION(1008),
	MESSAGE_TOO_LARGE(1009),
	EXTENSION_EXPECTED(1010);
	
	private final int code;
	StatusCode(int code){
		this.code = code;
	}
	public int getCode() {
		return code;
	}
}
