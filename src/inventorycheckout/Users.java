/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorycheckout;

import java.sql.*;
import javax.swing.JTable;
/**
 *
 * @author admin_kromer
 */
public class Users {
     Statement dbConn;
     String ln;
     String fn;
     String dept;
     String email;
     int id;
     
     
     //Constructors
     public Users(Statement conn) {
         dbConn = conn;
     }
     
     public Users (Statement conn, String f, String l, String d,
         String e, int i) {
         dbConn = conn;
         
         this.setLn(l);
         this.setFn(f);
         this.setDept(d);
         this.setEmail(e);
         this.setId(i);
     }

     // Getters and Setters
    public void setId(int id) {
        this.id = id;
    }     

    public void setLn(String ln) {
        this.ln = ln;
    }
    
    public void setFn(String fn) {
        this.fn = fn;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLn() {
        return ln;
    }

    public String getFn() {
        return fn;
    }

    public String getDept() {
        return dept;
    }

    public String getEmail() {
        return email;
    }
    
    public int getId() {
        return id;
    }
     
     public ResultSet printEmployeeList() {
        try {
            ResultSet myRs = dbConn.executeQuery("SELECT LName, FName, Dept, IFNULL(Email, '-') AS Email," +
                    "E_ID FROM employee");
            
            return myRs;
        }
        catch(Exception exc) {
            exc.printStackTrace();
        }
        return null;
    }// End print Users
     
     public int addEmployee() {
        String sql;
        
        sql = "INSERT INTO employee (LName, FName, Dept, Email, E_ID )"
                + " VALUES ('" + ln + "', '" + fn + "', '" + dept + "', '"
                + email + "', '" + id + "');";
        try{
            dbConn.executeUpdate(sql);
        } catch (SQLIntegrityConstraintViolationException dub) {
            return 1;
        } catch (Exception exc) {
            exc.printStackTrace();
            return 2;
        }
        
        return 0;
    }// End addUser
     
    public void removeEmp() {
        String sql = "UPDATE checkout"
                + " SET E_ID=null"
                + " WHERE E_ID=" + getId() + ";";
        
        try{
            dbConn.executeUpdate(sql);
            
            sql = "DELETE FROM employee WHERE E_ID=" + getId() + ";";
            dbConn.executeUpdate(sql); 
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    
    public JTable showDevs() {
        int numRows = 10;
        ResultSet res;
        String sql ="SELECT COUNT(E_ID) FROM checkout WHERE E_ID="
            + getId() + ";";
        try {
            res = dbConn.executeQuery(sql);
            res.next();
            numRows = res.getInt(1);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        sql = "SELECT device.DType, device.Make, device.Model,"
                + "device.D_ID, checkout.opTime"
                + " FROM checkout JOIN device ON checkout.D_ID="
                + "device.D_ID AND checkout.E_ID=" + getId() + ";";
        try{
            res = dbConn.executeQuery(sql);

            int row = 0;
            int col = 0;            
            
            String[][] data = new String[numRows][6];
            String[] colNames = {"#", "Type", "Make", "Model", "ID", "Date"};
            
            while(res.next()) {
                col = 0;
                data[row][col] = Integer.toString(row + 1);
                col++;
                data[row][col] = res.getString("DType");
                col++;
                data[row][col] = res.getString("Make");
                col++;
                data[row][col] = res.getString("Model");
                col++;
                data[row][col] = Integer.toString(res.getInt("D_ID"));
                col++;
                data[row][col] = res.getObject("opTime").toString().substring(0, 10);
                row++;
            }
            JTable results = new JTable(data, colNames);
            results.getColumn(colNames[0]).setPreferredWidth(10);
            results.getColumn(colNames[4]).setPreferredWidth(15);
            return results;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
}// End Class
