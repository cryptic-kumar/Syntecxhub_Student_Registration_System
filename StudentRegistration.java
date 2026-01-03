import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;

public class StudentRegistration {

    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("---------------------------------------------");
        System.out.println("      SYNTECXHUB REGISTRATION SYSTEM      ");
        System.out.println("---------------------------------------------");
        System.out.println(">> System Initializing...");
        loadingAnimation(); // Fake loading effect

        while (true) {
            System.out.println("\n============= MAIN MENU =============");
            System.out.println("1.   Add New Student");
            System.out.println("2.   View All Students");
            System.out.println("3.   Search Student by ID");
            System.out.println("4.   Update Student Details");
            System.out.println("5.   Delete Student Record");
            System.out.println("6.   Exit");
            System.out.println("=====================================");
            System.out.print(">> Select an option (1-6): ");

            if (scanner.hasNextInt()) {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Fix newline bug

                switch (choice) {
                    case 1: addStudent(); break;
                    case 2: viewStudents(); break;
                    case 3: searchStudent(); break; // New Feature
                    case 4: updateStudent(); break;
                    case 5: deleteStudent(); break;
                    case 6: 
                        System.out.println("\n>> Saving data... Goodbye! ");
                        return;
                    default: System.out.println("\n[!] Invalid Option. Try again.");
                }
            } else {
                System.out.println("\n[!] Please enter a number.");
                scanner.next(); // Clear invalid input
            }
            
            // Pause before showing menu again
            System.out.print("\nPress Enter to continue...");
            scanner.nextLine();
        }
    }

    // --- 1. ADD STUDENT ---
    public static void addStudent() {
        System.out.println("\n---   Add New Student ---");
        try {
            System.out.print("Enter Name: ");
            String name = scanner.nextLine();
            
            System.out.print("Enter Email: ");
            String email = scanner.nextLine();
            
            System.out.print("Enter Course: ");
            String course = scanner.nextLine();
            
            System.out.print("Enter Fee (e.g. 5000): ");
            double fee = scanner.nextDouble();

            Connection con = DBConnection.getConnection();
            String sql = "INSERT INTO students (name, email, course, fee) VALUES (?, ?, ?, ?)";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, course);
            pstmt.setDouble(4, fee);

            System.out.print(">> Saving Record");
            loadingAnimation();
            
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("\n[Success] Student Registered Successfully!  ");
            con.close();

        } catch (Exception e) {
            System.out.println("\n[Error] " + e.getMessage());
        }
    }

    // --- 2. VIEW STUDENTS (Formatted Table) ---
    public static void viewStudents() {
        try {
            Connection con = DBConnection.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            System.out.println("\n---   Student Records ---");
            // Table Header
            System.out.printf("%-5s %-20s %-25s %-15s %-10s\n", "ID", "Name", "Email", "Course", "Fee");
            System.out.println("-------------------------------------------------------------------------------");
            
            boolean found = false;
            while (rs.next()) {
                found = true;
                System.out.printf("%-5d %-20s %-25s %-15s Rs.%-10.2f\n",
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("email"),
                    rs.getString("course"),
                    rs.getDouble("fee")
                );
            }
            if (!found) System.out.println(">> No records found in database.");
            
            con.close();
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // --- 3. SEARCH STUDENT (New Feature) ---
    public static void searchStudent() {
        System.out.print("\nEnter Student ID to Search: ");
        int id = scanner.nextInt();
        
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM students WHERE id = ?";
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setInt(1, id);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("\n---   Record Found ---");
                System.out.println("ID:     " + rs.getInt("id"));
                System.out.println("Name:   " + rs.getString("name"));
                System.out.println("Email:  " + rs.getString("email"));
                System.out.println("Course: " + rs.getString("course"));
                System.out.println("Fee:    Rs." + rs.getDouble("fee"));
            } else {
                System.out.println("\n[!] Student with ID " + id + " not found.");
            }
            con.close();
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // --- 4. UPDATE STUDENT ---
    public static void updateStudent() {
        System.out.print("\nEnter Student ID to Update: ");
        int id = scanner.nextInt();
        scanner.nextLine(); 

        System.out.print("Enter New Course Name: ");
        String newCourse = scanner.nextLine();

        try {
            Connection con = DBConnection.getConnection();
            String sql = "UPDATE students SET course = ? WHERE id = ?";
            
            PreparedStatement pstmt = con.prepareStatement(sql);
            pstmt.setString(1, newCourse);
            pstmt.setInt(2, id);

            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("\n[Success] Course Updated!  ");
            else System.out.println("\n[!] ID Not Found.");
            
            con.close();
        } catch (Exception e) {
            System.out.println("[Error] " + e.getMessage());
        }
    }

    // --- 5. DELETE STUDENT (With Confirmation) ---
    public static void deleteStudent() {
        System.out.print("\nEnter Student ID to Delete: ");
        int id = scanner.nextInt();

        // Safety Check
        System.out.print(">> Are you sure you want to delete ID " + id + "? (yes/no): ");
        String confirm = scanner.next();

        if (confirm.equalsIgnoreCase("yes")) {
            try {
                Connection con = DBConnection.getConnection();
                String sql = "DELETE FROM students WHERE id = ?";
                
                PreparedStatement pstmt = con.prepareStatement(sql);
                pstmt.setInt(1, id);

                int rows = pstmt.executeUpdate();
                if (rows > 0) System.out.println("\n[Success] Record Deleted.  ");
                else System.out.println("\n[!] ID Not Found.");
                
                con.close();
            } catch (Exception e) {
                System.out.println("[Error] " + e.getMessage());
            }
        } else {
            System.out.println(">> Operation Cancelled.");
        }
    }

    // Helper for loading animation
    public static void loadingAnimation() {
        try {
            for (int i = 0; i < 3; i++) {
                System.out.print(".");
                Thread.sleep(500); // 0.5 second pause
            }
        } catch (InterruptedException e) {
            // Ignore
        }
    }
}