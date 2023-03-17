package main.java.org.wuxian.database;

import org.opencv.core.Mat;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Portal {

    Connection conn = DatabaseConnection.getConnection();


    public void insertTrainedKeyPoints(String matric_number, String filename, Mat src, byte[] bytes) throws SQLException {

        String sql = "INSERT INTO `trained`(`matric_number`, `fileUrl`, `mat`, `keyPoints`,`status`) VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, matric_number);
        preparedStatement.setString(2, filename);
        preparedStatement.setString(3, src.toString());
        preparedStatement.setBytes(4, bytes);
        preparedStatement.setString(5, "N");
        preparedStatement.executeUpdate();
        conn.close();
    }

    public int insertStudents(String student_id, String mat_number, String full_name, String department, String phone, String file) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `students`(`student_id`, `matric_number`, `full_name`, `phone`, `department`, `file`,`active`) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, student_id);
        preparedStatement.setString(2, mat_number);
        preparedStatement.setString(3, full_name);
        preparedStatement.setString(4, phone);
        preparedStatement.setString(5, department);
        preparedStatement.setString(6, file);
        preparedStatement.setString(7, "Y");
        status = preparedStatement.executeUpdate();
        conn.close();
        return status;
    }

    public int insertAttendance(String student_id, String mat_number, String full_name, String department, String course, String phone, String file) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `attendance`(`student_id`, `matric_number`, `full_name`, `phone`, `department`,`classes`, `file`,`is_present`) VALUES (?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, student_id);
        preparedStatement.setString(2, mat_number);
        preparedStatement.setString(3, full_name);
        preparedStatement.setString(4, phone);
        preparedStatement.setString(5, department);
        preparedStatement.setString(6, course);
        preparedStatement.setString(7, file);
        preparedStatement.setString(8, "Y");
        status = preparedStatement.executeUpdate();
        conn.close();
        return status;
    }

    public int insertImages(String student_id, String fileURL) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `images`(`student_id`, `image_url`) VALUES (?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, student_id);
        preparedStatement.setString(2, fileURL);
        status = preparedStatement.executeUpdate();
        conn.close();
        return status;
    }

    public void updateFlag(String matNumber) throws SQLException {
        String sql = "UPDATE `trained` SET status = 'N' WHERE `matric_number` = '" + matNumber + "'";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.executeUpdate();
        conn.close();
    }

    public ArrayList<String> getStudentDetails(String student_id) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = "Select * from `students` where `student_id` = '" + student_id + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            res.add(resultSet.getString("matric_number"));
            res.add(resultSet.getString("full_name"));
            res.add(resultSet.getString("department"));
            res.add(resultSet.getString("phone"));
            res.add(resultSet.getString("file"));
            res.add(resultSet.getString("student_id"));
        }
        statement.close();
        conn.close();

        return res;
    }


    public String[][] getAllStudentDetails() throws SQLException {
        String[][] res = new String[0][0];
        String sql = "SELECT `student_id`, `matric_number`, `full_name`, `phone`, `department` FROM `students`";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        resultSet.last();
        int rowCount = resultSet.getRow();
        resultSet.beforeFirst();

        // Determine the number of columns in the result set
        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();

        String[][] results = new String[rowCount][columnCount];

        int row = 0;
        while (resultSet.next()) {
            for (int col = 0; col < columnCount; col++) {
                results[row][col] = resultSet.getString(col + 1);
            }
            row++;
        }
        statement.close();
        conn.close();

        return results;
    }


    public DefaultListModel getStudentAttendanceArray(String student_id) throws SQLException {
        DefaultListModel model = new DefaultListModel();
        String sql = "SELECT student_id, full_name,classes, COUNT(*) as count \n" +
                "FROM attendance WHERE student_id = '" + student_id + "'\n" +
                "GROUP BY classes \n" +
                "ORDER BY count DESC";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            String classes = resultSet.getString("classes");
            String count = resultSet.getString("count");
            if (classes.isEmpty()){
                model.addElement("No Attendance");
            }else{
                model.addElement(classes +" : "+count);
            }
        }
        statement.close();
        conn.close();

        return model;
    }

    public ArrayList<String> getStudentDetailsByImage(String student_id) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = "SELECT S.student_id, S.matric_number, S.full_name, S.department, S.phone, I.image_url from students S, images I where S.student_id = I.student_id AND I.student_id = '" + student_id + "'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            res.add(resultSet.getString("matric_number"));
            res.add(resultSet.getString("full_name"));
            res.add(resultSet.getString("department"));
            res.add(resultSet.getString("phone"));
            res.add(resultSet.getString("image_url"));
            res.add(resultSet.getString("student_id"));
        }
        statement.close();
        conn.close();

        return res;
    }


    public void insertKeyPoints(String matric_number, String filename, Mat src, byte[] bytes) throws SQLException {
        String sql = "INSERT INTO `data_bank`(`matric_number`, `fileUrl`, `mat`, `keyPoints`) VALUES (?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1, matric_number);
        preparedStatement.setString(2, filename);
        preparedStatement.setString(3, src.toString());
        preparedStatement.setBytes(4, bytes);
        preparedStatement.executeUpdate();
        conn.close();
    }


   //  public static void main(String[] args) throws SQLException {
      //  System.out.println(new Portal().getAllStudentDetails());
//}

}
