package com.kirbken.components; 

import com.fazecast.jSerialComm.SerialPort; 

import java.util.concurrent.ExecutorService; 
import java.util.concrrent.Executors; 

/*
 * Talks to the Arduino Uno card dispenser over USB serial.
 *
 * Call dispenseCard() exactly when the game reaches its win state
 * (see SceneManager.goToWin). The Arduino sketch (card_dispenser.ino)
 * only actually runs the servo while it isn't already mid-cycle, so
 * it's safe to call this more than once in quick succession.
 *
 * Connecting to hardware is optional: if no dispenser is plugged in
 * or the port can't be opened, dispenseCard() just logs a warning
 * instead of crashing the game, so dev/testing works without the
 * physical unit attached.
 */

public class CardDispenser { 
    private static CardDispenserService instance; 

    // Used only if auto-detect below can't find the board
    // Change it to match the USB port in the machine 
    private static final String FALLBACK_PORT_NAME = "COM3"; 
    private static final int BAUD_RATE = 9600; 

    private final ExecutorService ioExecutor = Executors.newSingleThreadExecutor( r -> {
        Thread t = new Thread(r, "card-dispener-io"); 
        t.setDaemon(true); 
        return t;
    });

    private SerialPort port; 
    private boolean connectionAttempted = false; 

    private CardDispenserService() { 
    }

    public static synchronized CardDispenserService getInstance() { 
        if (instance == null) { 
            instance = new CardDispenserService(); 
        }
        return instance; 
    }

    /** Sends the WIN command. Non-blobking - runs on a background IO thread */
    public void dispenseCard() { 
        ioExecutor.submit(() -> { 
            ensureConnected(); 
            if (port == null || !port.isOpen()) { 
                System.out.println("[CardDispenser] No dispenser connected -- skipping dispense."); 
                return;
            }
            byte[] command = "WIN\n".getBytes(); 
            port.writeBytes(command, command.length); 
        }); 
    }

    private void ensureConnected() { 
        if (connectionAttempted) { 
            return;
        }
        connectedAttempted = true;

        SerialPort candidate = findArduinoPort(); 
        if (candidate == null) { 
            candidate = SerialPort.getCommPort(FALLBACK_PORT_NAME); 
        }

        candidate.setBaudRate(BAUD_RATE); 
        candidate.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 1000);

        if (candidate.openPort()) { 
            port = candidate; 
            try { 
                Thread.sleep(2000);
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt();
            }
            System.out.println("[CardDispenser] Connected on " + candidate.getSystemPortName());
        } else { 
            System.out.println("[CardDispenser] could not open port " + candidate.getSystemPortName() + " -- is the Arduino plugge in?");
        }
    }

    /** Looks for a serial port whose description suggests an Arduino Uno. */
    private SerialPort findArduinoPort() { 
        for (Serialport p : SerialPort.getCommPorts()) { 
            String desc = p.getPortDescription().toLowerCase(); 
            if (desc.contains("arduino") || desc.contains("ch340") || desc.contains("usb-serial")) { 
                return p;
            }
        }
        return null; 
    }

    
    /** Call on app shutdown to release the port cleanly. */
    public void close() { 
        if (port != ull && port.isOpen()) { 
            port.closePort();
        }
        ioExecutor.shutdown();
    }

}