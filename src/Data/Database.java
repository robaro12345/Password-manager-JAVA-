package Data;

import java.sql.*;
import java.util.Scanner;

public class Database {
    Scanner sc = new Scanner(System.in);
    public String url = "jdbc:postgresql://localhost:5432/Login";
    public String usersql = "postgres";
    public String passwordsql = "12345";
    public Connection connection = null;
    public String userVerified = "";
    public void App() throws SQLException {
        connection = DriverManager.getConnection(url, usersql, passwordsql);
    }
    public void searchApplication() {
        System.out.println("Search Element");
        System.out.println("Please enter the name of the application");
        Scanner sc = new Scanner(System.in);
        String name = sc.next();
        String query = "SELECT username, password FROM \"" + userVerified + "\" WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                System.out.println("+------------------+------------------+");
                System.out.println("Username: " + username + "\nPassword: " + password);
                System.out.println("+------------------+------------------+");
            } else System.out.println("No such name was found ");
            if (rs.getString("password").length() < 8) System.out.println("!!!Your password is less than 8 characters It would be better if you update it.!!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void Addelement(){
        System.out.println("New Element");
        System.out.println("Enter the name of the application");
        String elementname = sc.next();
        System.out.println("Enter the Username");
        String username = sc.next();
        System.out.println("Enter the Password");
        String password = sc.next();

        String insertUserSQL = "INSERT INTO \"" + userVerified + "\" (name, username, password) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(insertUserSQL)) {
            pstmt.setString(1, elementname);
            pstmt.setString(2, username);
            pstmt.setString(3, password);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println("Password Added");
            if (password.length() < 8) System.out.println("!!!Your password is less than 8 characters It would be better if you update it.!!!");
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void Deleteelement(){
        System.out.println("Delete Element");
        System.out.println("Please enter the name of the application");
        String name = sc.next();
        System.out.println(userVerified);
        String deleteSQL = "DELETE FROM \"" + userVerified + "\" WHERE name = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(deleteSQL)) {
            pstmt.setString(1, name);
            int execute = pstmt.executeUpdate();
            if (execute > 0) {
                System.out.println("Entry Name:"+name+" is Deleted");
            } else {
                System.out.println("No such name was found ");
            }
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void NameofAllApp(){
        String Select = "SELECT name FROM \""+userVerified+"\"";
        try (PreparedStatement pstmt = connection.prepareStatement(Select)) {
            ResultSet execute = pstmt.executeQuery();
            if (execute.isBeforeFirst()){
                System.out.println("+--------+Application names+--------+");
                while (execute.next()){
                    System.out.println("\n"+execute.getString("name"));
                }
            }
            else System.out.println("No Application name Found");
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void UpdatePassword(){
        System.out.println("Please enter the name of the application");
        String name = sc.next();
        System.out.println("Please enter the What would you like to change?\n1 for password \n2 for UserName\n3 for Both");
        int Ap = sc.nextInt();
        String Selectpassword = "UPDATE \""+userVerified+"\" SET password = ? WHERE name = ?";
        String Selectusername = "UPDATE \""+userVerified+"\" SET username = ? WHERE name = ?";
        String SelectBoth = "UPDATE \""+userVerified+"\" SET username = ?,password = ? WHERE name = ?";
        switch (Ap){
            case 1:{
                System.out.println("What would the new password be? ");
                String password = sc.next();
                try(PreparedStatement pstmt = connection.prepareStatement(Selectpassword)){
                    pstmt.setString(1,password);
                    pstmt.setString(2,name);
                    int Effect = pstmt.executeUpdate();
                    if(Effect > 0)System.out.println("Password updated successfully.");
                    else System.out.println("Name not found");
                    if (password.length() < 8) System.out.println("!!!Your password is less than 8 characters It would be better if you update it.!!!");
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                break;
            }
            case 2:{
                System.out.println("What would the new username be? ");
                String username = sc.next();
                try(PreparedStatement pstmt = connection.prepareStatement(Selectusername)){
                    pstmt.setString(1,username);
                    pstmt.setString(2,name);
                    int Effect = pstmt.executeUpdate();
                    if(Effect > 0)System.out.println("Username updated successfully.");
                    else System.out.println("Name not found");
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                break;
            }
            case 3:{
                System.out.println("What would the new username be? ");
                String username = sc.next();
                System.out.println("What would the new password be? ");
                String password = sc.next();
                try(PreparedStatement pstmt = connection.prepareStatement(SelectBoth)){
                    pstmt.setString(1,username);
                    pstmt.setString(2,password);
                    pstmt.setString(3,name);
                    int Effect = pstmt.executeUpdate();
                    if(Effect > 0)System.out.println("Username and Password updated successfully.");
                    else System.out.println("Name not found");
                    if (password.length() < 8) System.out.println("!!!Your password is less than 8 characters It would be better if you update it.!!!");
                }catch (SQLException e){
                    System.out.println(e.getMessage());
                }
                break;
            }
        }
    }
}
