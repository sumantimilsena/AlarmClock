# ⏰ Java Alarm Clock with Sound & Countdown

A simple, lightweight Java Swing application that allows users to set a timer in **minutes or seconds**, view a **live countdown**, and play a **WAV sound** when the timer ends.

---

## Project Structure

```text
Java-AlarmClock/
├── src/
│   └── AlarmGUI.java
├── sound/
│   └── alarm.wav
├── README.md
└── LICENSE
<<<<<<< HEAD

=======
>>>>>>> 7da7ffad51a788e53041c73d022a3a7284a00b95


## 🚀 Features

- Set alarm in **Minutes** or **Seconds**  
- Live countdown display (`MM:SS` format)  
- Plays a **WAV file** when time is up  
- Modern blue UI using **Swing + GridBagLayout**  
- Works on **Windows, macOS, and Linux**  

---

## 🧩 How It Works

The app uses two timers:
1. `javax.swing.Timer` — updates the countdown label every second  
2. `java.util.Timer` — triggers the alarm after the specified delay  

When time is up:
- Plays `alarm.wav` using Java Sound API  
- Displays an alert dialog message  
- Resets for the next alarm  

---

## 🛠️ Installation & Run

### 1️⃣ Requirements
- Java JDK 8 or higher  
- A `.wav` sound file named `alarm.wav` (in ./sound folder)

### 2️⃣ Run in Terminal
javac AlarmGUI.java
java AlarmGUI

Example
Enter: 2

Select: Minutes or Seconds

Click: Set Alarm
→ After 2 minutes/seconds, a popup and alarm sound will appear.

🗂️ File Structure
File	Description
AlarmGUI.java	Main Swing GUI and timer logic
SoundPlayer	Separate class for audio playback
alarm.wav	Sound file triggered when time is up

## 🧾 License
This project is licensed under the [MIT License](./LICENSE).

© 2025 Suman Timilsena.  
You are free to use, modify, and distribute this project with proper attribution.
