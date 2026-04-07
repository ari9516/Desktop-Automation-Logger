# 🖥️ Desktop Activity & Task Automation Logger

> **File Organization + Smart Reminders + JDBC Logging**  
> A Java desktop application that automates file management and task reminders with MySQL database persistence.

![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)
![JDBC](https://img.shields.io/badge/JDBC-MySQL-blue?style=flat-square&logo=mysql)
![Swing](https://img.shields.io/badge/GUI-Swing%20%26%20AWT-6DB33F?style=flat-square&logo=java)
![Database](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat-square&logo=mysql)
![XAMPP](https://img.shields.io/badge/Server-XAMPP-FB7A24?style=flat-square&logo=xampp)

---

## 📋 Table of Contents

- [About](#-about)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [System Architecture](#-system-architecture)
- [Database Schema](#-database-schema)
- [Installation & Setup](#-installation--setup)
- [How to Run](#-how-to-run)
- [Project Structure](#-project-structure)
- [Screenshots](#-screenshots)
- [Future Enhancements](#-future-enhancements)
- [Author](#-author)

---

## 🧠 About

**Desktop Activity & Task Automation Logger** is a Java-based desktop application that solves two common problems:

1. **Messy Desktop** — Automatically organizes scattered files into categorized folders
2. **Forgotten Tasks** — Schedule reminders with pop-up notifications at exact times

All activities are persistently logged to a **MySQL database** using **JDBC**, providing a complete audit trail of file operations and completed reminders.

---

## ✨ Features

### 📁 File Organizer
- One-click desktop file organization
- Automatic categorization by file extension:
  - 🖼️ Images (jpg, png, gif, bmp)
  - 📄 Documents (pdf, doc, txt, xlsx)
  - 🎬 Videos (mp4, mkv, avi, mov)
  - 🎵 Music (mp3, wav, aac)
  - ⚙️ Installers (exe, msi)
  - 🗜️ Archives (zip, rar, 7z)
- All moves logged to database with timestamps

### ⏰ Reminder System
- Schedule reminders with custom title, date/time, and message
- Background thread checks for due reminders every 30 seconds
- Pop-up notifications at exact scheduled time
- Automatic status tracking (completed/pending)

### 📊 Database Logging
- All file operations stored in `file_actions` table
- All reminders stored in `reminders` table
- Real-time log viewing within the application
- Persistent storage across application restarts

### 🖥️ User Interface
- Clean tabbed interface using Swing components
- Responsive design with JTable for log display
- Color-coded buttons for better user experience

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Programming Language | Java 17+ |
| GUI Framework | Swing & AWT |
| Database Connectivity | JDBC |
| Database | MySQL 8.0+ |
| Local Server | XAMPP |
| Version Control | Git & GitHub |

---

## 🏗 System Architecture

```
┌─────────────────────────────────────────────────┐
│              Presentation Layer                 │
│         Java Swing / AWT GUI (JFrame)           │
│    ┌──────────┐ ┌──────────┐ ┌──────────────┐   │
│    │  File    │ │Reminders │ │  View Logs   │   │
│    │Organizer │ │   Tab    │ │     Tab      │   │
│    └──────────┘ └──────────┘ └──────────────┘   │
└─────────────────────┬───────────────────────────┘
                      │ Method Calls
┌─────────────────────▼───────────────────────────┐
│              Business Logic Layer               │
│  ┌────────────┐ ┌────────────┐ ┌─────────────┐  │
│  │FileOrganizer│ │ReminderMgr │ │ MainGUI    │  │
│  │.organize() │ │.check()    │ │ Load Logs   │  │
│  └────────────┘ └────────────┘ └─────────────┘  │
└─────────────────────┬───────────────────────────┘
                      │ JDBC
┌─────────────────────▼───────────────────────────┐
│                Data Layer                       │
│              MySQL Database                     │
│  ┌──────────────┐      ┌────────────────────┐   │
│  │file_actions  │      │    reminders       │   │
│  │──────────────│      │────────────────────│   │
│  │action_id(PK) │      │reminder_id(PK)     │   │
│  │file_name     │      │title               │   │
│  │file_type     │      │reminder_datetime   │   │
│  │source_path   │      │message             │   │
│  │dest_path     │      │is_completed        │   │
│  │action_time   │      └────────────────────┘   │
│  └──────────────┘                               │
└─────────────────────────────────────────────────┘
```

---

## 🗄 Database Schema

### Table 1: `file_actions`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| action_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| file_name | VARCHAR(255) | NOT NULL | Name of moved file |
| file_type | VARCHAR(50) | - | File extension |
| source_path | TEXT | NOT NULL | Original location |
| dest_path | TEXT | NOT NULL | New location |
| action_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | When action occurred |

### Table 2: `reminders`

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| reminder_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| title | VARCHAR(200) | NOT NULL | Reminder title |
| reminder_datetime | DATETIME | NOT NULL | Scheduled time |
| message | TEXT | NOT NULL | Reminder message |
| is_completed | BOOLEAN | DEFAULT FALSE | Completion status |

### Table 3: `app_usage` (Future Scope)

| Column | Type | Constraints | Description |
|--------|------|-------------|-------------|
| log_id | INT | PRIMARY KEY, AUTO_INCREMENT | Unique identifier |
| app_name | VARCHAR(255) | NOT NULL | Application name |
| start_time | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP | Start timestamp |
| end_time | TIMESTAMP | NULL | End timestamp |

---

## 🚀 Installation & Setup

### Prerequisites

| Software | Version | Purpose |
|----------|---------|---------|
| Java JDK | 8 or higher | Compile & run Java code |
| XAMPP | 8.2+ | MySQL server |
| MySQL Connector/J | 8.0+ | JDBC driver |

### Step-by-Step Setup

#### 1. Install XAMPP & Start MySQL
```bash
# Download from: https://www.apachefriends.org/
# Open XAMPP Control Panel → Click "Start" next to MySQL
```

#### 2. Create Database
Open phpMyAdmin (`http://localhost/phpmyadmin`) and run:

```sql
CREATE DATABASE desktop_automation;

USE desktop_automation;

CREATE TABLE file_actions (
    action_id INT PRIMARY KEY AUTO_INCREMENT,
    file_name VARCHAR(255) NOT NULL,
    file_type VARCHAR(50),
    source_path TEXT NOT NULL,
    dest_path TEXT NOT NULL,
    action_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE reminders (
    reminder_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    reminder_datetime DATETIME NOT NULL,
    message TEXT NOT NULL,
    is_completed BOOLEAN DEFAULT FALSE
);
```

#### 3. Clone Repository
```bash
git clone https://github.com/ari9516/Desktop-Automation-Logger.git
cd Desktop-Automation-Logger
```

#### 4. Add MySQL Connector JAR
Download from [MySQL Official Site](https://dev.mysql.com/downloads/connector/j/) and place in project folder.

---

## ▶️ How to Run

### Compile
```bash
javac -cp ".;mysql-connector-j-9.6.0.jar" *.java
```

### Run
```bash
java -cp ".;mysql-connector-j-9.6.0.jar" MainGUI
```

### Expected Output
- GUI window opens with 3 tabs
- "Database connected!" appears in console
- Application ready for use

---

## 📁 Project Structure

```
Desktop-Automation-Logger/
│
├── DatabaseConnection.java      # JDBC connection handler
├── FileOrganizer.java           # File organization logic
├── ReminderManager.java         # Reminder scheduling & popups
├── MainGUI.java                 # Swing GUI (JFrame, JTable, JTabbedPane)
├── mysql-connector-j-9.6.0.jar  # MySQL JDBC driver
│
├── database/
│   └── schema.sql               # Database creation script
│
├── screenshots/                 # Application screenshots
│   ├── main-gui.png
│   ├── file-organizer.png
│   ├── reminder-popup.png
│   └── database-logs.png
│
├── README.md                    # Project documentation
└── LICENSE                      # MIT License
```

---

## 📸 Screenshots

### Main GUI Window
![Main GUI](screenshots/main-gui.png)

### File Organizer in Action
![File Organizer](screenshots/file-organizer.png)

### Reminder Pop-up Notification
![Reminder Popup](screenshots/reminder-popup.png)

### Database Logs in phpMyAdmin
![Database Logs](screenshots/database-logs.png)

> *Add actual screenshots after your demo*

---

## 🔮 Future Enhancements

| Feature | Description | Priority |
|---------|-------------|----------|
| ↩️ Undo Last Organization | Revert files to original locations | High |
| 📱 App Usage Tracker | Log which apps are opened and for how long | Medium |
| 📧 Email Notifications | Send reminder alerts via email as backup | Medium |
| 📊 Export Reports | Export logs to CSV or PDF format | Low |
| 🌙 Dark Mode | Add dark theme for better user experience | Low |
| ☁️ Cloud Backup | Backup organized files to Google Drive | Low |

---

## 👤 Author

**Arnab Kumar**

- GitHub: [@ari9516](https://github.com/ari9516)
- Project Repository: [Desktop-Automation-Logger](https://github.com/ari9516/Desktop-Automation-Logger)

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## ⭐ Show Your Support

If you found this project helpful, please give it a ⭐ on GitHub!

---

**Built with ☕ and Java**
