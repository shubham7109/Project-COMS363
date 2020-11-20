package coms363;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.sql.*;

/**
 * This is the HW4 JDBC Submission
 * @author Shubham Sharma
 */
public class hw4 {

    /**
     * Main method to handle the option selection
     */
    public static void main(String[] args) {
        String dbServer = "jdbc:mysql://127.0.0.1:3306/sakila_mod?allowPublicKeyRetrieval=true&useSSL=false";
        // For compliance with existing applications not using SSL the verifyServerCertificate property is set to ‘false’,
        String userName;
        String password;

        String[] result = loginDialog();
        userName = result[0];
        password = result[1];

        Connection conn;
        Statement stmt;
        if (result[0]==null || result[1]==null) {
            System.out.println("Terminating: No username nor password is given");
            return;
        }
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(dbServer, userName, password);
            stmt = conn.createStatement();

            String option;
            String instruction = "Option a: Insert a new actor." + "\n"
                    + "Option b: Delete a customer.\n"
                    + "Option c: Find total sales. \n"
                    + "Option e: Quit";

            label:
            while (true) {
                option = JOptionPane.showInputDialog(instruction);
                switch (option) {
                    case "a":
                        insertActor(conn);
                        break;
                    case "b":
                        deleteCustomer(conn);
                        break;
                    case "c":
                        totalSales(conn);
                        break;
                    case "e":
                        System.out.println("Exiting application");
                        break label;
                }
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Program terminates due to errors");
            System.out.println(e.getMessage());
            //e.printStackTrace(); // for debugging
        }
    }

    /**
     * Part A:
     * Inserts an actor to the actor table
     * @param conn Connection object to the DB
     */
    private static void insertActor(Connection conn) {

        if (conn==null) throw new NullPointerException();
        try {

            String firstName = "";
            String lastName = "";
            while(firstName.trim().equals("")){
                firstName = JOptionPane.showInputDialog("Enter first name:");
            }
            while(lastName.trim().equals("")){
                lastName = JOptionPane.showInputDialog("Enter last name:");
            }

            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            PreparedStatement inststmt = conn.prepareStatement(
                    " insert into actor (first_name,last_name) values(?,?) ");
            inststmt.setString(1, firstName);
            inststmt.setString(2, lastName);

            int rowcount = inststmt.executeUpdate();

            System.out.println("Number of rows updated:" + rowcount);
            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();
            // if other parts of the program needs commit per SQL statement
            // conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Option B:
     * Deletes the customer with the customer_id.
     * Updates the customer, rental and the payment table
     * @param conn Connection object to the DB
     */
    private static void deleteCustomer(Connection conn) {
        if (conn==null) throw new NullPointerException();
        try {
            String customerID = "";
            while(customerID.trim().equals("")){
                customerID = JOptionPane.showInputDialog("customer_id to be deleted:");
            }

            String confirmResponse = "";
            while(!confirmResponse.equals("y") && !confirmResponse.equals("n")){
                confirmResponse = JOptionPane.showInputDialog("CONFIRM: (y/n)");
            }

            if(confirmResponse.equals("n"))
                return;

            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            PreparedStatement inststmt = conn.prepareStatement(" delete from payment where customer_id=? ");
            inststmt.setString(1, customerID);
            int rowcount = inststmt.executeUpdate();
            System.out.println("Number of rows updated in payment table: " + rowcount);
            inststmt.close();

            inststmt = conn.prepareStatement(" delete from rental where customer_id=? ");
            inststmt.setString(1, customerID);
            rowcount = inststmt.executeUpdate();
            System.out.println("Number of rows updated in rental table: " + rowcount);
            inststmt.close();

            inststmt = conn.prepareStatement(" delete from customer where customer_id=? ");
            inststmt.setString(1, customerID);
            rowcount = inststmt.executeUpdate();
            System.out.println("Number of rows updated in customer table: " + rowcount);
            inststmt.close();

            // confirm that these are the changes you want to make
            conn.commit();
            // if other parts of the program needs commit per SQL statement
            // conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     *  Option C:
     *  Report the total sales for a given month
     *  Input: Month as a number
     *  Output: Total sales for the given month
     * @param conn Connection object to the DB
     */
    private static void totalSales(Connection conn) {
        if (conn==null) throw new NullPointerException();
        try {

            createProcedure(conn);

            String monthString = "";
            while(!isStringInt(monthString)){
                monthString = JOptionPane.showInputDialog("Enter month as number: ");
            }

            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL my_total_sales(?,?)}");

            inststmt.setInt(1, Integer.parseInt(monthString));
            inststmt.registerOutParameter(2, Types.DOUBLE);


            inststmt.executeUpdate();

            double salesResult = inststmt.getDouble(2);
            System.out.println("Sales: " + salesResult);

            JOptionPane.showMessageDialog(null, "Total Sales in month " + monthString + " is: " + salesResult);

            inststmt.close();

            // confirm that these are the changes you want to make
            conn.commit();
            // if other parts of the program needs commit per SQL statement
            // conn.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Function to create the my_total_sales procedure
     * Drops the procedure if already created.
     * @param con Connection object to the DB
     */
    private static void createProcedure(Connection con) {

        String queryDrop = "DROP PROCEDURE IF EXISTS my_total_sales";

        String createProcedure =
                "CREATE PROCEDURE my_total_sales(IN given_month INT, OUT total_amount double)\n" +
                        "SELECT sum(p.amount) INTO total_amount\n" +
                        " FROM payment p\n" +
                        " INNER JOIN rental AS r ON p.rental_id = r.rental_id\n" +
                        " WHERE month(r.rental_date)=given_month";

        try (Statement stmtDrop = con.createStatement()) {
            System.out.println("Calling DROP PROCEDURE");
            stmtDrop.execute(queryDrop);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement stmt = con.createStatement()) {
            stmt.executeUpdate(createProcedure);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Checks if the given String is an integer.
     * @param s String to check
     * @return True if Integer, False if not Integer.
     */
    private static boolean isStringInt(String s)
    {
        try
        {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex)
        {
            return false;
        }
    }


    /**
     * Handle the login view and retrive the input username and password.
     * @return String[] with string[0] as the username, and string[1] as the password
     */
    private static String[] loginDialog() {
        String[] result = new String[2];
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints cs = new GridBagConstraints();

        cs.fill = GridBagConstraints.HORIZONTAL;

        JLabel lbUsername = new JLabel("Username: ");
        cs.gridx = 0;
        cs.gridy = 0;
        cs.gridwidth = 1;
        panel.add(lbUsername, cs);

        JTextField tfUsername = new JTextField(20);
        cs.gridx = 1;
        cs.gridy = 0;
        cs.gridwidth = 2;
        panel.add(tfUsername, cs);

        JLabel lbPassword = new JLabel("Password: ");
        cs.gridx = 0;
        cs.gridy = 1;
        cs.gridwidth = 1;
        panel.add(lbPassword, cs);

        JPasswordField pfPassword = new JPasswordField(20);
        cs.gridx = 1;
        cs.gridy = 1;
        cs.gridwidth = 2;
        panel.add(pfPassword, cs);
        panel.setBorder(new LineBorder(Color.GRAY));

        String[] options = new String[] { "OK", "Cancel" };
        int ioption = JOptionPane.showOptionDialog(null, panel, "Login", JOptionPane.OK_OPTION,
                JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (ioption == 0) // pressing OK button
        {
            result[0] = tfUsername.getText();
            result[1] = new String(pfPassword.getPassword());
        }
        return result;
    }

}
