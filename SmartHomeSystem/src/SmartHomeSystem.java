package src;
// import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SmartHomeSystem {

    public static void main(String[] args) {
        // Initialize devices
        SmartThermostat thermostat = new SmartThermostat();
        SmartSpeaker speaker = new SmartSpeaker();
        SmartTV tv = new SmartTV();

        // Add devices to the hub
        SmartHomeHub hub = new SmartHomeHub();
        hub.addDevice(thermostat);
        hub.addDevice(speaker);
        hub.addDevice(tv);

        // Scanner for user input
        Scanner scanner = new Scanner(System.in);

        // Main program loop
        boolean isRunning = true;
        while (isRunning) {
            // Show main menu
            System.out.println("\n--- Main Menu ---");
            System.out.println("1. View All Device Status");
            System.out.println("2. Select a Device");
            System.out.println("3. Turn ON All Devices");
            System.out.println("4. Turn OFF All Devices");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    // Show all device statuses
                    hub.displayDeviceStatuses();
                    showNavigationOptions(scanner);
                    break;

                case "2":
                    // Select a device for specific operations
                    selectDeviceMenu(hub, scanner);
                    showNavigationOptions(scanner);
                    break;

                case "3":
                    // Turn ON all devices
                    hub.turnAllDevicesOn();
                    System.out.println("All devices are turned ON.");
                    showNavigationOptions(scanner);
                    break;

                case "4":
                    // Turn OFF all devices
                    hub.turnAllDevicesOff();
                    System.out.println("All devices are turned OFF.");
                    showNavigationOptions(scanner);
                    break;

                case "5":
                    // Exit the program
                    System.out.println("Exiting the system. Goodbye!");
                    isRunning = false;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }

        scanner.close();
    }

    // Method to handle navigation options after each operation
    private static void showNavigationOptions(Scanner scanner) {
        while (true) {
            System.out.println("\n1. Return to Main Menu");
            System.out.println("2. Exit");
            System.out.print("Enter your choice: ");
            String navChoice = scanner.nextLine();

            if (navChoice.equals("1")) {
                return; // Return to main menu
            } else if (navChoice.equals("2")) {
                System.out.println("Exiting the system. Goodbye!");
                System.exit(0); // Exit the program
            } else {
                System.out.println("Invalid choice. Please select 1 or 2.");
            }
        }
    }

    // Method to handle device selection and operations
    private static void selectDeviceMenu(SmartHomeHub hub, Scanner scanner) {
        System.out.println("\n--- Select a Device ---");
        List<Object> devices = hub.getDevices();

        for (int i = 0; i < devices.size(); i++) {
            System.out.println((i + 1) + ". " + devices.get(i).getClass().getSimpleName());
        }
        System.out.print("Enter your choice: ");
        String deviceChoice = scanner.nextLine();

        try {
            int deviceIndex = Integer.parseInt(deviceChoice) - 1;
            if (deviceIndex >= 0 && deviceIndex < devices.size()) {
                Object selectedDevice = devices.get(deviceIndex);
                performDeviceOperations(selectedDevice, scanner);
            } else {
                System.out.println("Invalid choice. Returning to Main Menu.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to Main Menu.");
        }
    }

    // Method to handle operations for a selected device
    private static void performDeviceOperations(Object device, Scanner scanner) {
        boolean backToDeviceMenu = false;

        while (!backToDeviceMenu) {
            System.out.println("\n--- Operations for " + device.getClass().getSimpleName() + " ---");
            if (device instanceof PowerControl) {
                System.out.println("1. Turn ON");
                System.out.println("2. Turn OFF");
            }
            if (device instanceof NetworkConnected) {
                System.out.println("3. Connect to WiFi");
            }
            if (device instanceof TemperatureControl) {
                System.out.println("4. Set Temperature");
            }
            if (device instanceof AudioControl) {
                System.out.println("5. Adjust Volume");
                System.out.println("6. Mute/Unmute");
            }
            System.out.println("7. Return to Main Menu");
            System.out.print("Enter your choice: ");
            String operationChoice = scanner.nextLine();

            switch (operationChoice) {
                case "1":
                    if (device instanceof PowerControl) {
                        ((PowerControl) device).turnOn();
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "2":
                    if (device instanceof PowerControl) {
                        ((PowerControl) device).turnOff();
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "3":
                    if (device instanceof NetworkConnected) {
                        System.out.print("Enter WiFi name: ");
                        String wifiName = scanner.nextLine();
                        ((NetworkConnected) device).connectToWiFi(wifiName);
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "4":
                    if (device instanceof TemperatureControl) {
                        System.out.print("Enter temperature: ");
                        try {
                            int temperature = Integer.parseInt(scanner.nextLine());
                            ((TemperatureControl) device).setTemperature(temperature);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Temperature not set.");
                        }
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "5":
                    if (device instanceof AudioControl) {
                        System.out.print("Enter volume level: ");
                        try {
                            int volume = Integer.parseInt(scanner.nextLine());
                            ((AudioControl) device).adjustVolume(volume);
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Volume not adjusted.");
                        }
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "6":
                    if (device instanceof AudioControl) {
                        ((AudioControl) device).mute();
                    } else {
                        System.out.println("Operation not supported.");
                    }
                    break;

                case "7":
                    backToDeviceMenu = true;
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
}
