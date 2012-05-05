package websocket.protocol;

import java.io.IOException;

/**
 * @author bbogovich
 * Thrown when parsing a frame where not all data is available.
 * This is a recoverable exception.
 */
public class IncompleteFrameException extends IOException {
    private static final long serialVersionUID = -7602512999028580054L;
}
