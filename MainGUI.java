import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MainGUI extends JFrame {
    private JTable fileTable, reminderTable;
    private DefaultTableModel fileModel, reminderModel;
    private JLabel statusLabel;
    
    public MainGUI() {
        setTitle("📁 Desktop Automation & Task Manager");
        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Start reminder checker
        new ReminderManager().startReminderChecker();
        
        // Create main container
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create tabs with better colors
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("📁 File Organizer", createOrganizerPanel());
        tabs.addTab("⏰ Reminders", createReminderPanel());
        tabs.addTab("📊 View Logs", createLogsPanel());
        
        // Status bar at bottom
        statusLabel = new JLabel("✅ Ready | MySQL Connected");
        statusLabel.setBorder(BorderFactory.createEtchedBorder());
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        statusLabel.setForeground(new Color(0, 100, 0));
        statusLabel.setBackground(new Color(240, 255, 240));
        statusLabel.setOpaque(true);
        
        mainPanel.add(tabs, BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Load initial data
        loadFileLogs();
        loadReminders();
        
        setVisible(true);
    }
    
    private JPanel createOrganizerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        panel.setBackground(new Color(245, 245, 250));
        
        // Button Panel with two buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setBackground(new Color(245, 245, 250));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Organize Button - GREEN background with BLACK text
        JButton organizeBtn = new JButton("📁 ORGANIZE DESKTOP NOW");
        organizeBtn.setFont(new Font("Arial", Font.BOLD, 22));
        organizeBtn.setBackground(new Color(40, 200, 40));  // Bright Green
        organizeBtn.setForeground(Color.BLACK);  // Black text
        organizeBtn.setFocusPainted(false);
        organizeBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        organizeBtn.setBorder(BorderFactory.createLineBorder(new Color(0, 100, 0), 2));
        organizeBtn.setOpaque(true);
        organizeBtn.addActionListener(e -> {
            statusLabel.setText("⏳ Organizing files...");
            SwingUtilities.invokeLater(() -> {
                FileOrganizer.organizeDesktop();
                loadFileLogs();
                statusLabel.setText("✅ Desktop organized successfully!");
            });
        });
        
        // Undo Button - RED background with WHITE text
        JButton undoBtn = new JButton("↩️ UNDO LAST ORGANIZATION");
        undoBtn.setFont(new Font("Arial", Font.BOLD, 20));
        undoBtn.setBackground(new Color(220, 60, 60));  // Bright Red
        undoBtn.setForeground(Color.WHITE);  // White text
        undoBtn.setFocusPainted(false);
        undoBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        undoBtn.setBorder(BorderFactory.createLineBorder(new Color(100, 0, 0), 2));
        undoBtn.setOpaque(true);
        undoBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "⚠️ This will move the last 20 files back to their original locations.\n\n" +
                "This action CANNOT be undone. Continue?",
                "Confirm Undo", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                statusLabel.setText("⏳ Undoing last organization...");
                SwingUtilities.invokeLater(() -> {
                    FileOrganizer.undoLastOrganization();
                    loadFileLogs();
                    statusLabel.setText("✅ Undo completed successfully!");
                });
            }
        });
        
        buttonPanel.add(organizeBtn);
        buttonPanel.add(undoBtn);
        
        // Info Panel with better visibility
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(new Color(255, 255, 255));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 150, 0), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JLabel titleLabel = new JLabel("📁 Desktop File Organizer", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(0, 100, 0));
        
        JLabel infoLabel = new JLabel("<html><center>" +
            "<p style='font-size:14px; color:#333333;'>📌 Click <b style='color:#006400;'>ORGANIZE</b> to automatically sort files into folders:</p>" +
            "<p style='font-size:16px; color:#006400;'>🖼️ Images &nbsp;|&nbsp; 📄 Documents &nbsp;|&nbsp; 🎬 Videos &nbsp;|&nbsp; 🎵 Music &nbsp;|&nbsp; ⚙️ Installers &nbsp;|&nbsp; 🗜️ Archives</p>" +
            "<br>" +
            "<p style='font-size:13px; color:#CC0000;'><b>↩️ UNDO</b> - Restores the last 20 moved files to their original locations</p>" +
            "<p style='font-size:12px; color:#666666;'><i>📋 All actions are logged to MySQL database via JDBC</i></p>" +
            "</center></html>");
        infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(titleLabel, BorderLayout.NORTH);
        infoPanel.add(infoLabel, BorderLayout.CENTER);
        
        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createReminderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(new Color(245, 245, 250));
        
        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 200), 2),
            "➕ Add New Reminder",
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            new Color(70, 130, 200)
        ));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title field
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.2;
        JLabel titleLabel = new JLabel("📝 Title:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(titleLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        gbc.weightx = 0.8;
        JTextField titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 14));
        titleField.setBackground(new Color(255, 255, 220));
        formPanel.add(titleField, gbc);
        
        // Date & Time field
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.2;
        JLabel dateLabel = new JLabel("📅 Date & Time:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(dateLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        gbc.weightx = 0.8;
        JTextField dateField = new JTextField();
        dateField.setFont(new Font("Arial", Font.PLAIN, 14));
        dateField.setBackground(new Color(255, 255, 220));
        dateField.setToolTipText("Format: YYYY-MM-DD HH:MM:SS");
        formPanel.add(dateField, gbc);
        
        // Example label
        gbc.gridx = 1; gbc.gridy = 2;
        JLabel exampleLabel = new JLabel("Example: 2026-04-08 15:30:00");
        exampleLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        exampleLabel.setForeground(new Color(100, 100, 100));
        formPanel.add(exampleLabel, gbc);
        
        // Message field
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.weightx = 0.2;
        gbc.anchor = GridBagConstraints.NORTH;
        JLabel msgLabel = new JLabel("💬 Message:");
        msgLabel.setFont(new Font("Arial", Font.BOLD, 13));
        formPanel.add(msgLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 3;
        gbc.weightx = 0.8;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea msgArea = new JTextArea(4, 30);
        msgArea.setFont(new Font("Arial", Font.PLAIN, 14));
        msgArea.setBackground(new Color(255, 255, 220));
        msgArea.setLineWrap(true);
        msgArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(msgArea);
        formPanel.add(scrollPane, gbc);
        
        // Add Reminder Button
        gbc.gridx = 1; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton addBtn = new JButton("✅ ADD REMINDER");
        addBtn.setFont(new Font("Arial", Font.BOLD, 16));
        addBtn.setBackground(new Color(70, 130, 200));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(250, 45));
        addBtn.setOpaque(true);
        addBtn.addActionListener(e -> {
            String title = titleField.getText().trim();
            String datetime = dateField.getText().trim();
            String message = msgArea.getText().trim();
            
            if(title.isEmpty() || datetime.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "❌ Please fill Title and Date & Time!",
                    "Missing Fields",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if(message.isEmpty()) {
                message = "No message provided";
            }
            
            if(!datetime.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")) {
                JOptionPane.showMessageDialog(this,
                    "❌ Invalid datetime format!\nUse: YYYY-MM-DD HH:MM:SS",
                    "Format Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            ReminderManager.addReminder(title, datetime, message);
            titleField.setText("");
            dateField.setText("");
            msgArea.setText("");
            loadReminders();
            statusLabel.setText("✅ Reminder added successfully!");
        });
        
        formPanel.add(addBtn, gbc);
        
        // Quick Reminder Examples
        JPanel quickPanel = new JPanel();
        quickPanel.setBackground(new Color(245, 245, 250));
        quickPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 150, 150)),
            "⏩ Quick Examples"
        ));
        quickPanel.setLayout(new FlowLayout());
        
        String[] examples = {"Tomorrow 9:00 AM", "Lunch Time", "Meeting", "Call Mom"};
        for(String ex : examples) {
            JButton exBtn = new JButton(ex);
            exBtn.setFont(new Font("Arial", Font.PLAIN, 12));
            exBtn.setBackground(new Color(200, 220, 240));
            exBtn.setForeground(Color.BLACK);
            exBtn.setFocusPainted(false);
            exBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            exBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(this, 
                    "Example: Set datetime to current time + 5 minutes\n" +
                    "Format: " + getCurrentTimePlusMinutes(5),
                    "Quick Tip",
                    JOptionPane.INFORMATION_MESSAGE);
            });
            quickPanel.add(exBtn);
        }
        
        panel.add(formPanel, BorderLayout.NORTH);
        panel.add(quickPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getCurrentTimePlusMinutes(int minutes) {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime future = now.plusMinutes(minutes);
        return future.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    
    private JPanel createLogsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setBackground(new Color(245, 245, 250));
        
        JTabbedPane logsTabs = new JTabbedPane();
        
        // File logs table with enhanced columns
        fileModel = new DefaultTableModel(new String[]{"ID", "File Name", "Type", "Destination", "Status", "Action"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        
        fileTable = new JTable(fileModel);
        fileTable.setRowHeight(28);
        fileTable.setFont(new Font("Arial", Font.PLAIN, 12));
        fileTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        fileTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        fileTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        fileTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        fileTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        fileTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        fileTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        
        // Add mouse listener for undo button in table
        fileTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int column = fileTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / fileTable.getRowHeight();
                
                if (row < fileTable.getRowCount() && row >= 0 && column == 5) {
                    String action = (String) fileTable.getValueAt(row, 5);
                    if ("↩️ Undo".equals(action)) {
                        int actionId = (int) fileTable.getValueAt(row, 0);
                        String sourcePath = getSourcePathById(actionId);
                        String destPath = (String) fileTable.getValueAt(row, 3);
                        
                        int confirm = JOptionPane.showConfirmDialog(MainGUI.this,
                            "Restore this file to its original location?",
                            "Confirm Undo",
                            JOptionPane.YES_NO_OPTION);
                        
                        if (confirm == JOptionPane.YES_OPTION) {
                            FileOrganizer.undoFileById(actionId, sourcePath, destPath);
                            loadFileLogs();
                            statusLabel.setText("✅ File restored successfully!");
                        }
                    }
                }
            }
        });
        
        logsTabs.addTab("📁 File Actions", new JScrollPane(fileTable));
        
        // Reminders table with enhanced columns
        reminderModel = new DefaultTableModel(new String[]{"ID", "Title", "Date & Time", "Message", "Status"}, 0);
        reminderTable = new JTable(reminderModel);
        reminderTable.setRowHeight(28);
        reminderTable.setFont(new Font("Arial", Font.PLAIN, 12));
        reminderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        reminderTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        reminderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        reminderTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reminderTable.getColumnModel().getColumn(3).setPreferredWidth(300);
        reminderTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        
        logsTabs.addTab("⏰ Reminders", new JScrollPane(reminderTable));
        
        // Stats Panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(new Color(240, 240, 245));
        statsPanel.setBorder(BorderFactory.createEtchedBorder());
        
        JLabel statsLabel = new JLabel();
        updateStatsLabel(statsLabel);
        
        statsPanel.add(statsLabel);
        
        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(240, 240, 245));
        
        JButton refreshBtn = new JButton("🔄 Refresh All");
        refreshBtn.setBackground(new Color(100, 100, 100));
        refreshBtn.setForeground(Color.WHITE);
        refreshBtn.setFocusPainted(false);
        refreshBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshBtn.setFont(new Font("Arial", Font.BOLD, 12));
        refreshBtn.addActionListener(e -> { 
            loadFileLogs(); 
            loadReminders();
            updateStatsLabel(statsLabel);
            statusLabel.setText("✅ Logs refreshed!");
        });
        
        JButton exportBtn = new JButton("📎 Export Logs");
        exportBtn.setBackground(new Color(0, 150, 100));
        exportBtn.setForeground(Color.WHITE);
        exportBtn.setFocusPainted(false);
        exportBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportBtn.setFont(new Font("Arial", Font.BOLD, 12));
        exportBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Export feature coming soon!\nWill export to CSV format.",
                "Coming Soon",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(refreshBtn);
        buttonPanel.add(exportBtn);
        
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(240, 240, 245));
        bottomPanel.add(statsPanel, BorderLayout.WEST);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(logsTabs, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private String getSourcePathById(int actionId) {
        String sql = "SELECT source_path FROM file_actions WHERE action_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, actionId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("source_path");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
    
    private void updateStatsLabel(JLabel label) {
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            
            ResultSet rs1 = stmt.executeQuery("SELECT COUNT(*) FROM file_actions WHERE is_undone = FALSE");
            rs1.next();
            int activeMoves = rs1.getInt(1);
            
            ResultSet rs2 = stmt.executeQuery("SELECT COUNT(*) FROM reminders WHERE is_completed = FALSE");
            rs2.next();
            int pendingReminders = rs2.getInt(1);
            
            label.setText("📊 Stats: " + activeMoves + " active file moves | " + pendingReminders + " pending reminders");
            label.setFont(new Font("Arial", Font.PLAIN, 12));
            label.setForeground(new Color(0, 80, 0));
            
        } catch (SQLException e) {
            label.setText("📊 Stats: Unable to load");
        }
    }
    
    private void loadFileLogs() {
        fileModel.setRowCount(0);
        String sql = "SELECT action_id, file_name, file_type, dest_path, is_undone FROM file_actions ORDER BY action_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                boolean isUndone = rs.getBoolean("is_undone");
                String status = isUndone ? "✅ Undone" : "📁 Active";
                String action = isUndone ? "✔️ Done" : "↩️ Undo";
                
                fileModel.addRow(new Object[]{
                    rs.getInt("action_id"),
                    rs.getString("file_name"),
                    rs.getString("file_type"),
                    rs.getString("dest_path"),
                    status,
                    action
                });
            }
        } catch (Exception e) {
            System.out.println("Error loading file logs: " + e.getMessage());
        }
    }
    
    private void loadReminders() {
        reminderModel.setRowCount(0);
        String sql = "SELECT reminder_id, title, reminder_datetime, message, is_completed FROM reminders ORDER BY reminder_id DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                boolean isCompleted = rs.getBoolean("is_completed");
                String status = isCompleted ? "✅ Completed" : "⏰ Pending";
                
                reminderModel.addRow(new Object[]{
                    rs.getInt("reminder_id"),
                    rs.getString("title"),
                    rs.getString("reminder_datetime"),
                    rs.getString("message"),
                    status
                });
            }
        } catch (Exception e) {
            System.out.println("Error loading reminders: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> new MainGUI());
    }
}
