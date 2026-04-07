import java.sql.*;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.*;

public class ReminderManager {
    private Timer timer;
    
    public void startReminderChecker() {
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                checkReminders();
            }
        }, 0, 30000);
    }
    
    private void checkReminders() {
        String sql = "SELECT * FROM reminders WHERE is_completed = FALSE AND reminder_datetime <= NOW()";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                String title = rs.getString("title");
                String message = rs.getString("message");
                int id = rs.getInt("reminder_id");
                
                JOptionPane.showMessageDialog(null, message, "REMINDER: " + title, JOptionPane.INFORMATION_MESSAGE);
                
                String update = "UPDATE reminders SET is_completed = TRUE WHERE reminder_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(update)) {
                    pstmt.setInt(1, id);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    public static void addReminder(String title, String datetime, String message) {
        String sql = "INSERT INTO reminders (title, reminder_datetime, message) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setString(2, datetime);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Reminder added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }
}