package de.rwth.idsg.steve.ocpp.ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

/**
 * @author Sevket Goekay <goekay@dbis.rwth-aachen.de>
 * @since 10.05.2018
 */
@Slf4j
public final class WebSocketLogger {

    private WebSocketLogger() { }

    public static void connected(String chargeBoxId, WebSocketSession session) {
        log.info("[chargeBoxId={}, sessionId={}] Connection is established", chargeBoxId, session.getId());
    }

    public static void closed(String chargeBoxId, WebSocketSession session, CloseStatus closeStatus) {
        log.warn("[chargeBoxId={}, sessionId={}] Connection is closed, status: {}", chargeBoxId, session.getId(), closeStatus);
    }

    public static void sending(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Sending: {}", chargeBoxId, session.getId(), msg);
    }

    public static void sendingPing(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Sending ping message", chargeBoxId, session.getId());
    }

    public static void receivedPong(String chargeBoxId, WebSocketSession session) {
        log.debug("[chargeBoxId={}, sessionId={}] Received pong message", chargeBoxId, session.getId());
    }

    public static void receivedText(String chargeBoxId, WebSocketSession session, String msg) {
        log.info("[chargeBoxId={}, sessionId={}] Received: {}", chargeBoxId, session.getId(), msg);
    }

    public static void pingError(String chargeBoxId, WebSocketSession session, Throwable t) {
        if (log.isErrorEnabled()) {
            log.error("[chargeBoxId=" + chargeBoxId + ", sessionId=" + session.getId() + "] Ping error", t);
        }
    }

    public static void transportError(String chargeBoxId, WebSocketSession session, Throwable t) {
        if (log.isErrorEnabled()) {
            log.error("[chargeBoxId=" + chargeBoxId + ", sessionId=" + session.getId() + "] Transport error", t);
        }
    }
}
