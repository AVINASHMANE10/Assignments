import src.SmartTV;

import static org.junit.Assert.*;
import org.junit.Test;
public class SmartTVTest {

    @Test
    public void testPowerControl() {
        SmartTV tv = new SmartTV();
        assertFalse(tv.isPoweredOn());
        tv.turnOn();
        assertTrue(tv.isPoweredOn());
        tv.turnOff();
        assertFalse(tv.isPoweredOn());
    }

    @Test
    public void testNetworkConnection() {
        SmartTV tv = new SmartTV();
        assertFalse(tv.checkConnection());
        tv.connectToWiFi("HomeNetwork");
        assertTrue(tv.checkConnection());
    }

    @Test
    public void testTemperatureControl() {
        SmartTV tv = new SmartTV();
        tv.turnOn();
        tv.connectToWiFi("HomeNetwork");
        tv.setTemperature(22);
        assertEquals(22, tv.getTemperature());
    }

    @Test
    public void testVolumeControl() {
        SmartTV tv = new SmartTV();
        tv.turnOn();
        tv.connectToWiFi("HomeNetwork");
        tv.adjustVolume(30);
        assertEquals(30, tv.getVolume());

        // Test mute
        tv.mute();
        assertTrue(tv.isMuted());
        tv.adjustVolume(40);
        assertEquals("Volume should not be changed if device is muted",30, tv.getVolume()); // Volume should not change when muted
    }
}
