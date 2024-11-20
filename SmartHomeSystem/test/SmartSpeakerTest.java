import src.SmartSpeaker;

import static org.junit.Assert.*;
import org.junit.Test;

public class SmartSpeakerTest {

    @Test
    public void testPowerControl() {
        SmartSpeaker speaker = new SmartSpeaker();
        assertFalse("speaker should be off initially before turnon() is called.",speaker.isPoweredOn());
        speaker.turnOn();
        assertTrue("speaker should be on after power on.",speaker.isPoweredOn());
        speaker.turnOff();
        assertFalse("speaker should be turned off after turnoff() called",speaker.isPoweredOn());
    }

    @Test
    public void testNetworkConnection() {
        SmartSpeaker speaker = new SmartSpeaker();
        assertFalse("before connecting to wifi connection status should be false",speaker.checkConnection());
        speaker.connectToWiFi("HomeNetwork");
        assertTrue("connection status should be true once connection established.",speaker.checkConnection());
    }

    @Test
    public void testVolumeControl() {
        SmartSpeaker speaker = new SmartSpeaker();
        speaker.turnOn();
        speaker.connectToWiFi("HomeNetwork");
        speaker.adjustVolume(60);
        assertEquals(60, speaker.getVolume());

        // Test mute
        speaker.mute();
        assertTrue(speaker.isMuted());
        speaker.adjustVolume(70);
        assertEquals("Volume should not be changed if device is muted",60, speaker.getVolume()); // Volume should not change when muted
    }


    @Test
   public void testSmartSpeakerOperations() {
        SmartSpeaker speaker = new SmartSpeaker();
        assertFalse(speaker.isPoweredOn());
        speaker.turnOn();
        assertTrue("speaker should be turned on when power turned on.",speaker.isPoweredOn());

        speaker.connectToWiFi("Home WiFi");
        assertTrue("speaker should be connected as wifi is connected",speaker.checkConnection());

        speaker.adjustVolume(50);
        assertEquals("updated value should match with speakers current value",50, speaker.getVolume());

        speaker.mute();
       assertTrue(speaker.isMuted());
        speaker.adjustVolume(60);
        assertEquals(50, speaker.getVolume()); // Volume should not change when muted
    }
}
