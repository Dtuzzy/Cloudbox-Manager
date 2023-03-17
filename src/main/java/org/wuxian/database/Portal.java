package main.java.org.wuxian.database;

import org.opencv.core.Mat;

import java.sql.*;
import java.util.ArrayList;

public class Portal {

    Connection conn = DatabaseConnection.getConnection();


    public void insertTrainedKeyPoints(String matric_number, String filename, Mat src, byte[] bytes) throws SQLException {

        String sql = "INSERT INTO `trained`(`matric_number`, `fileUrl`, `mat`, `keyPoints`,`status`) VALUES (?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,matric_number);
        preparedStatement.setString(2,filename);
        preparedStatement.setString(3,src.toString());
        preparedStatement.setBytes(4,bytes);
        preparedStatement.setString(5,"N");
        preparedStatement.executeUpdate();
        conn.close();
    }

    public int insertStudents(String student_id, String mat_number, String full_name,String department, String phone, String file) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `students`(`student_id`, `matric_number`, `full_name`, `phone`, `department`, `file`,`active`) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,student_id);
        preparedStatement.setString(2,mat_number);
        preparedStatement.setString(3,full_name);
        preparedStatement.setString(4,phone);
        preparedStatement.setString(5,department);
        preparedStatement.setString(6,file);
        preparedStatement.setString(7,"Y");
        status = preparedStatement.executeUpdate();
        conn.close();
        return  status;
    }

    public int insertAttendance(String student_id, String mat_number, String full_name,String department, String phone, String file) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `attendance`(`student_id`, `matric_number`, `full_name`, `phone`, `department`, `file`,`active`) VALUES (?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,student_id);
        preparedStatement.setString(2,mat_number);
        preparedStatement.setString(3,full_name);
        preparedStatement.setString(4,phone);
        preparedStatement.setString(5,department);
        preparedStatement.setString(6,file);
        preparedStatement.setString(7,"Y");
        status = preparedStatement.executeUpdate();
        conn.close();
        return  status;
    }

    public int insertImages(String student_id, String fileURL) throws SQLException {
        int status = 0;
        String sql = "INSERT INTO `images`(`student_id`, `image_url`) VALUES (?,?)";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.setString(1,student_id);
        preparedStatement.setString(2,fileURL);
        status = preparedStatement.executeUpdate();
        conn.close();
        return  status;
    }

    public void updateFlag(String matNumber) throws SQLException {
        String sql = "UPDATE `trained` SET status = 'N' WHERE `matric_number` = '"+matNumber+"'";
        PreparedStatement preparedStatement = conn.prepareStatement(sql);
        preparedStatement.executeUpdate();
        conn.close();
    }

    public ArrayList<String> getStudentDetails(String student_id) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = "Select * from `students` where `student_id` = '"+student_id+"'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()){
            res.add(resultSet.getString("matric_number"));
            res.add(resultSet.getString("full_name"));
            res.add(resultSet.getString("department"));
            res.add(resultSet.getString("phone"));
            res.add(resultSet.getString("file"));
        }
        statement.close();
        conn.close();

        return res;
    }

    public ArrayList<String> getStudentDetailsByImage(String student_id) throws SQLException {
        ArrayList<String> res = new ArrayList<>();
        String sql = "SELECT S.student_id, S.matric_number, S.full_name, S.department, S.phone, I.image_url from students S, images I where S.student_id = I.student_id AND I.student_id = '"+student_id+"'";
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        if (resultSet.next()){
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
        preparedStatement.setString(1,matric_number);
        preparedStatement.setString(2,filename);
        preparedStatement.setString(3,src.toString());
        preparedStatement.setBytes(4,bytes);
        preparedStatement.executeUpdate();
        conn.close();
    }


    //public static void main(String[] args) throws SQLException {
  //      System.out.println(new Portal().getStudentDetailsByImage("2EI0ZBOW"));
  //  }
}
