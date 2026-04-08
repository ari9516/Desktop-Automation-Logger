import java.io.*;
import java.sql.*;
import java.nio.file.*;
import javax.swing.*;

public class FileOrganizer {
    
    public static void organizeDesktop() {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        File desktop = new File(desktopPath);
        File[] files = desktop.listFiles();
        
        if (files == null || files.length == 0) {
            JOptionPane.showMessageDialog(null, "No files found on desktop!");
            return;
        }
        
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                String ext = getExtension(file.getName());
                String folder = getFolder(ext);
                if (moveFile(file, desktopPath, folder)) {
                    count++;
                }
            }
        }
        JOptionPane.showMessageDialog(null, "Organized " + count + " files successfully!");
    }
    
    private static String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) {
            return fileName.substring(lastDot + 1).toLowerCase();
        }
        return "others";
    }
    
    private static String getFolder(String ext) {
        if (ext.matches("jpg|jpeg|png|gif|bmp")) return "Images";
        if (ext.matches("pdf|doc|docx|txt|xlsx|pptx")) return "Documents";
        if (ext.matches("mp4|mkv|avi|mov")) return "Videos";
        if (ext.matches("mp3|wav|aac")) return "Music";
        if (ext.matches("exe|msi")) return "Installers";
        if (ext.matches("zip|rar|7z")) return "Archives";
        return "Others";
    }
    
    private static boolean moveFile(File file, String desktopPath, String folderName) {
        try {
            File destDir = new File(desktopPath + "/" + folderName);
            if (!destDir.exists()) destDir.mkdir();
            
            Path source = file.toPath();
            Path dest = new File(destDir, file.getName()).toPath();
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
            
            // Log to database with undo tracking
            String sql = "INSERT INTO file_actions (file_name, file_type, source_path, dest_path, is_undone) VALUES (?, ?, ?, ?, FALSE)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, file.getName());
                pstmt.setString(2, getExtension(file.getName()));
                pstmt.setString(3, file.getAbsolutePath());
                pstmt.setString(4, dest.toString());
                pstmt.executeUpdate();
            }
            return true;
        } catch (Exception e) {
            System.out.println("Error moving: " + file.getName());
            return false;
        }
    }
    
    // NEW METHOD: Undo last organization
    public static void undoLastOrganization() {
        String sql = "SELECT action_id, file_name, source_path, dest_path FROM file_actions WHERE is_undone = FALSE ORDER BY action_id DESC LIMIT 20";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            int undoneCount = 0;
            
            while (rs.next()) {
                int actionId = rs.getInt("action_id");
                String fileName = rs.getString("file_name");
                String sourcePath = rs.getString("source_path");
                String destPath = rs.getString("dest_path");
                
                // Move file back to original location
                File destFile = new File(destPath);
                File sourceFile = new File(sourcePath);
                
                // Make sure source directory exists
                sourceFile.getParentFile().mkdirs();
                
                // Move the file back
                if (destFile.exists()) {
                    Files.move(destFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                    
                    // Mark as undone in database
                    String updateSql = "UPDATE file_actions SET is_undone = TRUE WHERE action_id = ?";
                    try (PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                        pstmt.setInt(1, actionId);
                        pstmt.executeUpdate();
                    }
                    undoneCount++;
                }
            }
            
            if (undoneCount > 0) {
                JOptionPane.showMessageDialog(null, "Undo completed! " + undoneCount + " files restored to original locations.");
            } else {
                JOptionPane.showMessageDialog(null, "No files to undo! All moves have already been undone.");
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database error: " + e.getMessage());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "File error: " + e.getMessage());
        }
    }
    
    // NEW METHOD: Undo specific file by ID
    public static void undoFileById(int actionId, String sourcePath, String destPath) {
        try {
            File destFile = new File(destPath);
            File sourceFile = new File(sourcePath);
            sourceFile.getParentFile().mkdirs();
            
            if (destFile.exists()) {
                Files.move(destFile.toPath(), sourceFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                
                String updateSql = "UPDATE file_actions SET is_undone = TRUE WHERE action_id = ?";
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement pstmt = conn.prepareStatement(updateSql)) {
                    pstmt.setInt(1, actionId);
                    pstmt.executeUpdate();
                }
            }
        } catch (Exception e) {
            System.out.println("Error undoing file: " + e.getMessage());
        }
    }
}
