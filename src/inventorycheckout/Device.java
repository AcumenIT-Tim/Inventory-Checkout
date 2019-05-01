/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inventorycheckout;

import java.sql.*;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import static inventorycheckout.InventoryCheckout.conn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.ServiceUI;
import javax.print.SimpleDoc;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashDocAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;


/**
 *
 * @author admin_kromer
 */
public class Device{
    Statement statement;
   
    String type, make, model, sn, tag, mac, sql, notes;
    int dID;

    public void setdID(int dID) {
        this.dID = dID;
    }

    public int getdID() {
        return dID;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getNotes() {
        return notes;
    }
    
    int error = 0;
    public Device(Statement stmt) {
        setStatement(stmt);
    }
    
    public Device (Statement stmt, String nType, String nMake, String nModel,
            String nSN, String nTag, String nMac, int nDID, String nNotes) {
        setStatement(stmt);
        setType(nType);
        setMake(nMake);
        setModel(nModel);
        setSn(nSN);
        setTag(nTag);
        setMac(nMac);
        setdID(nDID);
        setNotes(nNotes);
     }
    
        public Device (Statement stmt, String nType, String nMake, String nModel,
            String nSN, String nTag, String nMac) {
        setStatement(stmt);
        setType(nType);
        setMake(nMake);
        setModel(nModel);
        setSn(nSN);
        setTag(nTag);
        setMac(nMac);
     }
   
    public Statement getStatement() {
        return statement;
    }

    public String getType() {
        return type;
    }

    public String getMake() {
        return make;
    }

    public String getModel() {
        return model;
    }

    public String getSn() {;
        return sn;
    }

    public String getTag() {
        return tag;
    }

    public String getMac() {
        return mac;
    }


    public void setStatement(Statement statement) {
        this.statement = statement;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int addDevice() {
        String newDevNote = 
                new Timestamp(System.currentTimeMillis()).toString() +
                "\nDevice added to inventory.";
        
        sql = "INSERT INTO device (DType, Make, Model, SN, Stag, MAC, Notes)"
                + " VALUES (" + type + ", " + make + ", " + model 
                + ", " + sn 
                + ", " + tag + ", " + mac + ", '" + newDevNote + "');";
        try{
            statement.executeUpdate(sql);
        }catch(SQLIntegrityConstraintViolationException sqlEx){
            return 2;
        }
        catch (Exception exc) {
            exc.printStackTrace();
            return 1;
        }
        
        return 0;
    }// End addDevice
    
    public ResultSet printDeviceList() {
        try {
            String sql = "SELECT DType,"
                    + "Make, IFNULL(Model, '-') AS Model, D_ID, IFNULL(SN, '-')AS SN,"
                    + " IFNULL(Stag, '-') AS Stag, "
                    + "IFNULL(Mac, '-') AS Mac, Checkout FROM device;";
            ResultSet myRs = statement.executeQuery(sql);
            return myRs;       
            }
        
        catch(Exception exc) {
            exc.printStackTrace();
            System.out.println("Error in Device.printDeviceList");
        }
        return null;
    }// End printDevice     
    
        public void printBarcode(int D_ID ) {
            String filePath = "C:\\users\\" +
                    System.getProperty("user.name") +
                    "\\AppData\\local\\AcumenIT Checkout\\barcode.png";
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(Integer.toString(D_ID),BarcodeFormat.QR_CODE, 100, 100);
           
            
            Path path = FileSystems.getDefault().getPath(filePath);
            MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);

//          This section adds the device ID below the barcode  
            FileInputStream rewrite = new FileInputStream(filePath);
            
            BufferedImage image = ImageIO.read(rewrite);
            Graphics g = image.getGraphics();
            g.setFont(g.getFont().deriveFont(12f));
            g.setColor(Color.red);
            g.drawString(Integer.toString(D_ID), 35, 93);
            g.dispose();
            rewrite.close();
            ImageIO.write(image, "png", new File(filePath));
            
            
            
//          Sends barcode.png to the print service
            PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

            DocFlavor flavor = DocFlavor.INPUT_STREAM.PNG;
            PrintService printService[] = PrintServiceLookup.lookupPrintServices(flavor, pras);
            PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
            PrintService service = ServiceUI.printDialog(null, 0, 0, printService, defaultService, flavor, pras);
            
            
            if(service != null) {
                DocPrintJob job = service.createPrintJob();
                FileInputStream in = new FileInputStream(filePath);

                DocAttributeSet das = new HashDocAttributeSet();
                das.add(OrientationRequested.PORTRAIT);
                das.add(new MediaPrintableArea(0f, 0f, 1, 1, MediaPrintableArea.INCH));
                Doc doc = new SimpleDoc(in, flavor, das);
                
                job.print(doc, pras);
                in.close();
                
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }// End createImage
        
    
    public void addNote(JFrame frame) {
        Font notesFont = new Font("Times New Roman", Font.PLAIN, 16);
        String text = "'" + getNotes();
        
        JTextArea prevNotes = new JTextArea(10, 20);
        prevNotes.setFont(notesFont);
        prevNotes.append(getNotes());
        prevNotes.setEditable(false);
        prevNotes.setLineWrap(true);
        prevNotes.setWrapStyleWord(true);
        prevNotes.setBackground(null);
        prevNotes.setBorder(null);
        
        JTextArea newNotes = new JTextArea(10,20);
        newNotes.setFont(notesFont);
        newNotes.append
            (new Timestamp(System.currentTimeMillis()).toString() + "\n");
        newNotes.setLineWrap(true);
        newNotes.setWrapStyleWord(true);
        
        Box notes = Box.createVerticalBox();
        JScrollPane prevNotesScroll = new JScrollPane();
        prevNotesScroll.setBorder(null);
        prevNotesScroll.getViewport().add(prevNotes);
        prevNotesScroll.setHorizontalScrollBarPolicy
            (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notes.add(prevNotesScroll);
        notes.add(Box.createVerticalStrut(2));
        JScrollPane newNotesScroll = new JScrollPane();
        newNotesScroll.getViewport().add(newNotes);
        newNotesScroll.setHorizontalScrollBarPolicy
            (ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        notes.add(newNotesScroll);
        
        UIManager.put("OptionPane.minimumSize", new Dimension(500,500));
        
        int res = JOptionPane.showConfirmDialog(frame, notes,
            "Add new note", JOptionPane.OK_CANCEL_OPTION);
        UIManager.put("OptionPane.minimumSize", null);
        
        if(res == JOptionPane.OK_OPTION) {
            String temp = newNotes.getText().trim();
            text += ("\n\n" + temp);
            setNotes(text);
            text += "'";
            
            try {
                String sql = "UPDATE device SET Notes=" + text + " WHERE"
                        + " D_ID=" + getdID() + ";";
                conn.executeUpdate(sql);
            }
            catch (Exception e) {
                
                InventoryCheckout.errorLog("Error adding new notes", e);
            }
        }
    }
    
    public void addNote(String note) {
        String text = "'" + getNotes();
            text += ("\n\n" +
                new Timestamp(System.currentTimeMillis()).toString() +
                "\n"+ note);
            setNotes(text);
            text += "'";
            
            try {
                String sql = "UPDATE device SET Notes=" + text + " WHERE"
                    + " D_ID=" + getdID() + ";";
                conn.executeUpdate(sql);
            }
            catch (Exception e) {
                InventoryCheckout.errorLog("Error adding new notes", e);
                e.printStackTrace();
            }
    }
    
    public void removeDev() {
        String sql = "DELETE FROM checkout WHERE D_ID=" + getdID() + ";";
        
        try {
            conn.executeUpdate(sql);
            sql = "DELETE FROM device WHERE D_ID=" + getdID() + ";";
            conn.executeUpdate(sql);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

}// End Class
