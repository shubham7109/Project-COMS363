package coms363;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.sql.*;
import java.util.Vector;

public class MainJDBCProject {

    /**
     * Main method to handle the option selection
     */
    public static void main(String[] args) {
        String dbServer = "jdbc:mysql://127.0.0.1:3306/group105?allowPublicKeyRetrieval=true&useSSL=false";
        // For compliance with existing applications not using SSL the verifyServerCertificate property is set to ‘false’,
        String userName;
        String password;

        String[] result = loginDialog();
        //userName = result[0];
        //password = result[1];
        userName = "cs363";
        password = "363F2020";

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
            String instruction =
                      "Option a: Q3) Find hashtags that appeared in the most number of states in a given year" + "\n"
                    + "Option b: Q7) Find users who used a given hashtag in a given state in a given month of a given year.\n"
                    + "Option c: Q9) Find top most followed users in a given party \n"
                    + "Option d: Q16) Show names and categories of users \n"
                    + "Option e: Q18) Find users who were mentioned the most in tweets of users of a given party \n"
                    + "Option f: Q23) Find most used hashtags \n"
                    + "Option g: Insert: Information of a new user into the database \n"
                    + "Option h: Delete: A user \n"
                    + "Option q: Quit";

            label:
            while (true) {
                option = JOptionPane.showInputDialog(instruction);
                if(option == null){
                    System.out.println("Exiting application...");
                    System.exit(0);
                }

                switch (option) {
                    case "a":
                        findHashtagsInMostStates(conn);
                        break;
                    case "b":
                        findHashtagsInGivenState(conn);
                        break;
                    case "c":
                        topFollowedUsers(conn);
                        break;
                    case "d":
                        namesAndCategories(conn);
                        break;
                    case "e":
                        mostMentions(conn);
                        break;
                    case "f":
                        mostHashtagsInCategories(conn);
                        break;
                    case "g":
                        insertNewUser(conn);
                        break;
                    case "h":
                        deleteUser(conn);
                        break;
                    case "q":
                        System.out.println("Exiting application");
                        break label;
                    default:
                        break;
                }
            }

            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println("Program terminates due to errors");
            //System.out.println(e.getMessage());
            e.printStackTrace(); // for debugging
        }
    }

    private static void deleteUser(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String numHashtagsString = "";
        String yearString = "";

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter the year: ");
        }

        while(!isInteger(numHashtagsString)){
            numHashtagsString = JOptionPane.showInputDialog("Enter number of hashtags to find: ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q3(?,?)}");

            inststmt.setInt(1, Integer.parseInt(numHashtagsString));
            inststmt.setInt(2, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Hashtags in the most number of states");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    private static void insertNewUser(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String numHashtagsString = "";
        String yearString = "";

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter the year: ");
        }

        while(!isInteger(numHashtagsString)){
            numHashtagsString = JOptionPane.showInputDialog("Enter number of hashtags to find: ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q3(?,?)}");

            inststmt.setInt(1, Integer.parseInt(numHashtagsString));
            inststmt.setInt(2, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Hashtags in the most number of states");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Q23
     * @param conn
     */
    private static void mostHashtagsInCategories(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String k = "";
        String category = "";
        String yearString = "";
        String monthString = "";

        while(category.equals("")){
            category = JOptionPane.showInputDialog("Enter category (Eg: 'GOP'):");
        }

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter year (Eg: 2016):");
        }

        while(monthString.equals("")){
            monthString = JOptionPane.showInputDialog("Enter months as an array (Eg: '1,2,3'): ");
        }

        while(!isInteger(k)){
            k = JOptionPane.showInputDialog("Enter number of results to find (k): ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q23(?,?,?,?)}");

            inststmt.setInt(1, Integer.parseInt(k));
            inststmt.setString(2, category);
            inststmt.setString(3, monthString);
            inststmt.setInt(4, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Most used hashtags with the count of tweets");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Q18
     * @param conn
     */
    private static void mostMentions(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String k = "";
        String category = "";
        String yearString = "";
        String monthString = "";

        while(category.equals("")){
            category = JOptionPane.showInputDialog("Enter category (Eg: 'GOP'):");
        }

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter year: (Eg: 2016):");
        }

        while(!isInteger(monthString)){
            monthString = JOptionPane.showInputDialog("Enter month as a number of the given year: ");
        }

        while(!isInteger(k)){
            k = JOptionPane.showInputDialog("Enter number of results to find (k): ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q18(?,?,?,?)}");

            inststmt.setInt(1, Integer.parseInt(k));
            inststmt.setString(2,category);
            inststmt.setInt(3, Integer.parseInt(monthString));
            inststmt.setInt(4, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Users who were mentioned the most in tweets");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Q16
     * @param conn
     */
    private static void namesAndCategories(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String k = "";
        String yearString = "";
        String monthString = "";

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter year: (Eg: 2016):");
        }

        while(!isInteger(monthString)){
            monthString = JOptionPane.showInputDialog("Enter month as a number of the given year: ");
        }

        while(!isInteger(k)){
            k = JOptionPane.showInputDialog("Enter number of results to find (k): ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q16(?,?,?)}");

            inststmt.setInt(1, Integer.parseInt(k));
            inststmt.setInt(2, Integer.parseInt(monthString));
            inststmt.setInt(3, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Names and categories in desc order of retweets\n");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Q9
     * @param conn
     */
    private static void topFollowedUsers(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String k = "";
        String category = "";


        while(category.equals("")){
            category = JOptionPane.showInputDialog("Enter category (Eg: 'GOP'): ");
        }

        while(!isInteger(k)){
            k = JOptionPane.showInputDialog("Enter number of results to find (k): ");
        }


        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q9(?,?)}");

            inststmt.setString(1, category);
            inststmt.setInt(2, Integer.parseInt(k));

            rs = inststmt.executeQuery();
            buildTableModel(rs," Most followed users in a given party");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Q7
     * @param conn
     */
    private static void findHashtagsInGivenState(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String inputHastag = "";
        String stateName = "";
        String monthString = "";
        String yearString = "";
        String k = "";

        while(inputHastag.equals("")){
            inputHastag = JOptionPane.showInputDialog("Enter Hashtag without #: ");
        }

        while(stateName.equals("")){
            stateName = JOptionPane.showInputDialog("Enter state name in short format (Eg: IA): ");
        }

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter year: (Eg: 2016):");
        }

        while(!isInteger(monthString)){
            monthString = JOptionPane.showInputDialog("Enter month as a number of the given year: ");
        }


        while(!isInteger(k)){
            k = JOptionPane.showInputDialog("Enter number of results to find (k): ");
        }


        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q7(?,?,?,?,?)}");

            inststmt.setString(1, inputHastag);
            inststmt.setString(2, stateName);
            inststmt.setInt(3, Integer.parseInt(k));
            inststmt.setInt(4, Integer.parseInt(monthString));
            inststmt.setInt(5, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Users who used a given hashtag");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    /**
     * Q3
     * @param conn
     */
    private static void findHashtagsInMostStates(Connection conn) {
        if (conn==null) throw new NullPointerException();

        String numHashtagsString = "";
        String yearString = "";

        while(!isInteger(yearString)){
            yearString = JOptionPane.showInputDialog("Enter the year: ");
        }

        while(!isInteger(numHashtagsString)){
            numHashtagsString = JOptionPane.showInputDialog("Enter number of hashtags to find: ");
        }

        try {
            ResultSet rs;
            // we want to make sure that all the query and update statements
            // are considered as one unit; both got done or none got done
            conn.setAutoCommit(false);

            CallableStatement  inststmt = conn.prepareCall("{CALL q3(?,?)}");

            inststmt.setInt(1, Integer.parseInt(numHashtagsString));
            inststmt.setInt(2, Integer.parseInt(yearString));

            rs = inststmt.executeQuery();
            buildTableModel(rs,"Hashtags in the most number of states");

            inststmt.close();
            // confirm that these are the changes you want to make
            conn.commit();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public static void buildTableModel(ResultSet rs, String message) throws SQLException {

        ResultSetMetaData metaData = rs.getMetaData();

        // names of columns
        Vector<String> columnNames = new Vector<String>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // data of the table
        Vector<Vector<Object>> data = new Vector<Vector<Object>>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<Object>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        //table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        JScrollPane jScrollPane = new JScrollPane(table);
        jScrollPane.setSize(30,30);
        JOptionPane.showMessageDialog(null, jScrollPane, message, JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * Checks if the number is integer
     * @return True if integer. False if not integer.
     */
    private static boolean isInteger(String numHashtags) {
        try{
            Integer.parseInt(numHashtags);
        }catch (NumberFormatException e){
            return false;
        }
        return true;
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
