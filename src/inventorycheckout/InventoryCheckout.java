/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorycheckout;

import com.mysql.cj.jdbc.exceptions.CommunicationsException;
import java.sql.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;
import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class InventoryCheckout extends JFrame {
    
    static String installPath = "C:\\users\\" +
        System.getProperty("user.name") +
        "\\AppData\\local\\AcumenIT Checkout\\";
    
    static Statement conn; //DB connection that gets passed to different classes
    static boolean connCheck;//Returns false if it fails to connect to DB
    
    private String title  = "AcumenIT Checkout";
    
    JFrame frame = new JFrame();
    
// The initial width and height of the window
    private int width     = 1000;
    private int height    = 900;
    
    Font formFont = new Font("SansSerif", Font.PLAIN, 15);
    Font tableFont = new Font("Tahoma", Font.PLAIN, 15);
    Font resultFont = new Font("Verdana", Font.PLAIN, 20);
    Font butFont = new Font("Futura", Font.PLAIN, 17);
    
    JPanel master = new JPanel();
/*------------------------------------------------------------------------------
                            Employee Variables 
------------------------------------------------------------------------------*/    
    Users employee;
    
    JPanel newEmp;    
    JButton newEmpSub, newEmpRes, removeEmp, showDevs, findEmp, empCheck,
        viewDevice, empRet;
    JTextField newEmpFN, newEmpLN, newEmpEmail, newEmpID, findFName, findLName,
        findID;
    JTable tab;
    JComboBox newEmpDept;
    JRadioButton name, eID;
    String fn, ln, dept, email, result;
    String[] departments = {"IT", "Maintenance", "Quality", "Logistics",
        "Production", "Engineering", "Managers", "HR", "Accounting","Other"};
    int id;
    JTextArea results;
    
    static Object[][] empDataBaseInfo;
    static Object[] eTableColumns = {"<html><h4>Last Name</h4></html>",
        "<html><h4>First Name</h4></html>", "<html><h4>Email</h4></html>",
        "<html><h4>Department</h4></html>", "<html><h4>ID #</h4></html>"};
    JButton empListBut;
    JPanel masterPanel = new JPanel(); 
    
    DefaultTableModel eTable = new DefaultTableModel(empDataBaseInfo,
        eTableColumns){
        @Override
        public Class getColumnClass(int column){
            Class returnValue;
            
            if((column >= 0) && (column < getColumnCount())){
                returnValue = getValueAt(0, column).getClass();
            } else {
                returnValue = Object.class;
            }
            return returnValue;
        }
    };
    
    JTable empTable = new JTable(eTable);
    JScrollPane checkList;

/*------------------------------------------------------------------------------
                            Device Variables 
------------------------------------------------------------------------------*/
    Device device;
    
    JPanel newDev;
    JButton newDevSub, newDevRes, viewNotes, print, findDev, removeDev,
        checkoutBut, returnBut, edit;
    JTextField newMake, newModel, newSN, newStag, newMAC;
    JComboBox newType;
    int dID;
    String make, model, sn, stag, mac, type;
    String[] devType = {"Desktop", "Laptop", "Cell Phone", "Desk Phone",
        "Printer", "Scanner", "Monitor", "Other"};
    
     static Object[][] devDataBaseInfo;
    static String[] devColumns = {"<html><h4>Type</h4></html>",
        "<html><h4>Make</h4></html>", "<html><h4>Model</h4></html>",
        "<html><h4>ID</h4></html>", "<html><h4>S/N</h4></html>",
        "<html><h4>Service Tag/IMEI</h4></html>", "<html><h4>MAC</h4></html>",
        "<html><h4>Checked Out</h4></html>"};
    JButton devListBut;
    
    DefaultTableModel dTable = new DefaultTableModel(empDataBaseInfo,
        devColumns){
        @Override
        public Class getColumnClass(int column){
            Class returnValue;
            
            if((column >= 0) && (column < getColumnCount())){
                returnValue = getValueAt(0, column).getClass();
            } else {
                returnValue = Object.class;
            }
            return returnValue;
        }
    };
    
    JTable devTable = new JTable(dTable);
    JScrollPane dScrollPane = new JScrollPane();
    
    JTextField fNameField;
    JTextField lNameField;
/*------------------------------------------------------------------------------
                            InventoryCheckout Constructor
    
    *This is where all of the UI elements are layed out 
------------------------------------------------------------------------------*/
    public InventoryCheckout() {
        splashScreen();
//        frame.setResizable(false);
        File file = new File(installPath);
        //Checks if folder exists in appdata/local. Installs if it doesn't
        if(!file.exists()) {
            install();
        }
        
        // Size of window WxH
        frame.setSize(width, height);
        // Sets the window in the middle of the screen
        frame.setLocationRelativeTo(null);
        // Exits the program on close
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle(title);
        BufferedImage icon = null;

//      Load application icon        
        try
        {
            icon = ImageIO.read(getClass().getResource("resources/icon.jpg"));
        } catch (Exception iconEx) {
            errorLog("Line 144: Error Loading Icon", iconEx);
        }
        frame.setIconImage(icon);
        
//
//                      UI Elements
//
        
        newEmpRes = new JButton("Reset");
        newEmpSub = new JButton("Submit");
        NewEmpButListener newEmpL = new NewEmpButListener();
        
        newEmpRes.addActionListener(newEmpL);
        newEmpSub.addActionListener(newEmpL);
        newEmpRes.requestFocus(true);
        
        JLabel newEmpLabel = new JLabel("<html><b>Add New Employee</b></html>");
        newEmpFN    = new JTextField("First Name", 16);
        newEmpLN    = new JTextField("Last Name", 16);
        newEmpDept  = new JComboBox(departments);
        newEmpEmail = new JTextField("Email", 16);
        newEmpID    = new JTextField("ID #", 16);
        
        onClick focus = new onClick();
        
        newEmpFN.addFocusListener(focus);
        newEmpLN.addFocusListener(focus);
        newEmpEmail.addFocusListener(focus);
        newEmpID.addFocusListener(focus);
        
        NewDevButListener newDevL = new NewDevButListener();
        
        newDevRes = new JButton("Reset");
        newDevSub = new JButton("Submit");
        newDevRes.addActionListener(newDevL);
        newDevSub.addActionListener(newDevL);

        
        JLabel newDevLabel = new JLabel("<html><b>Add New Device</b></html>");
        newType  = new JComboBox (devType);
        newMake  = new JTextField("Make", 16);
        newModel = new JTextField("Model", 16);
        newSN    = new JTextField("S/N", 14);
        newStag  = new JTextField("Service Tag/IMEI", 15);
        newMAC   = new JTextField("MAC Address");
        
        newMake.addFocusListener(focus);
        newModel.addFocusListener(focus);
        newSN.addFocusListener(focus);
        newStag.addFocusListener(focus);
        newMAC.addFocusListener(focus);
        
        newEmpLabel.setFont(formFont);
        newEmpFN.setFont(formFont);
        newEmpLN.setFont(formFont);
        newEmpDept.setFont(formFont);
        newEmpEmail.setFont(formFont);
        newEmpID.setFont(formFont);
        newEmpRes.setFont(butFont);
        newEmpSub.setFont(butFont);
        
        newDevLabel.setFont(formFont);
        newMake.setFont(formFont);
        newModel.setFont(formFont);
        newSN.setFont(formFont);
        newStag.setFont(formFont);
        newType.setFont(formFont);
        newMAC.setFont(formFont);
        newDevRes.setFont(butFont);
        newDevSub.setFont(butFont);
        
/*******************************************************************************
 *                                    LAYOUT                                   *
 ******************************************************************************/     
        masterPanel.setLayout(new GridBagLayout());
//      New Employee Form
        
        Box empBox = Box.createVerticalBox();
        Box empRow1 = Box.createHorizontalBox();
        Box empRow2 = Box.createHorizontalBox();
        Box empRow3 = Box.createHorizontalBox();
        Box empRow4 = Box.createHorizontalBox();
        Box empRow5 = Box.createHorizontalBox();
        Box empRow6 = Box.createHorizontalBox();
        
        Box devBox = Box.createVerticalBox();
        Box devRow1 = Box.createHorizontalBox();
        Box devRow2 = Box.createHorizontalBox();
        Box devRow3 = Box.createHorizontalBox();
        Box devRow4 = Box.createHorizontalBox();
        Box devRow5 = Box.createHorizontalBox();
        Box devRow6 = Box.createHorizontalBox();
        
        empRow1.add(newEmpLabel);
        empRow2.add(newEmpFN);
        empRow2.add(newEmpLN);
        empRow3.add(newEmpDept);
        empRow4.add(newEmpID);
        empRow5.add(newEmpEmail);
        empRow6.add(newEmpRes);
        empRow6.add(Box.createHorizontalStrut(1));
        empRow6.add(newEmpSub);
        
        devRow1.add(newDevLabel);
        devRow2.add(newMake);
        devRow2.add(newModel);
        devRow3.add(newType);
        devRow4.add(newMAC);
        devRow5.add(newSN);
        devRow5.add(newStag);
        devRow6.add(newDevRes);
        devRow6.add(Box.createHorizontalStrut(1));
        devRow6.add(newDevSub);
        
        empBox.add(empRow1);
        empBox.add(empRow2);
        empBox.add(empRow3);
        empBox.add(empRow4);
        empBox.add(empRow5);
        empBox.add(empRow6);
        
        devBox.add(devRow1);
        devBox.add(devRow2);
        devBox.add(devRow3);
        devBox.add(devRow4);
        devBox.add(devRow5);
        devBox.add(devRow6);
        
        
        addComp(masterPanel, empBox, 1,1,1,6,GridBagConstraints.CENTER,
            GridBagConstraints.NONE);
        addComp(masterPanel, devBox, 2,1,1,6,GridBagConstraints.CENTER,
            GridBagConstraints.NONE);

        
/*******************************************************************************
 *                        Query Results Tables
 ******************************************************************************/
        
        
        TableButListener tListener = new TableButListener();
        empListBut = new JButton("List Employees");
        empListBut.addActionListener(tListener);
        empTable.setAutoCreateRowSorter(true);
        empListBut.setFont(butFont);
        
        devListBut = new JButton("List Devices");
        devListBut.addActionListener(tListener);
        devTable.setAutoCreateRowSorter(true);
        devListBut.setFont(butFont);
        
        checkoutBut = new JButton("Checkout");
        checkoutBut.addActionListener(tListener);
        checkoutBut.setFont(butFont);
        
        Box tButs1 = Box.createHorizontalBox();
        tButs1.add(empListBut);
        tButs1.add(Box.createHorizontalStrut(15));
        tButs1.add(devListBut);
        tButs1.add(Box.createHorizontalStrut(15));
        tButs1.add(checkoutBut);
        
        findEmp = new JButton("Find Employee");
        findEmp.addActionListener(tListener);
        findEmp.setFont(butFont);
        Box tButs2 = Box.createHorizontalBox();
        
        findDev = new JButton("Find Device");
        findDev.addActionListener(tListener);
        findDev.setFont(butFont);
        
        returnBut = new JButton(" Return ");
        returnBut.addActionListener(tListener);
        returnBut.setFont(butFont);
        
        tButs2.add(findEmp);
        tButs2.add(Box.createHorizontalStrut(15));
        tButs2.add(findDev);
        tButs2.add(Box.createHorizontalStrut(13));
        tButs2.add(returnBut);
        
        Box butBox = Box.createVerticalBox();
        butBox.add(tButs1);
        butBox.add(Box.createVerticalStrut(20));
        butBox.add(tButs2);
        
        empTable.setFont(tableFont);
        devTable.setFont(tableFont);
        
        addComp(masterPanel, dScrollPane, 1,7,3,1,GridBagConstraints.CENTER,
            GridBagConstraints.BOTH);


        addComp(masterPanel, butBox, 1,8,3,1, GridBagConstraints.NORTH, 
            GridBagConstraints.NONE);

        frame.add(masterPanel);
        login();
       
//        employee = new Users(conn);        
        printDevTable();
        frame.setVisible(true);
        devTable.setFont(tableFont);
        dScrollPane.setViewportView(devTable);

       // frame.setVisible(true);
//      Displays error message if fails to connect to db on launch
        connCheck(true);
    }// End Constructor
/*------------------------------------------------------------------------------
                                Event Listeners 
------------------------------------------------------------------------------*/
    private class NewEmpButListener implements ActionListener {       
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == newEmpSub) {
                newEmployee();
             }// End if newEmpSub
            else if (e.getSource() == newEmpRes) {
                newEmpFN.setText("First Name");
                newEmpLN.setText("Last Name");
                newEmpDept.setSelectedIndex(0);
                newEmpEmail.setText("Email");
                newEmpID.setText("ID #");
            } // End newEmpRes
        }
    }
   
    private class NewDevButListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {           
            if (e.getSource() == newDevSub) {
                newDevice();
            } // End newDevSub
            else if (e.getSource() == newDevRes) {
                newType.setSelectedIndex(0);
                newMake.setText("Make");
                newModel.setText("Model");
                newSN.setText("S/N");
                newStag.setText("Service Tag/IMEI");
                newMAC.setText("MAC Address");
            } // End newDevRes
        }
    }
   
    private class TableButListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {

            if(e.getSource() == empListBut) {
                if(!connCheck(true))
                    return;
                printEmpTable();
                devTable.clearSelection();
                dScrollPane.setViewportView(empTable);
            }
            else if(e.getSource() == devListBut) {
                if(!connCheck(true))
                    return;
                printDevTable();
                empTable.clearSelection();
                dScrollPane.setViewportView(devTable);                
            }
            else if(e.getSource() == checkoutBut) {
                checkout();
                
            }// End Checkout Button
            else if(e.getSource()== returnBut) {
                returnDevice();
            }// End ReturnBut
            else if(e.getSource() == print){
                device.printBarcode(device.getdID());
            }// End Print
            else if(e.getSource() == findEmp) {
                findEmp();
            }// End findEmp
           
            else if(e.getSource() == findDev) {
                findDevice();
            }// end findDev
            else if(e.getSource() == viewNotes) {
                device.addNote(frame);
            }
            else if(e.getSource() == removeDev) {
                UIManager.put("OptionPane.minimumSize", null);
                int res = JOptionPane.showConfirmDialog(InventoryCheckout.this,
                    "Are You Sure?", "Delete Device",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if(res == JOptionPane.OK_OPTION) {
                    device.removeDev();
                    printDevTable();
                }
            }
            else if(e.getSource() == edit) {
                editDev();
            }
            
       }// End actionPerformed
   }// End ButtonListener
    
    private class EmpListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == removeEmp) {
                UIManager.put("OptionPane.minimumSize", null);
                int result = JOptionPane.showConfirmDialog(
                    InventoryCheckout.this,
                    "Make Sure that all devices have been returned\n"
                        + " before deleteing employee.", "Delete Employee",
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                if(result == JOptionPane.OK_OPTION) {
                    employee.removeEmp();
                    printEmpTable();
                }   
            }
            else if(e.getSource() == empCheck) {
                checkout(employee.getId());
                checkList.setViewportView(devTable);
                tab = employee.showDevs();
                checkList.setViewportView(tab);
            }
            else if(e.getSource() == viewDevice) {
                try {
                    int did = Integer.parseInt(
                        (String)tab.getValueAt(tab.getSelectedRow(), 4));
                    findDevice(did);
                }
                catch(ArrayIndexOutOfBoundsException aiobEX) {}
                catch(Exception es) {
                    es.printStackTrace();
                    errorLog("Error searching device from findEmployee", es);
                }
            }
            else if(e.getSource() == empRet) {
                int did = 
                        Integer.parseInt(
                            (String)tab.getValueAt(tab.getSelectedRow(), 4));
                returnDevice(did);
                checkList.setViewportView(devTable);
                tab = employee.showDevs();
                checkList.setViewportView(tab);
            }
        }
        
    }

    private class onClick implements FocusListener {
        @Override
        public void focusGained(FocusEvent e) {
            if(e.getSource() == newEmpFN) {
               if(newEmpFN.getText().equals("First Name")){
                    newEmpFN.setText("");
                } 
            }
            else if(e.getSource() == newEmpLN) {
                if(newEmpLN.getText().equals("Last Name")){
                    newEmpLN.setText("");
                }
            }
            else if(e.getSource() == newEmpID) {
                if(newEmpID.getText().equals("ID #")){
                    newEmpID.setText("");
                }
            }
            else if(e.getSource() == newEmpEmail) {
                if(newEmpEmail.getText().equals("Email")){
                    newEmpEmail.setText("");
                }
            }
            else if(e.getSource() == newMake) {
                if(newMake.getText().equals("Make")){
                    newMake.setText("");
                }
            }
            else if(e.getSource() == newModel) {
                if(newModel.getText().equals("Model")){
                    newModel.setText("");
                }
            }
            else if(e.getSource() == newMAC) {
                if(newMAC.getText().equals("MAC Address")){
                    newMAC.setText("");
                }
            }
            else if(e.getSource() == newSN) {
                if(newSN.getText().equals("S/N")){
                    newSN.setText("");
                }
            }
            else if(e.getSource() == newStag) {
                if(newStag.getText().equals("Service Tag/IMEI")){
                    newStag.setText("");
                }
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if(e.getSource() == newEmpFN) {
               if(newEmpFN.getText().equals("")){
                    newEmpFN.setText("First Name");
                } 
            }
            else if(e.getSource() == newEmpLN) {
                if(newEmpLN.getText().equals("")){
                    newEmpLN.setText("Last Name");
                }
            }
            else if(e.getSource() == newEmpID) {
                if(newEmpID.getText().equals("")){
                    newEmpID.setText("ID #");
                }
            }
            else if(e.getSource() == newEmpEmail) {
                if(newEmpEmail.getText().equals("")){
                    newEmpEmail.setText("Email");
                }
            }
            else if(e.getSource() == newMake) {
                if(newMake.getText().equals("")){
                    newMake.setText("Make");
                }
            }
            else if(e.getSource() == newModel) {
                if(newModel.getText().equals("")){
                    newModel.setText("Model");
                }
            }
            else if(e.getSource() == newMAC) {
                if(newMAC.getText().equals("")){
                    newMAC.setText("MAC Address");
                }
            }
            else if(e.getSource() == newSN) {
                if(newSN.getText().equals("")){
                    newSN.setText("S/N");
                }
            }
            else if(e.getSource() == newStag) {
                if(newStag.getText().equals("")){
                    newStag.setText("Service Tag/IMEI");
                }
            }
        }
    }    
    
    public class RadioListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            if(e.getSource() == name) {
                findID.setEditable(false);
                findFName.setEditable(true);
                findLName.setEditable(true);
                findID.setText("");
            }
            else if(e.getSource() == eID) {
                findFName.setEditable(false);
                findLName.setEditable(false);
                findID.setEditable(true);
                findFName.setText("");
                findLName.setText("");
            }
        }
    }
   
    public static void main(String[] args) {
        // Changes the theme to match the system
        try {
            UIManager.setLookAndFeel (
                UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            InventoryCheckout.errorLog("Error loading system styles", e);
        }
        new InventoryCheckout();
       
    }// End Main    
    
    
/*******************************************************************************
 *                                METHODS
*******************************************************************************/
/**
 * Connects to the db using login info provided from the login() method
 * 
 * @param user - MySQL Username
 * @param pw   - MySQL Password
 * @return a statement of the connection
 */
    private Statement dbConnect(String user, String pw){
          String url = "jdbc:mysql://";
          String path = installPath + "config.cfg";
          
//      Loads the IP address from the config file
          try {
              FileInputStream in = new FileInputStream(path);
              Properties prop = new Properties();
              prop.load(in);
              
              url += prop.getProperty("IP-Address") + "/inventory";
          }
          catch(Exception e) {
              errorLog("Error loading server IP", e);
              e.printStackTrace();
          }
        try {
            // Get a connection to the database

            Connection myConn = DriverManager.getConnection(url, user, pw);
            
            // Create a statement. The statement is passed to the other classes
            // and is used by the various methods to interact with the db.
            Statement myStmt = myConn.createStatement();
            connCheck = true;
            return myStmt;
            }
        catch(CommunicationsException comEx) {
            JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Error Connecting to database", "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        catch(SQLException invalid) {
            JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Invalid Username or Password", "Invalid Login",
                    JOptionPane.ERROR_MESSAGE);
      
            connCheck = false;
        }
        catch(Exception exc) {
            errorLog("Line 750: Error Connecting to DB", exc);
            connCheck = false;
        }  
        System.out.println("No Connection.");
        return null;
    }
/**
 * Prompts the user for login credentials and passes the to dbConnect().
 * If the login fails, it asks for new credentials
 */ 
    private void login() {
        JTextField user    = new JTextField("");
        JPasswordField pw  = new JPasswordField("");
        JLabel un          = new JLabel("Username: ");
        JLabel pass        = new JLabel("Password: ");

        user.setFont(formFont);
        un.setFont(formFont);
        pass.setFont(formFont);

        Box loginBox = Box.createVerticalBox();
        Box unBox = Box.createHorizontalBox();
        Box pwBox = Box.createHorizontalBox();
        unBox.add(un);
        unBox.add(user);
        pwBox.add(pass);
        pwBox.add(pw);

        loginBox.add(unBox);
        loginBox.add(Box.createVerticalStrut(3));
        loginBox.add(pwBox);
       
        int response;
       
        do{
            response = JOptionPane.showConfirmDialog(InventoryCheckout.this,
                loginBox,"Login", JOptionPane.OK_CANCEL_OPTION, 
                JOptionPane.PLAIN_MESSAGE);
           
            if(response == JOptionPane.CANCEL_OPTION ||
            response == JOptionPane.CLOSED_OPTION) {
                System.exit(0);
            }
            conn = dbConnect(user.getText(), pw.getText());
           
           
        }
        while (!connCheck);
    }
   
/**
 * Adds a component to a grid bag layout and adds the layout to the panel
 * @param panel - The panel to be added to
 * @param comp - The component to be added
 * @param xpos - X position in the grid
 * @param ypos - Y position in the grid
 * @param compWidth - Width of the component
 * @param compHeight - Height of the component
 * @param place - Orientation in the grid cell (North, South, East, West, Center
 * @param stretch  - How it should fill the cell (Horizontal, Vertical, Both)
 */    
    private void addComp(JPanel panel, JComponent comp, int xpos, int ypos,
           int compWidth, int compHeight, int place, int stretch) {
       
        GridBagConstraints gridConstraints = new GridBagConstraints();
        
        gridConstraints.gridx = xpos;
        gridConstraints.gridy = ypos;
        gridConstraints.gridwidth = compWidth;
        gridConstraints.gridheight = compHeight;
        gridConstraints.weightx = 100;
        gridConstraints.weighty = 100;
        gridConstraints.insets = new Insets(5,5,5,5);
        gridConstraints.anchor = place;
        gridConstraints.fill = stretch; 
        
        panel.add(comp, gridConstraints);
    }

/**
 * Adds a new employee to the database.
 */
    private void newEmployee() {
        int error = 0;
// The strings sent to the the Users class must be in quotes or there will
// be an sql error
        try {

            fn = newEmpFN.getText();
            ln =newEmpLN.getText();
            dept = departments[newEmpDept.getSelectedIndex()];
            email =newEmpEmail.getText();
            id = Integer.parseInt(newEmpID.getText());

            if(email.equals("Email")) {
                email = "NULL";
            }

            if(fn.equals("First Name") || ln.equals("Last Name") ||
                    fn.trim().equals("") || ln.trim().equals("")) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                   "Must enter first and last name.",
                   "Error", JOptionPane.ERROR_MESSAGE);
                return;                
            } else {
                employee = new Users(conn,fn, ln, dept, email, id);
                error = employee.addEmployee();
            }
            
            switch(error) {
                case 0:
                    JOptionPane.showMessageDialog(InventoryCheckout.this,
                        "Employee Added!", "Success!", 
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                case 1:
                    JOptionPane.showMessageDialog(InventoryCheckout.this, 
                        "Duplicate ID #.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    break;
                case 2:
                    JOptionPane.showMessageDialog(InventoryCheckout.this, 
                        "Error adding employee to database.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                    break;                    
            }
        }catch (NumberFormatException nEX) {
            JOptionPane.showMessageDialog(InventoryCheckout.this, 
                    "Invalid ID Number",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }

        newEmpFN.setText("First Name");
        newEmpLN.setText("Last Name");
        newEmpDept.setSelectedIndex(0);
        newEmpEmail.setText("Email");
        newEmpID.setText("ID #");
        employee = null;
        printEmpTable();
    }// End newEmployee
    
/**
 * Prints all employees in the DB to a table
 */
    private void printEmpTable() {
        devTable.clearSelection();
        dScrollPane.setViewportView(devTable);
        if (!connCheck(false))
            return;
        clearTable(1);
        try {
            employee = new Users(conn);
            ResultSet empList = employee.printEmployeeList();
            Object[] tempRow;
            while (empList.next()) {
                tempRow = new Object[] {empList.getString("LName"),
                    empList.getString("FName"), empList.getString("Email"),
                    empList.getString("Dept"), empList.getInt("E_ID")};

                eTable.addRow(tempRow);
            }
        }catch (Exception emListButEx) {
            errorLog("Line 817: Error Printing Emp List", emListButEx);
        }
        empTable = new JTable(eTable);
        
        employee = null;
        dScrollPane.setViewportView(empTable);
        empTable.setFont(tableFont);
        
//        Left justifies contents of the employee table
        DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
        ((DefaultTableCellRenderer)empTable.getTableHeader().
            getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
                for(int i = 0; i < empTable.getColumnModel().getColumnCount(); i++) 
            empTable.getColumnModel().getColumn(i).setCellRenderer(rend);
                
//        Sets the width of the ID columns so that other info fits
        
        empTable.getColumn(eTableColumns[4]).setPreferredWidth(20);
   }// End printEmpTable

/**
 * Adds new device to the DB
 */
    private void newDevice() {
        int newDevError;

        try {
            type  = "'" + devType[newType.getSelectedIndex()] + "'";
            make  = "'" + newMake.getText() + "'";
            model = "'" + newModel.getText() + "'";
            sn    = "'" + toUpper(newSN.getText()) + "'";
            stag  = "'" + toUpper(newStag.getText()) + "'";
            mac   = "'" + macFormat(newMAC.getText()) + "'";
            
            if(mac.equals("'y'"))
                return;
            else if(mac.equals("'n'"))
                mac = "'MAC ADDRESS'";

            if(sn.equals("'S/N'")|| sn.trim().equals("''")) {
                sn = "NULL";
            }
            if(stag.equals("'SERVICE TAG/IMEI'")|| stag.trim().equals("''")) {
                stag = "NULL";
            }
            if(mac.equals("'MAC ADDRESS'")|| mac.trim().equals("''")) {
                mac = "NULL";
            }
            if(model.equals("'MODEL'")|| model.trim().equals("''")) {
                model = "NULL";
            }

            device = new Device(conn, type, make, model, sn, stag, mac);

            if (make.equals("'Make'") || make.trim().equals("''") ||
                model.equals("'Model'") || model.trim().equals("''")) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                   "Must enter valid make and model",
                   "Error", JOptionPane.ERROR_MESSAGE);
                newDevError = 3;
            } else {
                newDevError = device.addDevice();
            }

            if (newDevError == 0) {
                JOptionPane.showMessageDialog(InventoryCheckout.this,
                        "Device Added!", "Success!", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
            else if(newDevError == 1) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                   "SQL Error adding device.",
                   "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            else if(newDevError == 2) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                 "S/N, Service Tag, and MAC address must be unique. " + 
                     "Make sure this device isn't registered." ,
                 "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (Exception newDevEx) {
            JOptionPane.showMessageDialog(InventoryCheckout.this, 
                 "Error adding the device",
                 "Error", JOptionPane.ERROR_MESSAGE);
            errorLog("line 462: Error adding device", newDevEx);
            return;
        }

        newType.setSelectedIndex(0);
        newMake.setText("Make");
        newModel.setText("Model");
        newSN.setText("S/N");
        newStag.setText("Service Tag/IMEI");
        newMAC.setText("MAC Address");

        String sql = "SELECT MAX(D_ID) FROM device;";

        try{
            ResultSet res = conn.executeQuery(sql);
            res.next();
            device = new Device(conn);
            device.printBarcode(res.getInt(1));
        }catch (Exception printLabelEx) {
            System.out.println("Error printing new device label");
            errorLog("Line 485: Error printing new device label", printLabelEx);
        }
        printDevTable();
    } // End newDevice  
/**
 * Prints all devices in the DB to a table
 */
    private void printDevTable() {
        empTable.clearSelection();
        dScrollPane.setViewportView(empTable);
        if (!connCheck(false))
             return;
        clearTable(2);
        try {
            device = new Device(conn);
            ResultSet devList = device.printDeviceList();
            Object[] tempRow;
            while(devList.next()) {
                tempRow = new Object[]{devList.getString("DType"), 
                    devList.getString("Make"),devList.getString("Model"),
                    devList.getInt("D_ID"), devList.getString("SN"), 
                    devList.getString("Stag"), devList.getString("Mac"),
                    devList.getString("Checkout")};
                dTable.addRow(tempRow);
            }
        }catch(Exception devListButEx){
            devListButEx.printStackTrace();
        }
        devTable = new JTable(dTable);
        device = null;
        dScrollPane.setViewportView(devTable);
        devTable.setFont(tableFont);
       
//      Left justifies contents of the devTable
        ((DefaultTableCellRenderer)devTable.getTableHeader().
            getDefaultRenderer()).setHorizontalAlignment(JLabel.LEFT);
      
        DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
        rend.setHorizontalAlignment(JLabel.LEFT);
        
        for(int i = 0; i < devTable.getColumnModel().getColumnCount(); i++) 
            devTable.getColumnModel().getColumn(i).setCellRenderer(rend);
        
        //Sets width of columns so that all info fits
        devTable.getColumn(devColumns[3]).setPreferredWidth(1);//ID
        devTable.getColumn(devColumns[7]).setPreferredWidth(1);//Checkout
       
   }// End printDevTable
    
/**
 * Clears the employee or device table depending on table provided
 * @param ref - 1 for employee table, 2 for device table
 */ 
    private void clearTable(int ref) {
       if (ref == 1) {
           for(int i = eTable.getRowCount() - 1; i > -1; i--) {
               eTable.removeRow(i);
           }    
       } else if(ref ==2) {
           for(int i = dTable.getRowCount() - 1; i > -1; i--) {
               dTable.removeRow(i);
           } 
       }
   }
   /**
    * Checks if there is a connection to the database
    * @param displayMessage - True if there should be a pop-up message
    * @return - returns true on good connection, false if fails to connect
    */
    private boolean connCheck(boolean displayMessage) {
        if (!connCheck) {
            if(displayMessage) {
                JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Error connecting to database", "Connection Error",
                    JOptionPane.ERROR_MESSAGE);
           }
            return false;
        }
       return true;
   }
 /**
  *  Logs un-caught exceptions in errorLog.txt
  * @param desc - Custom description of the error
  * @param e - The exception being logged
  */  
    public static void errorLog(String desc, Exception e) {
        String path = installPath + "errorLog.txt";
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        String error = timestamp.toString() + System.lineSeparator() + desc +
                System.lineSeparator() + e.toString() + System.lineSeparator()
                + System.lineSeparator();
        

        try {
            File file = new File(path); // Creates file if it doesnt exist
            FileOutputStream fos = new FileOutputStream(path, true);
            fos.write(error.getBytes());
            
            fos.close();
        }
        catch (Exception s) {
            s.printStackTrace();
        }
   }// End errorLog
    
/**
 *  Finds a device in the database based on ID, SN, Service Tag, or MAC.
 *  Displays all information about the device and allows the user to take notes
 *  or delete the device
 */
    public void findDevice() {
        if(devTable.getSelectedRow() != -1) {
            findDevice((Integer)devTable.getValueAt
                (devTable.getSelectedRow(), 3));
            devTable.clearSelection();
            return;
        }
        
        String[] searchParams = {"Device ID", "S/N", "Service Tag",
            "MAC Address"};
        JLabel instruct  = new JLabel("Search By:");
        JComboBox params = new JComboBox(searchParams);
        JTextField info  = new JTextField("");
        
        instruct.setFont(formFont);
        params.setFont(formFont);
        info.setFont(formFont);

        Box row = Box.createHorizontalBox();
        row.add(params);
        row.add(Box.createHorizontalStrut(2));
        row.add(info);
        Box search = Box.createVerticalBox();
        search.add(instruct);
        search.add(row);
        
        UIManager.put("OptionPane.minimumSize",
                        new Dimension(500,100));
        
        int res = JOptionPane.showConfirmDialog(InventoryCheckout.this,
            search,"Search for Device", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if(res == JOptionPane.OK_OPTION) {
            int select = params.getSelectedIndex();
            String sql = " ";
            String test;
            
// Sets the SQL query based on search parameters. Returns '-' if entry is null            
            switch (select) {
                case 0:
                    sql = "SELECT DType," 
                    + "Make, IFNULL(Model, '-') AS Model, D_ID,"
                    + " IFNULL(SN, '-')AS SN," 
                    + " IFNULL(Stag, '-') AS Stag, " 
                    + "IFNULL(Mac, '-') AS Mac, Checkout, Notes"
                    + " FROM device WHERE D_ID="  
                    + info.getText() + ";";
                    break;
                case 1:
                    sql = "SELECT DType," 
                    + "Make, IFNULL(Model, '-') AS Model, D_ID,"
                    + " IFNULL(SN, '-')AS SN," 
                    + " IFNULL(Stag, '-') AS Stag, " 
                    + "IFNULL(Mac, '-') AS Mac, Checkout, Notes"
                    + " FROM device WHERE SN="  
                    + info.getText() + ";";
                    break;
                case 2:
                    sql = "SELECT DType," 
                    + "Make, IFNULL(Model, '-') AS Model, D_ID,"
                    + " IFNULL(SN, '-')AS SN," 
                    + " IFNULL(Stag, '-') AS Stag, " 
                    + "IFNULL(Mac, '-') AS Mac, Checkout, Notes"
                    + " FROM device WHERE Stag="  
                    + info.getText() + ";";
                    break;
                case 3: 
                    test = macFormat(info.getText());
                    if(test.equals("y")){
                        return;
                    }
                    sql = "SELECT DType," 
                    + "Make, IFNULL(Model, '-') AS Model, D_ID,"
                    + " IFNULL(SN, '-')AS SN," 
                    + " IFNULL(Stag, '-') AS Stag, " 
                    + "IFNULL(Mac, '-') AS Mac, Checkout, Notes"
                    + " FROM device WHERE Mac="  
                    + info.getText() + ";";
                    break;
            }
            try{
                ResultSet searchDev = conn.executeQuery(sql);

                if(searchDev.next()) {
                    String checkout, emp;
                    
                    device = new Device(conn,searchDev.getString("DType"),
                        searchDev.getString("Make"),
                        searchDev.getString("Model"),
                        searchDev.getString("SN"),
                        searchDev.getString("Stag"),
                        searchDev.getString("Mac"),
                        searchDev.getInt("D_ID"),
                        searchDev.getString("Notes")
                    );  
                    
                    checkout = searchDev.getString("Checkout");
                    
                    if (checkout.equals("Yes")) {
                        sql = "SELECT employee.FName, employee.LName FROM " +
                            "checkout join employee ON checkout.E_ID=" + 
                            "employee.E_ID AND checkout.D_ID=" + device.getdID()
                            + ";";
                               
                        searchDev = conn.executeQuery(sql);
                        if(searchDev.next()) {
                            emp = searchDev.getString("LName") + ", " +
                                searchDev.getString("FName");
                        }
                        else
                            emp = "Employee Removed.";
                    }
                    else {
                        emp = "-";
                    }                    

                    print     = new JButton("Print Label");
                    viewNotes = new JButton("Device Notes");
                    removeDev = new JButton("Delete Device");
                    print.setFont(butFont);
                    viewNotes.setFont(butFont);
                    removeDev.setFont(butFont);
                    
                    TableButListener butList = new TableButListener();
                    print.addActionListener(butList);
                    viewNotes.addActionListener(butList);
                    removeDev.addActionListener(butList);
                    
                    Box stats = Box.createVerticalBox();
                    Box devBut = Box.createHorizontalBox();
                    
                    String styles = "'"
                            + "font-family: verdana;"
                            + "font-size: 20f;"
                            + "text-align: center;"
                            + "'";
                    
                    JTextPane resultsPane = new JTextPane();
                    StyledDocument doc = resultsPane.getStyledDocument();
                    SimpleAttributeSet center = new SimpleAttributeSet();
                    StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                    doc.setParagraphAttributes(0, doc.getLength(), center, false);
                    resultsPane.setContentType("text/html");
                    resultsPane.setText(
                            "<html><body style=" + styles + ">" +                                
                                "<b>Type</b>: " + device.getType() + "<br/>" +
                                "<b>Device ID</b>: " + device.getdID() + "<br/>" +
                                "<b>Make</b>: " + device.getMake() + "<br/>" +
                                "<b>Model</b>: " + device.getModel() + "<br/>" +
                                "<b>S/N</b>: " + device.getSn() + "<br/>" +
                                "<b>Service Tag</b>: " + device.getTag() + "<br/>" +
                                "<b>MAC Address</b>: " + device.getMac() + "<br/>" +
                                "<b>Checked Out By</b>: " + emp + "<pre><br/></pre>" +
                            "</body></html>");
                    resultsPane.setEditable(false);
                    resultsPane.setFont(resultFont);
                    resultsPane.setBackground(null);


                 
                    stats.add(resultsPane);
                    devBut.add(print);
                    devBut.add(Box.createHorizontalStrut(3));
                    devBut.add(viewNotes);
                    devBut.add(Box.createHorizontalStrut(3));
                    devBut.add(removeDev);
                    devBut.add(Box.createHorizontalStrut(30));
                    stats.add(devBut);
                    
                    UIManager.put("OptionPane.minimumSize",
                        new Dimension(500,500));
                    
                    JOptionPane.showMessageDialog(InventoryCheckout.this, 
                        stats,
                        "Results", 
                        JOptionPane.PLAIN_MESSAGE);
                }
                else {
                    JOptionPane.showMessageDialog(InventoryCheckout.this,
                        "Device Not Found","Device Not Found",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
            catch(SQLSyntaxErrorException e) {
                return;
            }
            catch(SQLException e) {
                JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Device ID Must Be a Number","Invalid Device ID",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
            catch(Exception searchEx) {
                errorLog("Line 982: Error searching for Device", searchEx);
                searchEx.printStackTrace();
            }
        }
        UIManager.put("OptionPane.minimumSize", null);
        device = null;
    }// End findDevice
    
    public void findDevice(int dID) {
        String sql = "SELECT * FROM device WHERE D_ID=" + dID + ";";
        try{
            ResultSet searchDev = conn.executeQuery(sql);
            

            if(searchDev.next()) {
                String checkout, emp;

                device = new Device(conn,searchDev.getString("DType"),
                    searchDev.getString("Make"),
                    searchDev.getString("Model"),
                    searchDev.getString("SN"),
                    searchDev.getString("Stag"),
                    searchDev.getString("Mac"),
                    searchDev.getInt("D_ID"),
                    searchDev.getString("Notes")
                );
                
                checkout = searchDev.getString("Checkout");

                if (checkout.equals("Yes")) {
                    
                    sql = "SELECT employee.FName, employee.LName FROM " +
                        "checkout join employee ON checkout.E_ID=" + 
                        "employee.E_ID AND checkout.D_ID=" + device.getdID()
                        + ";";

                    searchDev = conn.executeQuery(sql);
                    if(searchDev.next()) {
                        emp = searchDev.getString("LName") + ", " +
                            searchDev.getString("FName");
                    }
                    else
                        emp = "Employee Removed.";
                }
                else {
                    emp = "-";
                }

                print     = new JButton("Print Label");
                viewNotes = new JButton("Device Notes");
                removeDev = new JButton("Delete Device");
                edit      = new JButton( "Edit");

                print.setFont(butFont);
                viewNotes.setFont(butFont);
                removeDev.setFont(butFont);
                edit.setFont(butFont);

                TableButListener butList = new TableButListener();
                print.addActionListener(butList);
                viewNotes.addActionListener(butList);
                removeDev.addActionListener(butList);
                edit.addActionListener(butList);

                Box stats = Box.createVerticalBox();
                Box devBut = Box.createHorizontalBox();

                String styles = "'"
                        + "font-family: verdana;"
                        + "font-size: 20f;"
                        + "text-align: center;"
                        + "'";

                JTextPane resultsPane = new JTextPane();
                StyledDocument doc = resultsPane.getStyledDocument();
                SimpleAttributeSet center = new SimpleAttributeSet();
                StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
                doc.setParagraphAttributes(0, doc.getLength(), center, false);
                resultsPane.setContentType("text/html");
                resultsPane.setText(
                        "<html><body style=" + styles + ">" +                                
                            "<b>Type</b>: " + device.getType() + "<br/>" +
                            "<b>Device ID</b>: " + device.getdID() + "<br/>" +
                            "<b>Make</b>: " + device.getMake() + "<br/>" +
                            "<b>Model</b>: " + device.getModel() + "<br/>" +
                            "<b>S/N</b>: " + device.getSn() + "<br/>" +
                            "<b>Service Tag</b>: " + device.getTag() + "<br/>" +
                            "<b>MAC Address</b>: " + device.getMac() + "<br/>" +
                            "<b>Checked Out By</b>: " + emp + "<pre><br/></pre>" +
                        "</body></html>");
                resultsPane.setEditable(false);
                resultsPane.setFont(resultFont);
                resultsPane.setBackground(null);

                stats.add(resultsPane);
                devBut.add(print);
                devBut.add(Box.createHorizontalStrut(3));
                devBut.add(viewNotes);
                devBut.add(Box.createHorizontalStrut(3));
                devBut.add(removeDev);
                devBut.add(Box.createHorizontalStrut(3));
                devBut.add(edit);
                devBut.add(Box.createHorizontalStrut(30));
                stats.add(devBut);

                UIManager.put("OptionPane.minimumSize",
                    new Dimension(500,500));

                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                    stats,
                    "Results", 
                    JOptionPane.PLAIN_MESSAGE);
            }
            else {
                JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Device Not Found","Device Not Found",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
        catch(SQLSyntaxErrorException e) {
            System.out.println("Aye Caramba");
            return;
        }
        catch(Exception searchEx) {
            errorLog("Line 982: Error searching for Device", searchEx);
            searchEx.printStackTrace();
        }
    
        UIManager.put("OptionPane.minimumSize", null);
        device = null;
    }// End findDevice
    
/**
 * Formats MAC Address to the XX-XX-XX-XX-XX-XX format. Checks validity of 
 * the given address and capitalizes all letters.
 * @param mac - address to be formatted
 * @return returns the formated address if it is valid, or n if it is invalid
 */
    private String macFormat(String mac) { 

        mac = toUpper(mac);
        char[] queue = new char[17];
        char[] in = mac.toCharArray();
        int x = 0;
        String formatted = "";
        
        if(mac.equals("MAC Address")|| mac.equals("")) {
            return "n";
        }
        
        if(mac.length() == 12) {
            for(int i = 1; i <= 12; i++) {
                if((int)in[i-1] == 58 || (int)in[i-1] == 45){
                    int y = JOptionPane.showConfirmDialog
                        (InventoryCheckout.this, 
                        "Invalid MAC Address. Would you like to re-enter it?",
                        "Invalid MAC", JOptionPane.YES_NO_OPTION);
                    if(y == JOptionPane.YES_OPTION) {
                        return "y";
                    } 
                    else
                        return "n";
                }
                    
                if(!validCheck(in[i-1])) {
                    int y = JOptionPane.showConfirmDialog
                        (InventoryCheckout.this, 
                        "Invalid MAC Address. Would you like to re-enter it?",
                        "Invalid MAC", JOptionPane.YES_NO_OPTION);
                    if(y == JOptionPane.YES_OPTION) {
                        return "y";
                    } 
                    else
                        return "n";
                }
                    
                queue[x] = in[i-1];
               
                if(i % 2 == 0 && i != 12) {
                    x++;
                    queue[x] = '-';
               }
            x++;
           }
           for(int i = 0; i < queue.length; i++) {
               formatted += queue[i];
           }
           
           return formatted;
        }
        else if(mac.length() == 17) {
            for(int i = 0; i < 17; i++) {
                if((int)in[i] == 58) {
                    in[i] = '-';
                }
                if(!validCheck(in[i])){
                    int y = JOptionPane.showConfirmDialog
                        (InventoryCheckout.this, 
                        "Invalid MAC Address. Would you like to re-enter it?",
                        "Invalid MAC", JOptionPane.YES_NO_OPTION);
                    if(y == JOptionPane.YES_OPTION) {
                        return "y";
                    } 
                    else
                        return "n";
                }
                formatted += in[i];
            }
            return formatted;
        }
       
       int y = JOptionPane.showConfirmDialog(InventoryCheckout.this, 
                        "Invalid MAC Address. Would you like to re-enter it?",
                        "Invalid MAC", JOptionPane.YES_NO_OPTION);
                    if(y == JOptionPane.YES_OPTION) {
                        return "y";
                    } 
                    else
                        return "n";
    }

/**
 * Converts a string to all uppercase letters
 * @param word - string to be converted
 * @return Uppercase string
 */
    private String toUpper(String word) {
        char[] chars = word.toCharArray();
        String caps = "";
        int temp;
        
        for(int i = 0; i < word.length(); i++) {

            temp = (int)chars[i];
            
            if(temp > 96 && temp < 123) {
                temp -= 32;
            }

            caps += (char)temp;
        }
        return caps;
    }
    
    private boolean validCheck(char c) {
        int check = (int)c;
        
        if((check > 96 && check < 103) ||
            (check > 64 && check < 71) || check == 45)
            return true;
        else if (check > 47 && check < 58)
            return true;
        
        return false;
    }
    
    private void checkout() {
        UIManager.put("OptionPane.minimumSize", new Dimension(300,100));
        Box checkBox = Box.createVerticalBox();
        JLabel empMess      = new JLabel("Employee ID: ");
        JTextField empCheck = new JTextField(10);
        JLabel devMess      = new JLabel("Device ID: ");
        JTextField devCheck = new JTextField(10);
        
        if(devTable.getSelectedRow()!= -1) {
            devCheck.setText(
                Integer.toString((Integer)
                devTable.getValueAt(devTable.getSelectedRow(), 3)));
        }
        
        if(empTable.getSelectedRow()!= -1) {
            empCheck.setText(
                Integer.toString((Integer)
                empTable.getValueAt(empTable.getSelectedRow(), 4)));
        }
        
        empMess.setFont(formFont);
        empCheck.setFont(formFont);
        devMess.setFont(formFont);
        devCheck.setFont(formFont);
        
        Box empBox = Box.createHorizontalBox();
        empBox.add(empMess);
        empBox.add(empCheck);
        Box devBox = Box.createHorizontalBox();
        devBox.add(devMess);
        devBox.add(Box.createHorizontalStrut(16));
        devBox.add(devCheck);
        checkBox.add(empBox);
        checkBox.add(Box.createVerticalStrut(2));
        checkBox.add(devBox);
        
        int result = JOptionPane.showConfirmDialog
            (InventoryCheckout.this, checkBox,
            "Checkout", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION) {
            String empID = empCheck.getText();
            String devID = devCheck.getText();

            String checkout = "SELECT E_ID from checkout where "
                + "D_ID=" + devID + ";";
            try {
                ResultSet res = conn.executeQuery(checkout);


                if(res.next()) {
                    JOptionPane.showMessageDialog
                        (InventoryCheckout.this, 
                        "Device Already Checked Out.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        checkout = "INSERT INTO checkout (E_ID, D_ID) "
                            + "VALUES (" + empID + ", " + devID + ");";

                        conn.executeUpdate(checkout);
                        
                        checkout = "SELECT * FROM device WHERE D_ID=" +
                            devID + ";";
                        
                        res = conn.executeQuery(checkout);
                        res.next();
                        
                        device = new Device(conn, res.getString("DType"), 
                            res.getString("Make"), res.getString("Model"),
                            res.getString("SN"), res.getString("Stag"),
                            res.getString("MAC"), res.getInt("D_ID"),
                            res.getString("Notes"));
                        checkout = "SELECT FName, LName FROM employee WHERE" +
                            " E_ID=" + empID + ";";
                        res = conn.executeQuery(checkout);
                        res.next();
                        device.addNote("Device checked out to " + 
                            res.getString("FName") + " " +
                            res.getString("LName") + ".");

                        checkout = "UPDATE device SET Checkout='Yes' "
                            + "WHERE D_ID=" + devID + ";";
                        conn.executeUpdate(checkout);
                        JOptionPane.showMessageDialog
                            (InventoryCheckout.this,
                            "Device Has Been Checked Out!", "Success!", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } catch(SQLIntegrityConstraintViolationException
                        empSql) {
                        JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Invalid Employee ID.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(SQLSyntaxErrorException emptyEmpID) {
                         JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Employee ID cannot be blank.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    catch(Exception es){
                        errorLog("Line 539: Error checking out", es);
                        JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Unknown Error Has Occured.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch(SQLException sqlEx) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                    "Invalid Device ID",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex) {
                errorLog("Line 531: Error Querying Checkout Table", ex);
            }
        }
        UIManager.put("OptionPane.minimumSize", null);
        //printDevTable();
    }// End checkout

/**
 * Allows the user to checkout a device from the findEmployee window
 * @param eID 
 */
    private void checkout(int eID) {
        UIManager.put("OptionPane.minimumSize", new Dimension(300,100));
        Box checkBox = Box.createVerticalBox();
        JLabel empMess      = new JLabel("Checkout to: " + employee.getFn()
            + " " + employee.getLn());
        JLabel devMess      = new JLabel("Device ID: ");
        JTextField devCheck = new JTextField(10);
        
        empMess.setFont(formFont);

        devMess.setFont(formFont);
        devCheck.setFont(formFont);
        
        Box empBox = Box.createHorizontalBox();
        empBox.add(empMess);
        Box devBox = Box.createHorizontalBox();
        devBox.add(devMess);
        devBox.add(Box.createHorizontalStrut(16));
        devBox.add(devCheck);
        checkBox.add(empBox);
        checkBox.add(Box.createVerticalStrut(2));
        checkBox.add(devBox);
        
        int result = JOptionPane.showConfirmDialog
            (InventoryCheckout.this, checkBox,
            "Checkout", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

        if(result == JOptionPane.OK_OPTION) {
            String empID = Integer.toString(eID);
            String devID = devCheck.getText();

            String checkout = "SELECT E_ID from checkout where "
                + "D_ID=" + devID + ";";
            try {
                ResultSet res = conn.executeQuery(checkout);


                if(res.next()) {
                    JOptionPane.showMessageDialog
                        (InventoryCheckout.this, 
                        "Device Already Checked Out.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    try {
                        checkout = "INSERT INTO checkout (E_ID, D_ID) "
                            + "VALUES (" + empID + ", " + devID + ");";

                        conn.executeUpdate(checkout);

                        checkout = "UPDATE device SET Checkout='Yes' "
                            + "WHERE D_ID=" + devID + ";";
                        conn.executeUpdate(checkout);
                        JOptionPane.showMessageDialog
                            (InventoryCheckout.this,
                            "Device Has Been Checked Out!", "Success!", 
                            JOptionPane.INFORMATION_MESSAGE);
                        Device dev = new Device(conn);
                        dev.setdID(Integer.parseInt(devID));
                        dev.addNote("Checked out by " + employee.getFn() +
                            " " + employee.getLn() + ".");
                        
                        
                    } catch(SQLIntegrityConstraintViolationException
                        empSql) {
                        JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Invalid Employee ID.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    } catch(SQLSyntaxErrorException emptyEmpID) {
                         JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Employee ID cannot be blank.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    catch(Exception es){
                        errorLog("Line 539: Error checking out", es);
                        JOptionPane.showMessageDialog
                           (InventoryCheckout.this, 
                           "Unknown Error Has Occured.",
                           "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch(SQLException sqlEx) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                    "Invalid Device ID",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch(Exception ex) {
                errorLog("Line 531: Error Querying Checkout Table", ex);
            }
        }
    }// End checkout
    
    private void returnDevice() {
        JLabel label = new JLabel("Device ID: ");
        JTextField ret = new JTextField();
        label.setFont(formFont);
        ret.setFont(formFont);
        Box devBox = Box.createHorizontalBox();
        devBox.add(label);
        devBox.add(ret);
        String devID;
        
        if(devTable.getSelectedRow() != -1) {
            ret.setText(Integer.toString((Integer)
                devTable.getValueAt(devTable.getSelectedRow(), 3)));
        }
        
        
        int result = JOptionPane.showConfirmDialog
            (InventoryCheckout.this,
            devBox,"Return", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);
        if(result == JOptionPane.OK_OPTION) {
            devID = ret.getText();
            String sql = "Select * FROM checkout WHERE D_ID=" + devID +
                 ";";

            try{
                ResultSet res =conn.executeQuery(sql);

                if(!res.next()) {
                    JOptionPane.showMessageDialog
                        (InventoryCheckout.this, 
                        "That Device has not been checked out.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    String fname, lname;
                    
                    sql = "SELECT employee.FName, employee.LName FROM " +
                            "checkout join employee ON checkout.E_ID=" + 
                            "employee.E_ID AND checkout.D_ID=" + devID
                            + ";";
                    
                    res = conn.executeQuery(sql);
                    res.next();
                    fname = res.getString("FName");
                    lname = res.getString("LName");
                    

                    sql = "DELETE FROM checkout WHERE D_ID=" +
                        devID + ";";
                    conn.executeUpdate(sql);
                    sql = "UPDATE device SET Checkout='No' "
                                     + "WHERE D_ID=" + devID + ";";
                    conn.executeUpdate(sql);

                    JOptionPane.showMessageDialog
                       (InventoryCheckout.this,
                       "Device Has Been Returned!", "Success!", 
                       JOptionPane.INFORMATION_MESSAGE);
                    
                    sql = "SELECT Notes FROM device where D_ID=" + devID +";";
                    
                    res = conn.executeQuery(sql);
                    res.next();
                    Device dev = new Device(conn);
                    dev.setdID(Integer.parseInt(devID));
                    dev.setNotes(res.getString("Notes"));
 
                    System.out.println(fname + " " + lname);
                    
                    dev.addNote("Returned by " + fname + " " +
                        lname + ".");
                    
                    
                    printDevTable();
                }
            }catch(SQLSyntaxErrorException invalidID) {
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                            "Invalid Device ID.",
                            "Error", JOptionPane.ERROR_MESSAGE);
            }
            catch (Exception retEx){
                errorLog("Line 586: Error Returning device", retEx);;
            }
        }
    }
    
    private void returnDevice(int did) {
        UIManager.put("OptionPane.minimumSize", null);
        String sql;
        try{
            Device dev = new Device(conn);
            
            sql = "SELECT Notes FROM device WHERE D_ID=" + did +";";
            
            ResultSet res = conn.executeQuery(sql);
            res.next();
            
            dev.setdID(did);
            dev.setNotes(res.getString("Notes"));
            
            
            dev.addNote("Returned by " + employee.getFn() + " " + 
                employee.getLn() + ".");
            
            sql = "DELETE FROM checkout WHERE D_ID=" +
                did + ";";
             conn.executeUpdate(sql);
             sql = "UPDATE device SET Checkout='No' "
                              + "WHERE D_ID=" + did + ";";
             conn.executeUpdate(sql);

             JOptionPane.showMessageDialog
                (InventoryCheckout.this,
                "Device Has Been Returned!", "Success!", 
                JOptionPane.INFORMATION_MESSAGE);
             printDevTable();
            
        }catch(SQLSyntaxErrorException invalidID) {
            JOptionPane.showMessageDialog(InventoryCheckout.this, 
                        "Invalid Device ID.",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        catch (Exception retEx){
            errorLog("Line 586: Error Returning device", retEx);
        }
    }
    
    private void findEmp() {
        name              = new JRadioButton("Name", true);
        eID               = new JRadioButton("Employee ID", false);
        ButtonGroup group = new ButtonGroup();
        JLabel first      = new JLabel("First Name: ");
        JLabel Last       = new JLabel("Last Name: ");
        JLabel id         = new JLabel("Employee ID: ");
        findID            = new JTextField();
        findFName         = new JTextField();
        findLName         = new JTextField();
        
        name.setFont(formFont);
        eID.setFont(formFont);
        first.setFont(formFont);
        Last.setFont(formFont);
        id.setFont(formFont);
        findID.setFont(formFont);
        findFName.setFont(formFont);
        findLName.setFont(formFont);
        
        RadioListener rList = new RadioListener();
        findID.setEditable(false);
        name.addItemListener(rList);
        eID.addItemListener(rList); 
        
        group.add(name);
        group.add(eID);

        Box select = Box.createHorizontalBox();
        Box name1 = Box.createHorizontalBox();
        Box name2 = Box.createHorizontalBox();
        Box i1 = Box.createHorizontalBox();
        Box vertBox = Box.createVerticalBox();
        Box buttonBox = Box.createHorizontalBox();
        select.add(name);
        select.add(eID);
        name1.add(first);
        name1.add(findFName);
        name2.add(Last);
        name2.add(findLName);
        i1.add(id);
        i1.add(findID);
        vertBox.add(select);
        vertBox.add(Box.createVerticalStrut(2));
        vertBox.add(name1);
        vertBox.add(Box.createVerticalStrut(2));
        vertBox.add(name2);
        vertBox.add(Box.createVerticalStrut(2));
        vertBox.add(i1);
        vertBox.add(buttonBox);
        
        String sql = " ";
        ResultSet qres;
        
        if (empTable.getSelectedRow() == -1) {
            int res = JOptionPane.showConfirmDialog(InventoryCheckout.this,
                vertBox, "Find Employee", JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

            if(res == JOptionPane.OK_OPTION) {

                if(eID.isSelected()){
                    sql = "Select * FROM employee WHERE E_ID=" +
                        findID.getText() + ";";  
                }
                else {
                    String fn = findFName.getText();
                    String ln = findLName.getText();

                    if(fn.equals("") || ln.equals("")){
                        JOptionPane.showMessageDialog(InventoryCheckout.this,
                            "You Must Enter Both First And Last Name.",
                            "Invalid Name", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    sql = "SELECT * FROM employee WHERE FName='" + fn + "'"
                        + " AND LName='" + ln + "';";
                }
            }
            else {
                return;
            }
        }
        else {
            sql = "SELECT * FROM employee WHERE E_ID=" +
                empTable.getValueAt(empTable.getSelectedRow(), 4);
        }
            
            try {
                qres = conn.executeQuery(sql);

                if(qres.next()){
                    EmpListener tlist = new EmpListener();
                    removeEmp  = new JButton("Delete Employee");
                    empCheck   = new JButton("Checkout Device");
                    empRet     = new JButton("Return");
                    viewDevice = new JButton("View Device");
                    viewDevice.setFont(butFont);
                    viewDevice.addActionListener(tlist);
                    empCheck.setFont(butFont);
                    empCheck.addActionListener(tlist);
                    empRet.setFont(butFont);
                    empRet.addActionListener(tlist);
                    removeEmp.setFont(butFont);
                    removeEmp.addActionListener(tlist);
                    
                    employee = new Users(conn,
                        qres.getString("FName"),
                        qres.getString("LName"),
                        qres.getString("Dept") ,
                        qres.getString("Email"),
                        qres.getInt("E_ID")
                    );
                }
                else {
                    JOptionPane.showMessageDialog(InventoryCheckout.this,
                        "Employee Not Found", "Employee Not Found",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                results = new JTextArea(5, 20);
                results.append("Name: " + employee.getLn() + ", " +
                    employee.getFn() + "\n");
                results.append("Dept: " + employee.getDept() + "\n");
                results.append("Email: " + employee.getEmail() + "\n");
                results.append("ID  #: " + employee.getId() + "\n");
                
                results.append("\nDevices Checked Out:");
                
                results.setFont(resultFont);
                results.setBackground(null);
                results.setEditable(false);
                results.setLineWrap(true);
                results.setWrapStyleWord(true);
                tab = employee.showDevs();
                

                tab.setBorder(null);
                tab.setFont(tableFont);
                checkList = new JScrollPane(tab);
                
                checkList.setBorder(null);
                
                Box col = Box.createVerticalBox();
                col.add(results);
                col.add(checkList);
                Box buttons = Box.createHorizontalBox();
                buttons.add(removeEmp);
                buttons.add(empCheck);
                buttons.add(empRet);
                buttons.add(viewDevice);
                col.add(buttons);
                UIManager.put("OptionPane.minimumSize", new Dimension(600,400));
                
                JOptionPane.showMessageDialog(InventoryCheckout.this, 
                        col, "Results",JOptionPane.PLAIN_MESSAGE);
            }
            catch(SQLSyntaxErrorException s) {
                JOptionPane.showMessageDialog(InventoryCheckout.this,
                    "Invalid ID #!", "Invalid",
                    JOptionPane.ERROR_MESSAGE);
            }
            catch(Exception e) {
                e.printStackTrace();
            } 
        
        UIManager.put("OptionPane.minimumSize", null);
        employee = null;
    }// End findEmp
    
    public void install() {
        
        
        JLabel instruct  = new JLabel("Enter the IP Address of the "
            + "MySQL Server");
        JLabel ipLabel   = new JLabel("IP Address: ");
        JLabel ipSep     = new JLabel(".");
        JLabel ipSep2    = new JLabel(".");
        JLabel ipSep3    = new JLabel(".");
        JTextField ip1    = new JTextField("", 3);
        JTextField ip2   = new JTextField("", 3);
        JTextField ip3   = new JTextField("", 3);
        JTextField ip4   = new JTextField("", 3);
        
        instruct.setFont(formFont);
        ipLabel.setFont(formFont);
        ip1.setFont(formFont);
        ip2.setFont(formFont);
        ip3.setFont(formFont);
        ip4.setFont(formFont);
        
        Box ipRow = Box.createHorizontalBox();
        ipRow.add(ip1);
        ipRow.add(ipSep);
        ipRow.add(ip2);
        ipRow.add(ipSep2);
        ipRow.add(ip3);
        ipRow.add(ipSep3);
        ipRow.add(ip4);
        
        
        JPanel installPanel = new JPanel();
        installPanel.setLayout(new GridBagLayout());
        
        addComp(installPanel, instruct, 1,1,2,1, GridBagConstraints.CENTER,
            GridBagConstraints.HORIZONTAL);
        addComp(installPanel, ipLabel, 1,2,1,1, GridBagConstraints.CENTER,
            GridBagConstraints.NONE);
        addComp(installPanel, ipRow, 2,2,1,1, GridBagConstraints.WEST, 
            GridBagConstraints.NONE);
        
        int opt = JOptionPane.showConfirmDialog(InventoryCheckout.this,
            installPanel, "First Time Setup", JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        
        if(opt == JOptionPane.CANCEL_OPTION ||
        opt == JOptionPane.CLOSED_OPTION) {
            System.exit(0);
        }
        else if(opt == JOptionPane.OK_OPTION) {
            new File(installPath).mkdir();
            new File(installPath).setWritable(true);
            
            String ip = ip1.getText() + "." + ip2.getText() + "." + 
                ip3.getText() + "." + ip4.getText();
            
            try {
                String file = installPath + "config.cfg";
                    Properties properties = new Properties();
                    properties.setProperty("IP-Address", ip);
                    
                    FileOutputStream fos = new FileOutputStream(file);
                    properties.store(fos, "Edit Connection Settings");
                    fos.close();
            }
            catch(Exception e) {
                e.printStackTrace();
            } 
        }        
    }
    
    public void splashScreen() {
        BufferedImage splash = null;
        try
        {
            splash = ImageIO.read(getClass().getResource("resources/splashscreenx.png"));
        } catch (Exception iconEx) {
            errorLog("Error loading splashscreen", iconEx);
            iconEx.printStackTrace();
        }
        
        JWindow window = new JWindow();
        window.getContentPane().add(new JLabel("", new ImageIcon(splash), SwingConstants.CENTER));
        window.setBounds(500, 500, 500, 103);
        window.setLocationRelativeTo(null);

        window.setVisible(true);
        
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            e.printStackTrace();
        }
        window.setVisible(false);
        window.dispose();
    }

    public void editDev() {
        JComboBox editType   = new JComboBox(devType);
        JTextField editMake  = new JTextField(device.getMake(), 5);
        JTextField editModel = new JTextField(device.getModel(), 6);
        JTextField editSN    = new JTextField(device.getSn(), 5);
        JTextField editStag  = new JTextField(device.getTag(), 9);
        JTextField editMac   = new JTextField(device.getMac(), 10);



        editType.setFont(resultFont);
        editMake.setFont(resultFont);
        editModel.setFont(resultFont);
        editSN.setFont(resultFont);
        editStag.setFont(resultFont);
        editMac.setFont(resultFont);

        String styles = "'"
                + "font-family: verdana;"
                + "font-size: 20f;"
                + "text-align: right;"
                + "'";

        JLabel label1 = new JLabel("<html><body style=" + styles + "><b>Device Type:</b></body></html>");
        JLabel label2 = new JLabel("<html><body style=" + styles + "><b>Make:</b></body></html>");
        JLabel label3 = new JLabel("<html><body style=" + styles + "><b>Model:</b></body></html>");
        JLabel label4 = new JLabel("<html><body style=" + styles + "><b>S/N:</b></body></html>");
        JLabel label5 = new JLabel("<html><body style=" + styles + "><b>Service Tag:</b></body></html>");
        JLabel label6 = new JLabel("<html><body style=" + styles + "><b>MAC Address:</b></body></html>");

        Box infoBox =  Box.createVerticalBox();
        Box row1    =  Box.createHorizontalBox();
        Box row2    =  Box.createHorizontalBox();
        Box row3    =  Box.createHorizontalBox();
        Box row4    =  Box.createHorizontalBox();
        Box row5    =  Box.createHorizontalBox();
        Box row6    =  Box.createHorizontalBox();

        row1.add(label1);
        row1.add(editType);

        row2.add(label2);
        row2.add(editMake);

        row3.add(label3);
        row3.add(editModel);

        row4.add(label4);
        row4.add(editSN);

        row5.add(label5);
        row5.add(editStag);

        row6.add(label6);
        row6.add(editMac);

        infoBox.add(row1);
        infoBox.add(Box.createVerticalStrut(15));
        infoBox.add(row2);
        infoBox.add(Box.createVerticalStrut(15));
        infoBox.add(row3);
        infoBox.add(Box.createVerticalStrut(15));
        infoBox.add(row4);
        infoBox.add(Box.createVerticalStrut(15));
        infoBox.add(row5);
        infoBox.add(Box.createVerticalStrut(15));
        infoBox.add(row6);

        UIManager.put("OptionPane.minimumSize",
                new Dimension(500,500));

        int res = JOptionPane.showConfirmDialog(InventoryCheckout.this,
                infoBox,
                "Results",
                JOptionPane.OK_CANCEL_OPTION);

        if (res == JOptionPane.OK_OPTION) {

            String sql;

            if(!devType[editType.getSelectedIndex()].equals(device.getType())) {
                device.setType(devType[editType.getSelectedIndex()]);
                sql = "UPDATE device SET DType='" + device.getType() + "' WHERE D_ID=" + device.getdID() + ";";
                update(sql);
            }

            if(!editMake.getText().equals(device.getMake())) {
                device.setMake(editMake.getText());
                sql = "UPDATE device SET Make='" + device.getMake() + "' WHERE D_ID=" + device.getdID() + ";";
                update(sql);
            }

            if(!editModel.getText().equals(device.getModel())) {
                device.setModel(editModel.getText());
                sql = "UPDATE device SET Model='" + device.getModel() + "' WHERE D_ID=" + device.getdID() + ";";
                update(sql);
            }

            if(!editSN.getText().equals(device.getSn())) {
                if(editSN.getText().trim().equals("")) {
                    device.setSn("NULL");
                    sql = "UPDATE device SET SN=" + toUpper(device.getSn()) + " WHERE D_ID=" + device.getdID() + ";";
                }
                else {
                    device.setSn(editSN.getText());
                    sql = "UPDATE device SET SN='" + toUpper(device.getSn()) + "' WHERE D_ID=" + device.getdID() + ";";
                }

                update(sql);
            }

            if(!editStag.getText().equals(device.getTag())) {
                if(editStag.getText().trim().equals("")) {
                    device.setTag("NULL");
                    sql = "UPDATE device SET Stag=" + device.getTag() + " WHERE D_ID=" + device.getdID() + ";";
                }
                else {
                    device.setTag(editStag.getText());
                    sql = "UPDATE device SET Stag='" + toUpper(device.getTag()) + "' WHERE D_ID=" + device.getdID() + ";";
                }
                update(sql);
            }

            if(!macFormat(editMac.getText()).equals(device.getMac())) {

                if(editMac.getText().trim().equals("")) {
                    device.setMac("NULL");
                    sql = "UPDATE device SET Mac=" + device.getMac() + " WHERE D_ID=" + device.getdID() + ";";
                    update(sql);
                }
                else {
                    String tmp = macFormat(editMac.getText());
                    if(tmp.equals("y")) {

                    }
                    else if(tmp.equals("n")) {
                    }
                    else {
                        device.setMac(tmp);
                        System.out.println(tmp);
                        sql = "UPDATE device SET Mac='" + device.getMac() + "' WHERE D_ID=" + device.getdID() + ";";
                        update(sql);
                    }


                }
            }

        }
        closePane();
        findDevice(device.getdID());
        printDevTable();

    }
    private void update(String sql) {
        try {
            conn.executeUpdate(sql);
        }catch (Exception e) {
            e.printStackTrace();
            errorLog("Error Editing", e);
        }
    }

    private void closePane() {
        Window[] windows = Window.getWindows();
        for (Window window : windows) {
            if (window instanceof JDialog) {
                JDialog dialog = (JDialog) window;
                if (dialog.getContentPane().getComponentCount() == 1
                && dialog.getContentPane().getComponent(0) instanceof JOptionPane) {
                    dialog.dispose();
                }
            }
        }
    }
}// End class