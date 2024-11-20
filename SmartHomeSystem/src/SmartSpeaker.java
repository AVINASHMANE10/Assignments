package src;
public class SmartSpeaker implements PowerControl, NetworkConnected, AudioControl {
    private boolean isOn = false;
    private boolean isConnected = false;
    private int volume = 50;
    private boolean isMuted = false;

    @Override
    public void turnOn() {
        isOn = true;
        System.out.println("SmartSpeaker is ON.");
    }

    @Override
    public void turnOff() {
        isOn = false;
        System.out.println("SmartSpeaker is OFF.");
    }

    @Override
    public boolean isPoweredOn() {
        return isOn;
    }

    @Override
    public void connectToWiFi(String networkName) {
        isConnected = true;
        System.out.println("SmartSpeaker connected to WiFi: " + networkName);
    }

    @Override
    public boolean checkConnection() {
        return isConnected;
    }

    @Override
    public void adjustVolume(int level) {
        if (isOn && isConnected) {
            if (!isMuted) {
                volume = level;
                System.out.println("SmartSpeaker volume set to: " + volume);
            } else {
                System.out.println("SmartSpeaker is muted. Volume adjustment has no effect.");
            }
        } else {
            System.out.println("Operation failed. Ensure SmartSpeaker is ON and connected to WiFi.");
        }
    }

    @Override
    public void mute() {
        if (isOn && isConnected) {
            isMuted = !isMuted;
            System.out.println("SmartSpeaker is now " + (isMuted ? "Muted" : "Unmuted"));
        } else {
            System.out.println("Operation failed. Ensure SmartSpeaker is ON and connected to WiFi.");
        }
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public int getVolume() {
        if(isOn){return volume;}
        else{return volume=0;}
    }
}
