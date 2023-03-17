package main.java.org.wuxian.machines;

import main.java.org.wuxian.database.Portal;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.util.ArrayList;


public class AttendanceTable {

    JTable jt;
    JList<String> list;
    JScrollPane js;
    JComboBox classes;
    String [] dep = {"Csc 402", "Csc 404", " Csc 406","Csc 408","Csc 410","Csc 466"};
    ImageIcon imageIcon;
    Container container2;
    JLabel result_full_name, result_matric_number, result_department, result_student_id, result_student_phone, image_label, lecture, message;
    JButton model, validate;
    public AttendanceTable() throws SQLException {
        JFrame frame = new JFrame("Attendance Table");
        Container container = new Container();
        container2 = new Container();


        list = new JList<>();

        String data[][]= new Portal().getAllStudentDetails();
        String column[]={"STUDENT-ID","MATRIC-NO","NAME","PHONE","DEPARTMENT"};
        jt=new JTable(data,column);
        jt.setCellSelectionEnabled(false);
        ListSelectionModel select= jt.getSelectionModel();
        select.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        select.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                String data = null;
                int[] row = jt.getSelectedRows();
                int[] columns = jt.getSelectedColumns();
                for (int i = 0; i < row.length; i++) {
                    for (int j = 0; j < columns.length; j++) {
                        data = (String) jt.getValueAt(row[i], columns[0]);
                    } }
                System.out.println("Table element selected is: " + data);
                ArrayList result = null;
                try {
                    container2.removeAll();
                    result = new Portal().getStudentDetails(data);
                    System.out.println(result);
                    resultContainer(result.get(0).toString(),result.get(1).toString(),result.get(2).toString(), result.get(3).toString(),result.get(4).toString(),result.get(5).toString());
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        js =new JScrollPane(jt);
        js.setBounds(0,0,600,600);
        container2.setBounds(601,0,399,300);
        list.setBounds(601,401,399,200);
        container.add(js);
        container.add(container2);
        container.add(list);
        frame.getContentPane().add(container);

        frame.setSize(1000, 600);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                e.getWindow().dispose();
            }
        });
    }


    public void resultContainer(String o, String o1, String o2, String o3, String s, String o4) throws SQLException {

        result_student_id = new JLabel();
        result_student_id.setFont(new Font("Lucida", Font.PLAIN, 14));
        result_student_id.setBounds(20,20,400,22);
        container2.add(result_student_id);

        result_full_name = new JLabel();
        result_full_name.setFont(new Font("Lucida", Font.PLAIN, 14));
        result_full_name.setBounds(20,44,400,22);
        container2.add(result_full_name);

        result_matric_number = new JLabel();
        result_matric_number.setFont(new Font("Lucida", Font.PLAIN, 14));
        result_matric_number.setBounds(20,68,400,22);
        container2.add(result_matric_number);

        result_student_phone = new JLabel();
        result_student_phone.setFont(new Font("Lucida", Font.PLAIN, 14));
        result_student_phone.setBounds(20,92,400,22);
        container2.add(result_student_phone);

        result_department = new JLabel();
        result_department.setFont(new Font("Lucida", Font.PLAIN, 14));
        result_department.setBounds(20,116,400,22);
        container2.add(result_department);


        image_label = new JLabel();
        image_label.setBounds(20,140,150,150);
        container2.add(image_label);

        lecture = new JLabel("Number Of Courses And Attendance");
        lecture.setBounds(20,290,300,22);
        lecture.setFont(new Font("Lucida", Font.PLAIN, 14));
        container2.add(lecture);

        result_student_id.setText("Student Matric-No : " + o);
        result_full_name.setText("Student Name : " + o1);
        result_department.setText("Department : " + o2);
        result_student_phone.setText("Student Phone: " + o3);
        imageIcon = new ImageIcon(s);
        image_label.setIcon(imageIcon);

        DefaultListModel<String> l1 = new Portal().getStudentAttendanceArray(o4);
        list.setModel(l1);
    }
   // public static void main(String[] a) throws SQLException {

  //      new AttendanceTable();
  //  }
}