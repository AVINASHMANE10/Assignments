package src;
public class SmartTV implements PowerControl, NetworkConnected, TemperatureControl, AudioControl {
    private boolean isOn = false;
    private boolean isConnected = false;
    private int temperature = 20;
    private int volume = 50;
    private boolean isMuted = false;

    @Override
    public void turnOn() {
        isOn = true;
        System.out.println("SmartTV is ON.");
    }

    @Override
    public void turnOff() {
        isOn = false;
        System.out.println("SmartTV is OFF.");
    }

    @Override
    public boolean isPoweredOn() {
        return isOn;
    }

    @Override
    public void connectToWiFi(String networkName) {
        isConnected = true;
        System.out.println("SmartTV connected to WiFi: " + networkName);
    }

    @Override
    public boolean checkConnection() {
        return isConnected;
    }

    @Override
    public void setTemperature(int temperature) {
        if (isOn && isConnected) {
            this.temperature = temperature;
            System.out.println("SmartTV temperature set to: " + temperature + "°C.");
        } else {
            System.out.println("Operation failed. Ensure SmartTV is ON and connected to WiFi.");
        }
    }

    @Override
    public int getTemperature() {
        return isOn && isConnected ? temperature : 0;
    }

    @Override
    public void adjustVolume(int level) {
        if (isOn && isConnected) {
            if (!isMuted) {
                volume = level;
                System.out.println("SmartTV volume set to: " + volume);
            } else {
                System.out.println("SmartTV is muted. Volume adjustment has no effect.");
            }
        } else {
            System.out.println("Operation failed. Ensure SmartTV is ON and connected to WiFi.");
        }
    }

    @Override
    public void mute() {
        if (isOn && isConnected) {
            isMuted = !isMuted;
            System.out.println("SmartTV is now " + (isMuted ? "Muted" : "Unmuted"));
        } else {
            System.out.println("Operation failed. Ensure SmartTV is ON and connected to WiFi.");
        }
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public int getVolume() {
        return volume;
    }
}
