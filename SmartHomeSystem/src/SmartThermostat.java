package src;
public class SmartThermostat implements PowerControl, NetworkConnected, TemperatureControl {
    private boolean isOn = false;
    private boolean isConnected = false;
    private int temperature = 0;

    @Override
    public void turnOn() {
        isOn = true;
        System.out.println("SmartThermostat is ON.");
    }

    @Override
    public void turnOff() {
        isOn = false;
        System.out.println("SmartThermostat is OFF.");
    }

    @Override
    public boolean isPoweredOn() {
        return isOn;
    }

    @Override
    public void connectToWiFi(String networkName) {
        isConnected = true;
        System.out.println("SmartThermostat connected to WiFi: " + networkName);
    }

    @Override
    public boolean checkConnection() {
        return isConnected;
    }

    @Override
    public void setTemperature(int temperature) {
        if (isOn && isConnected) {
            this.temperature = temperature;
            System.out.println("SmartThermostat temperature set to: " + temperature + "°C.");
        } else {
            System.out.println("Operation failed. Ensure SmartThermostat is ON and connected to WiFi.");
        }
    }

    @Override
    public int getTemperature() {
        return isOn && isConnected ? temperature : 0; // Return 0 if device is off or disconnected
    }
}
