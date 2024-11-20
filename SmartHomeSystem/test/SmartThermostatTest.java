import src.SmartThermostat;
import static org.junit.Assert.*;
import org.junit.Test;

public class SmartThermostatTest {

    @Test
    public void testPowerControl() {
        SmartThermostat thermostat = new SmartThermostat();
        assertFalse(thermostat.isPoweredOn());
        thermostat.turnOn();
        assertTrue(thermostat.isPoweredOn());
        thermostat.turnOff();
        assertFalse(thermostat.isPoweredOn());
    }

    @Test
    public void testNetworkConnection() {
        SmartThermostat thermostat = new SmartThermostat();
        assertFalse(thermostat.checkConnection());
        thermostat.connectToWiFi("HomeNetwork");
        assertTrue(thermostat.checkConnection());
    }

    @Test
    public void testTemperatureControl() {
        SmartThermostat thermostat = new SmartThermostat();
        thermostat.turnOn();
        thermostat.connectToWiFi("HomeNetwork");
        thermostat.setTemperature(25);
        assertEquals(25, thermostat.getTemperature());

        // Test failure when device is off or disconnected
        thermostat.turnOff();
        thermostat.setTemperature(30);
        assertEquals(0, thermostat.getTemperature());
    }
}
