import src.SmartHomeHub;
import src.SmartThermostat;
import src.SmartTV;
import src.SmartSpeaker;
import java.util.List;
import static org.junit.Assert.*;
import org.junit.Test;

public class SmartHomeHubTest{

    @Test
    public void testHubControl() {
        SmartHomeHub hub = new SmartHomeHub();
        SmartThermostat thermostat = new SmartThermostat();
        SmartSpeaker speaker = new SmartSpeaker();
        SmartTV tv = new SmartTV();

        hub.addDevice(thermostat);
        hub.addDevice(speaker);
        hub.addDevice(tv);

        List<Object> devices = hub.getDevices();
        assertEquals("total size of devices should be 3",3, devices.size());

        // Turn on all devices
        hub.turnAllDevicesOn();
        assertTrue("all devices should turn on which include thermostat",thermostat.isPoweredOn());
        assertTrue("all devices should turn on which include speaker",speaker.isPoweredOn());
        assertTrue("all devices should turn on which include tv",tv.isPoweredOn());

        // Turn off all devices
        hub.turnAllDevicesOff();
        assertFalse("all devices should turn on which include thermostat",thermostat.isPoweredOn());
        assertFalse("all devices should turn on which include speaker",speaker.isPoweredOn());
        assertFalse("all devices should turn on which include tv",tv.isPoweredOn());
    }
}
