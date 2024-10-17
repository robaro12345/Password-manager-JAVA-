import java.util.*;
import java.sql.*;
import Data.*;

public class cli {
    static Scanner sc = new Scanner(System.in);
    static Database c1 = new Database();
    public static void main(String[] args) {
        try {
            c1.App();
            if (c1.connection != null) {
                System.out.println("Connected to the database!");
            } else {
                System.out.println("Failed to connect to the database.");
                return;
            }

            boolean check = false;
            boolean register = false;
            while (!check) {
                System.out.println("Are you a new User\n1 for Yes\n2 for No");
                int l = register?2:sc.nextInt();
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
                                System.out.println("Login Successful");
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
                        try {
                            String Checking = "SELECT 1 FROM users WHERE name = ? LIMIT 1";
                            PreparedStatement pstmt = c1.connection.prepareStatement(Checking);
                            pstmt.setString(1,user);
                            ResultSet rs = pstmt.executeQuery();
                            if(rs.next()){
                                System.out.println("Name is already in database");
                            }else{
                                String insertUserSQL = "INSERT INTO users (name, password) VALUES (?, ?)";
                                try (PreparedStatement pstmt1 = c1.connection.prepareStatement(insertUserSQL)) {
                                    pstmt1.setString(1, user);
                                    pstmt1.setString(2, passkey);
                                    int rowsAffected = pstmt1.executeUpdate();
                                }

                                // Create user-specific table
                                String createTableSQL = "CREATE TABLE IF NOT EXISTS \"" + user + "\" ( name VARCHAR(255),username VARCHAR(255),password VARCHAR(255));";

                                try (Statement stmt = c1.connection.createStatement()) {
                                    stmt.execute(createTableSQL);
                                    System.out.println("User added in Database");
                                }

                                c1.userVerified = user;
                                register = true;
                            }
                        }catch (SQLException e){
                            System.out.println(e.getMessage());
                        }

                    } else {
                        System.out.println("Passwords do not match.");
                    }
                }
            }

            boolean run = true;
            while (run) {
                try {
                    System.out.println("What would you like to do?\n1 for Search\n2 for adding new password\n3 to remove a password\n4 to See Application names Whose passwords are stored\n5 to Update Info\n6 to Exit");
                    int h = sc.nextInt();
                    switch (h) {
                        case 1: {
                            c1.searchApplication();
                            break;
                        }
                        case 2: {
                            c1.Addelement();
                            break;
                        }
                        case 3: {
                            c1.Deleteelement();
                            break;
                        }
                        case 4: {
                            c1.NameofAllApp();
                            break;
                        }
                        case 5: {
                            c1.UpdatePassword();
                            break;
                        }
                        case 6: {
                            run = false;
                            break;
                        }
                        default:
                            System.out.println("Invalid input");
                            break;
                        }
                    }catch (InputMismatchException I){
                        System.out.println(I.getMessage());
                    }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }finally {
            try {
                if (c1.connection != null && !c1.connection.isClosed()) {
                    c1.connection.close();
                }
                sc.close();
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}


