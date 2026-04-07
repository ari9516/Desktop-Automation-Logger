# \# Desktop Activity \& Task Automation Logger

# 

# A Java desktop application that automatically organizes desktop files and provides a reminder system with MySQL database logging using JDBC.

# 

# \## Features

# 

# \- 📁 \*\*One-click File Organizer\*\* - Sorts desktop files into Images, Documents, Videos, Music, Installers, Archives folders

# \- ⏰ \*\*Smart Reminder System\*\* - Schedule reminders with pop-up notifications at exact time

# \- 📊 \*\*Database Logging\*\* - All actions logged to MySQL database using JDBC

# \- 🖥️ \*\*User-friendly GUI\*\* - Built with Java Swing \& AWT

# 

# \## Technologies Used

# 

# | Technology | Purpose |

# |------------|---------|

# | Java (JDK 8+) | Core programming language |

# | JDBC | Database connectivity |

# | MySQL | Data persistence |

# | Swing \& AWT | Graphical User Interface |

# | XAMPP | Local MySQL server |

# 

# \## Database Schema

# 

# \### file\_actions Table

# | Column | Type | Description |

# |--------|------|-------------|

# | action\_id | INT | Primary Key |

# | file\_name | VARCHAR(255) | Name of file moved |

# | file\_type | VARCHAR(50) | File extension |

# | source\_path | TEXT | Original location |

# | dest\_path | TEXT | New location |

# | action\_time | TIMESTAMP | When action occurred |

# 

# \### reminders Table

# | Column | Type | Description |

# |--------|------|-------------|

# | reminder\_id | INT | Primary Key |

# | title | VARCHAR(200) | Reminder title |

# | reminder\_datetime | DATETIME | Scheduled time |

# | message | TEXT | Reminder message |

# | is\_completed | BOOLEAN | Status flag |

# 

# \## How to Run

# 

# \### Prerequisites

# \- Java JDK 8 or higher

# \- MySQL Server (XAMPP recommended)

# \- MySQL Connector/J JAR

# 

# \### Steps

# 1\. Start MySQL server in XAMPP

# 2\. Create database and tables using phpMyAdmin

# 3\. Compile: `javac -cp ".;mysql-connector-j-9.6.0.jar" \*.java`

# 4\. Run: `java -cp ".;mysql-connector-j-9.6.0.jar" MainGUI`

# 

# \## Screenshots

# 

# \*(Add after your demo)\*

# 

# \## Future Enhancements

# \- Undo file organization

# \- App usage tracker with time logging

# \- Email notifications for reminders

# \- Export logs to CSV/PDF

# \- Cloud backup integration

# 

# \## Author

# 

# \*\*Arnab\*\*  

# GitHub: \[ari9516](https://github.com/ari9516)

# 

# \## License

# 

# MITDesktop Automation Logger

