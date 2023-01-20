package de.thu.inf.spro.chattitude.backend;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonObject;
import de.thu.inf.spro.chattitude.packet.packets.Packet;
import org.java_websocket.WebSocket;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
import org.java_websocket.framing.Framedata;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.function.Function;

public class MockWebSocket implements WebSocket {
    
    private Object attachment;
    private Function<Packet, Void> onMessageCallback;
    
    @Override
    public void close(int i, String s) {
        
    }

    @Override
    public void close(int i) {

    }

    @Override
    public void close() {

    }

    @Override
    public void closeConnection(int i, String s) {

    }

    @Override
    public void send(String s) {
        JsonObject packetData = Json.parse(s).asObject();
        Packet packet = Packet.of(packetData);
        if(onMessageCallback != null) onMessageCallback.apply(packet);
    }

    void setOnMessageCallback(Function<Packet, Void> onMessageCallback){
        this.onMessageCallback = onMessageCallback;
    }

    @Override
    public void send(ByteBuffer byteBuffer) {

    }

    @Override
    public void send(byte[] bytes) {

    }

    @Override
    public void sendFrame(Framedata framedata) {

    }

    @Override
    public void sendFrame(Collection<Framedata> collection) {

    }

    @Override
    public void sendPing() {

    }

    @Override
    public void sendFragmentedFrame(Opcode opcode, ByteBuffer byteBuffer, boolean b) {

    }

    @Override
    public boolean hasBufferedData() {
        return false;
    }

    @Override
    public InetSocketAddress getRemoteSocketAddress() {
        return null;
    }

    @Override
    public InetSocketAddress getLocalSocketAddress() {
        return null;
    }

    @Override
    public boolean isOpen() {
        return false;
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean isFlushAndClose() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public Draft getDraft() {
        return null;
    }

    @Override
    public ReadyState getReadyState() {
        return null;
    }

    @Override
    public String getResourceDescriptor() {
        return null;
    }

    @Override
    public <T> void setAttachment(T t) {
        attachment = t;
    }

    @Override
    public <T> T getAttachment() {
        return (T) attachment;
    }
}
