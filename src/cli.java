import java.util.*;
import java.sql.*;
class DataBase{
    String url = "jdbc:postgresql://localhost:5432/Login";
    String usersql = "postgres";
    String passwordsql = "12345";
    Connection connection = null;
    String userVerified = "";

}
public class cli {
    static Scanner sc = new Scanner(System.in);
    static DataBase c1 = new DataBase();
    private void searchApplication() {
        System.out.println("Search Element");
        System.out.println("Please enter the name of the application");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        String query = "SELECT username, password FROM \"" + c1.userVerified + "\" WHERE name = ?";
        try (PreparedStatement pstmt = c1.connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    System.out.println("Username: " + username + "\nPassword: " + password);
                } else {
                    System.out.println("No data found for the given name.");
                }
            }
        } catch (SQLException e) {
            System.out.println("An error occurred while querying the database: " + e.getMessage());
        }
    }
    private void Addelement(){
        System.out.println("New Element");
        System.out.println("Enter the name of the application");
        String elementname = sc.next();
        System.out.println("Enter the Username");
        String username = sc.next();
        System.out.println("Enter the Password");
        String password = sc.next();

        String insertUserSQL = "INSERT INTO \"" + c1.userVerified + "\" (name, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = c1.connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, elementname);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " row(s) inserted.");
        }
        catch (SQLException e) {
            System.out.println("An error occurred while querying the database: " + e.getMessage());
        }
    }
    private void Deleteelement(){
        System.out.println("Delete Element");
        System.out.println("Please enter the name of the application");
        String name = sc.next();

        String deleteSQL = "DELETE FROM \"" + c1.userVerified + "\" WHERE name = ?";
        try (PreparedStatement pstmt = c1.connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, name);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println(rowsAffected + " row(s) deleted.");
            } else {
                System.out.println("No application found with the given name.");
            }
        }catch (SQLException e) {
            System.out.println("An error occurred while querying the database: " + e.getMessage());
        }
    }
    public static void main(String[] args) {

        try {
            c1.connection = DriverManager.getConnection(c1.url, c1.usersql, c1.passwordsql);
            if (c1.connection != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
                return;
            }

            boolean check = false;

            while (!check) {
                System.out.println("Are you a new User\n1 for Yes\n2 for No");
                int l = sc.nextInt();
                System.out.println("Please enter your Username");
                String user = sc.next();
                System.out.println("Please enter your password");
                String passkey = sc.next();

                if (l == 2) {
                    // Check user credentials
                    PreparedStatement pstmt = null;
                    ResultSet rs = null;
                    try {
                        String query = "SELECT password FROM users WHERE name = ?";
                        pstmt = c1.connection.prepareStatement(query);
                        pstmt.setString(1, user);
                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            String password = rs.getString("password");
                            if (passkey.equals(password)) {
                                c1.userVerified = user;
                                check = true;
                            } else {
                                System.out.println("Incorrect password.");
                            }
                        } else {
                            System.out.println("User not found.");
                        }
                    } finally {
                        if (rs != null) rs.close();
                        if (pstmt != null) pstmt.close();
                    }

                } else if (l == 1) {
                    System.out.println("Please Confirm your password");
                    String pass = sc.next();

                    if (passkey.equals(pass)) {
                        String insertUserSQL = "INSERT INTO users (name, password) VALUES (?, ?)";
                        try (PreparedStatement pstmt = c1.connection.prepareStatement(insertUserSQL)) {
                            pstmt.setString(1, user);
                            pstmt.setString(2, passkey);
                            int rowsAffected = pstmt.executeUpdate();
                            System.out.println(rowsAffected + " row(s) inserted.");
                        }

                        // Create user-specific table
                        String createTableSQL = "CREATE TABLE IF NOT EXISTS \"" + user + "\" (" +
                                "name VARCHAR(255), " +
                                "username VARCHAR(255), " +
                                "password VARCHAR(255));";

                        try (Statement stmt = c1.connection.createStatement()) {
                            stmt.execute(createTableSQL);
                            System.out.println("Table '" + user + "' created or already exists.");
                        }

                        c1.userVerified = user;
                        check = true;
                    } else {
                        System.out.println("Passwords do not match.");
                    }
                }
            }

            boolean run = true;
            cli c2 = new cli();
            while (run) {
                System.out.println("What would you like to do?\n1 for Search\n2 for adding new password\n3 to remove a password\n4 to Exit");
                int h = sc.nextInt();

                switch (h) {
                    case 1: {
                        c2.searchApplication();
                        break;
                    }

                    case 2: {
                        c2.Addelement();
                        break;
                    }

                    case 3: {
                        c2.Deleteelement();
                        break;
                    }

                    case 4: {
                        run = false;
                        break;
                    }

                    default:
                        System.out.println("Invalid input");
                        break;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (c1.connection != null && !c1.connection.isClosed()) {
                    c1.connection.close();
                }
                sc.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}


