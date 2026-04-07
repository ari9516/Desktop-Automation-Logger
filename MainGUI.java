import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class MainGUI extends JFrame {
    private JTable fileTable, reminderTable;
    private DefaultTableModel fileModel, reminderModel;
    
    public MainGUI() {
        setTitle("Desktop Automation & Task Manager");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        new ReminderManager().startReminderChecker();
        
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("File Organizer", createOrganizerPanel());
        tabs.addTab("Reminders", createReminderPanel());
        tabs.addTab("View Logs", createLogsPanel());
        
        add(tabs);
        loadFileLogs();
        loadReminders();
        setVisible(true);
    }
    
    private JPanel createOrganizerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30,30,30,30));
        
        JButton btn = new JButton("ORGANIZE DESKTOP NOW");
        btn.setFont(new Font("Arial", Font.BOLD, 22));
        btn.setBackground(new Color(50,200,50));
        btn.addActionListener(e -> {
            FileOrganizer.organizeDesktop();
            JOptionPane.showMessageDialog(this, "Desktop organized!");
            loadFileLogs();
        });
        
        JLabel label = new JLabel("<html>Click button to organize desktop files into folders</html>");
        panel.add(label, BorderLayout.NORTH);
        panel.add(btn, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createReminderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridLayout(4,2,10,10));
        form.setBorder(BorderFactory.createTitledBorder("Add Reminder"));
        
        JTextField titleField = new JTextField();
        JTextField dateField = new JTextField();
        JTextArea msgArea = new JTextArea(3,20);
        
        form.add(new JLabel("Title:"));
        form.add(titleField);
        form.add(new JLabel("DateTime (YYYY-MM-DD HH:MM:SS):"));
        form.add(dateField);
        form.add(new JLabel("Message:"));
        form.add(new JScrollPane(msgArea));
        
        JButton addBtn = new JButton("Add Reminder");
        addBtn.addActionListener(e -> {
            if(!titleField.getText().isEmpty() && !dateField.getText().isEmpty()) {
                ReminderManager.addReminder(titleField.getText(), dateField.getText(), msgArea.getText());
                titleField.setText("");
                dateField.setText("");
                msgArea.setText("");
                loadReminders();
            }
        });
        
        panel.add(form, BorderLayout.NORTH);
        panel.add(addBtn, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTabbedPane logsTabs = new JTabbedPane();
        
        fileModel = new DefaultTableModel(new String[]{"ID","File Name","Type","Destination"},0);
        fileTable = new JTable(fileModel);
        logsTabs.addTab("File Actions", new JScrollPane(fileTable));
        
        reminderModel = new DefaultTableModel(new String[]{"ID","Title","DateTime","Message","Done"},0);
        reminderTable = new JTable(reminderModel);
        logsTabs.addTab("Reminders", new JScrollPane(reminderTable));
        
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> { loadFileLogs(); loadReminders(); });
        
        panel.add(logsTabs, BorderLayout.CENTER);
        panel.add(refreshBtn, BorderLayout.SOUTH);
        return panel;
    }
    
    private void loadFileLogs() {
        fileModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM file_actions ORDER BY action_id DESC")) {
            while(rs.next()) {
                fileModel.addRow(new Object[]{rs.getInt("action_id"), rs.getString("file_name"), rs.getString("file_type"), rs.getString("dest_path")});
            }
        } catch(Exception e) {}
    }
    
    private void loadReminders() {
        reminderModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM reminders ORDER BY reminder_id DESC")) {
            while(rs.next()) {
                reminderModel.addRow(new Object[]{rs.getInt("reminder_id"), rs.getString("title"), rs.getString("reminder_datetime"), rs.getString("message"), rs.getBoolean("is_completed")});
            }
        } catch(Exception e) {}
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}