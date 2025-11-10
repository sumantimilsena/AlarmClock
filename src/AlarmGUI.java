//================================================
//AlarmGUI.java
//Simple Java Swing Alarm Clock with Sound & Countdown
//================================================

import javax.swing.*;              // For GUI components (JFrame, JButton, etc.)
import java.awt.*;                 // For layout and color control
import java.awt.event.*;           // For event handling (button clicks, timers)
import java.util.Timer;            // For background scheduling (alarm)
import java.util.TimerTask;        // Task to execute after a delay
import java.io.File;               // For accessing sound file
import java.io.IOException;        // For handling file input errors
import javax.sound.sampled.*;      // For playing WAV audio

/**
 * A simple Java Swing application that lets a user:
 * - Set a countdown alarm (in minutes or seconds)
 * - See a live countdown timer
 * - Play a WAV sound when the timer ends
 *
 * HOW TO USE:
 * 1. Save this file as AlarmGUI.java
 * 2. Put a sound file named "alarm.wav" in the same folder
 * 3. Compile: javac AlarmGUI.java
 * 4. Run:     java AlarmGUI
 */
public class AlarmGUI {
    // -----------------------------
    // GUI COMPONENTS
    // -----------------------------
    private JTextField timeField;            // User input field for time value
    private JLabel statusLabel;              // Displays alarm status messages
    private JLabel countdownLabel;           // Shows remaining time dynamically
    private JButton setAlarmButton;          // Button to start alarm
    private JComboBox<String> timeUnitBox;   // Drop-down for selecting Minutes or Seconds

    // -----------------------------
    // TIMER OBJECTS
    // -----------------------------
    private Timer alarmTimer;                // java.util.Timer for background scheduling
    private javax.swing.Timer countdownTimer; // Swing Timer for updating countdown label every second

    // -----------------------------
    // CONSTANTS
    // -----------------------------
    private static final String SOUND_FILE_PATH = "../sound/alarm.wav"; 
    private static final String APP_TITLE = "Java Alarm Clock with Sound";

    // ==============================================================
    // MAIN METHOD — Entry point of the program
    // ==============================================================
    public static void main(String[] args) {
        // GUI updates must be run on Swing’s Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> new AlarmGUI().createAndShowGUI());
    }

    // ==============================================================
    // GUI SETUP — Creates and arranges all GUI components
    // ==============================================================
    private void createAndShowGUI() {
        JFrame frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 220); // Fixed window size
        frame.setLayout(new GridBagLayout()); // Flexible layout manager

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Padding between elements

        // --- 1. STATUS LABEL (Instruction text) ---
        statusLabel = new JLabel("Enter time for the alarm:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        frame.add(statusLabel, gbc);

        // --- 2. INPUT FIELD (Time entry box) ---
        timeField = new JTextField(8);
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        frame.add(timeField, gbc);

        // --- 3. COMBO BOX (Choose unit: minutes or seconds) ---
        timeUnitBox = new JComboBox<>(new String[]{"Minutes", "Seconds"});
        timeUnitBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        frame.add(timeUnitBox, gbc);

        // --- 4. SET ALARM BUTTON ---
        setAlarmButton = new JButton("Set Alarm");
        setAlarmButton.setBackground(new Color(25, 118, 210));  // Blue color
        setAlarmButton.setForeground(Color.WHITE);              // White text
        setAlarmButton.setFocusPainted(false);
        setAlarmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        // Add button click listener
        setAlarmButton.addActionListener(e -> setAlarm());
        frame.add(setAlarmButton, gbc);

        // --- 5. COUNTDOWN LABEL ---
        countdownLabel = new JLabel("Countdown: --:--");
        countdownLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        frame.add(countdownLabel, gbc);

        // Center window on screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==============================================================
    // SET ALARM LOGIC — Reads input, starts countdown, triggers alarm
    // ==============================================================
    private void setAlarm() {
        // Cancel any previous running timers (if user sets new alarm)
        if (alarmTimer != null) alarmTimer.cancel();
        if (countdownTimer != null) countdownTimer.stop();

        try {
            // Parse user input
            int inputValue = Integer.parseInt(timeField.getText().trim());
            if (inputValue <= 0) {
                statusLabel.setText("Please enter a positive number.");
                return;
            }

            // Determine whether user chose Minutes or Seconds
            String unit = (String) timeUnitBox.getSelectedItem();
            long delayMillis;  // delay for java.util.Timer
            int totalSeconds;  // countdown for Swing Timer

            if (unit.equals("Seconds")) {
                delayMillis = (long) inputValue * 1000;
                totalSeconds = inputValue;
            } else { // if Minutes selected
                delayMillis = (long) inputValue * 60 * 1000;
                totalSeconds = inputValue * 60;
            }

            // --- Setup live countdown ---
            final int[] remaining = { totalSeconds };
            countdownLabel.setText("Countdown: " + formatTime(remaining[0]));

            // Swing Timer runs every second to update label
            countdownTimer = new javax.swing.Timer(1000, e -> {
                remaining[0]--;
                countdownLabel.setText("Countdown: " + formatTime(remaining[0]));
                if (remaining[0] <= 0) ((javax.swing.Timer) e.getSource()).stop();
            });
            countdownTimer.start();

            // --- Setup background alarm timer ---
            alarmTimer = new Timer();
            alarmTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // Run GUI updates on EDT (safe thread)
                    SwingUtilities.invokeLater(() -> {
                        // Play alarm sound
                        SoundPlayer.play(SOUND_FILE_PATH);

                        // Show popup message
                        JOptionPane.showMessageDialog(null,
                                "Time’s up!",
                                "ALARM ALERT!",
                                JOptionPane.WARNING_MESSAGE);

                        // Reset labels
                        statusLabel.setText("Alarm complete. Set a new one:");
                        countdownLabel.setText("Countdown: --:--");
                        alarmTimer.cancel();
                    });
                }
            }, delayMillis); // Delay until alarm time

            // --- Update user status ---
            statusLabel.setText("Alarm set for " + inputValue + " " + unit.toLowerCase() + ".");

        } catch (NumberFormatException ex) {
            // Handle case where user enters invalid text
            statusLabel.setText("Invalid input. Please enter a number.");
        }
    }

    // ==============================================================
    // FORMATTER — Converts seconds into MM:SS string for display
    // ==============================================================
    private String formatTime(int seconds) {
        int mins = seconds / 60;
        int secs = seconds % 60;
        return String.format("%02d:%02d", mins, secs);
    }
}

// ================================================================
// SOUND PLAYER CLASS — Handles WAV playback using Java Sound API
// ================================================================
class SoundPlayer {
    /**
     * Plays a WAV file once using AudioSystem and Clip.
     */
    public static void play(String filePath) {
        try {
            File soundFile = new File(filePath);

            // If file missing, print message and use system beep
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + filePath);
                Toolkit.getDefaultToolkit().beep();
                return;
            }

            // --- Load and play sound ---
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile); // Load file
            Clip clip = AudioSystem.getClip();                                     // Get audio clip
            clip.open(audioIn);                                                    // Prepare clip
            clip.start();                                                          // Play sound

        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio format. Use WAV only.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O error while reading sound file.");
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable (sound device in use?).");
            e.printStackTrace();
        }
    }
}