import java.io.*;
import java.sql.*;
import java.nio.file.*;

public class FileOrganizer {
    
    public static void organizeDesktop() {
        String desktopPath = System.getProperty("user.home") + "/Desktop";
        File desktop = new File(desktopPath);
        File[] files = desktop.listFiles();
        
        if (files == null) return;
        
        for (File file : files) {
            if (file.isFile()) {
                String ext = getExtension(file.getName());
                String folder = getFolder(ext);
                moveFile(file, desktopPath, folder);
            }
        }
        System.out.println("Desktop organized!");
    }
    
    private static String getExtension(String fileName) {
        int lastDot = fileName.lastIndexOf(".");
        if (lastDot > 0) return fileName.substring(lastDot + 1).toLowerCase();
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
    
    private static void moveFile(File file, String desktopPath, String folderName) {
        try {
            File destDir = new File(desktopPath + "/" + folderName);
            if (!destDir.exists()) destDir.mkdir();
            
            Path source = file.toPath();
            Path dest = new File(destDir, file.getName()).toPath();
            Files.move(source, dest, StandardCopyOption.REPLACE_EXISTING);
            
            String sql = "INSERT INTO file_actions (file_name, file_type, source_path, dest_path) VALUES (?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, file.getName());
                pstmt.setString(2, getExtension(file.getName()));
                pstmt.setString(3, file.getAbsolutePath());
                pstmt.setString(4, dest.toString());
                pstmt.executeUpdate();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}