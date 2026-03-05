//================================================
//AlarmGUI.java
//Simple Java Swing Alarm Clock with Sound & Countdown
//================================================

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Timer;
import java.util.TimerTask;
import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class AlarmGUI {

    // -----------------------------
    // GUI COMPONENTS
    // -----------------------------
    private JTextField timeField;
    private JLabel statusLabel;
    private JLabel countdownLabel;
    private JButton setAlarmButton;
    private JButton stopButton; // NEW
    private JComboBox<String> timeUnitBox;

    // -----------------------------
    // TIMER OBJECTS
    // -----------------------------
    private Timer alarmTimer;
    private javax.swing.Timer countdownTimer;

    // -----------------------------
    // CONSTANTS
    // -----------------------------
    private static final String SOUND_FILE_PATH = "../sound/alarm.wav";
    private static final String APP_TITLE = "Java Alarm Clock with Sound";

    // ==============================================================
    // MAIN METHOD
    // ==============================================================
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new AlarmGUI().createAndShowGUI());
    }

    // ==============================================================
    // GUI SETUP
    // ==============================================================
    private void createAndShowGUI() {

        JFrame frame = new JFrame(APP_TITLE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 250);
        frame.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        // STATUS LABEL
        statusLabel = new JLabel("Enter time for the alarm:");
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        frame.add(statusLabel, gbc);

        // INPUT FIELD
        timeField = new JTextField(8);
        timeField.setHorizontalAlignment(JTextField.CENTER);
        timeField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;

        frame.add(timeField, gbc);

        // UNIT SELECT
        timeUnitBox = new JComboBox<>(new String[]{"Minutes", "Seconds"});
        timeUnitBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridx = 1;

        frame.add(timeUnitBox, gbc);

        // SET ALARM BUTTON
        setAlarmButton = new JButton("Set Alarm");
        setAlarmButton.setBackground(new Color(25,118,210));
        setAlarmButton.setForeground(Color.WHITE);
        setAlarmButton.setFont(new Font("Segoe UI", Font.BOLD, 14));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;

        setAlarmButton.addActionListener(e -> setAlarm());

        frame.add(setAlarmButton, gbc);

        // STOP BUTTON (NEW)
        stopButton = new JButton("Stop Alarm");
        stopButton.setBackground(new Color(220,53,69));
        stopButton.setForeground(Color.WHITE);
        stopButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stopButton.setEnabled(false);

        gbc.gridy = 3;

        stopButton.addActionListener(e -> stopAlarm());

        frame.add(stopButton, gbc);

        // COUNTDOWN LABEL
        countdownLabel = new JLabel("Countdown: --:--");
        countdownLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        gbc.gridy = 4;

        frame.add(countdownLabel, gbc);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    // ==============================================================
    // SET ALARM
    // ==============================================================
    private void setAlarm() {

        stopAlarm(); // cancel previous timers

        try {

            int inputValue = Integer.parseInt(timeField.getText().trim());

            if (inputValue <= 0) {
                statusLabel.setText("Please enter a positive number.");
                return;
            }

            String unit = (String) timeUnitBox.getSelectedItem();

            long delayMillis;
            int totalSeconds;

            if (unit.equals("Seconds")) {
                delayMillis = inputValue * 1000L;
                totalSeconds = inputValue;
            } else {
                delayMillis = inputValue * 60 * 1000L;
                totalSeconds = inputValue * 60;
            }

            // Disable/enable buttons
            setAlarmButton.setEnabled(false);
            stopButton.setEnabled(true);

            final int[] remaining = {totalSeconds};

            countdownLabel.setText("Countdown: " + formatTime(remaining[0]));

            countdownTimer = new javax.swing.Timer(1000, e -> {

                remaining[0]--;

                countdownLabel.setText("Countdown: " + formatTime(remaining[0]));

                if (remaining[0] <= 0) {
                    ((javax.swing.Timer)e.getSource()).stop();
                }

            });

            countdownTimer.start();

            alarmTimer = new Timer();

            alarmTimer.schedule(new TimerTask() {

                @Override
                public void run() {

                    SwingUtilities.invokeLater(() -> {

                        SoundPlayer.play(SOUND_FILE_PATH);

                        JOptionPane.showMessageDialog(
                                null,
                                "Time’s up!",
                                "ALARM ALERT!",
                                JOptionPane.WARNING_MESSAGE
                        );

                        stopAlarm();
                        statusLabel.setText("Alarm finished.");

                    });

                }

            }, delayMillis);

            statusLabel.setText("Alarm set for " + inputValue + " " + unit.toLowerCase() + ".");

        }
        catch (NumberFormatException ex) {

            statusLabel.setText("Invalid input. Please enter a number.");

        }
    }

    // ==============================================================
    // STOP ALARM (NEW)
    // ==============================================================
    private void stopAlarm() {

        if (alarmTimer != null) {
            alarmTimer.cancel();
            alarmTimer = null;
        }

        if (countdownTimer != null) {
            countdownTimer.stop();
            countdownTimer = null;
        }

        countdownLabel.setText("Countdown: --:--");

        setAlarmButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    // ==============================================================
    // FORMAT TIME
    // ==============================================================
    private String formatTime(int seconds) {

        int mins = seconds / 60;
        int secs = seconds % 60;

        return String.format("%02d:%02d", mins, secs);
    }
}


//================================================
// SOUND PLAYER CLASS
//================================================

class SoundPlayer {

    public static void play(String filePath) {

        try {

            File soundFile = new File(filePath);

            if (!soundFile.exists()) {

                System.err.println("Sound file not found: " + filePath);
                Toolkit.getDefaultToolkit().beep();
                return;

            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);

            Clip clip = AudioSystem.getClip();

            clip.open(audioIn);

            clip.start();

        }
        catch (UnsupportedAudioFileException e) {

            System.err.println("Unsupported audio format.");

        }
        catch (IOException e) {

            System.err.println("Error reading sound file.");

        }
        catch (LineUnavailableException e) {

            System.err.println("Audio line unavailable.");

        }

    }
}