package src;
import java.util.ArrayList;
import java.util.List;

public class SmartHomeHub {
    private List<Object> devices = new ArrayList<>();

    public void addDevice(Object device) {
        devices.add(device);
    }

    public List<Object> getDevices() {
        return devices;
    }

    public void turnAllDevicesOn() {
        for (Object device : devices) {
            if (device instanceof PowerControl) {
                ((PowerControl) device).turnOn();
            }
        }
    }

    public void turnAllDevicesOff() {
        for (Object device : devices) {
            if (device instanceof PowerControl) {
                ((PowerControl) device).turnOff();
            }
        }
    }

    public void displayDeviceStatuses() {
        for (Object device : devices) {
            System.out.println("\n" + device.getClass().getSimpleName() + " Status:");
            if (device instanceof PowerControl) {
                System.out.println("  Power: " + (((PowerControl) device).isPoweredOn() ? "ON" : "OFF"));
            }
            if (device instanceof NetworkConnected) {
                System.out.println("  WiFi: " + (((NetworkConnected) device).checkConnection() ? "Connected" : "Disconnected"));
            }
            if (device instanceof TemperatureControl) {
                System.out.println("  Temperature: " + ((TemperatureControl) device).getTemperature() + "°C");
            }
            if (device instanceof AudioControl) {
                System.out.println("  Volume: " + ((AudioControl) device).getVolume());
                System.out.println("  Mute: " + (((AudioControl) device).isMuted() ? "Muted" : "Unmuted"));
            }
        }
    }
}
