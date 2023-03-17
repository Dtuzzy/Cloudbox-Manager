package main.java.org.wuxian.machines;

import org.opencv.core.Core;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

public class Administration {

    JPanel total_pane, login_image, sign_in;
    JTextField email, password;
    JButton create_student, live_mode, exit;
    JFrame content_frame;
    public Administration() {
        content_frame = new JFrame("Facial Recognition");
        total_pane = new JPanel();
        total_pane.setLayout(new GridLayout());
        ImageIcon imageIcon = new  ImageIcon(getClass().getResource("machine.jpg"));
        JLabel image = new JLabel(imageIcon);
        login_image = new JPanel();
        login_image.add(image);

        ImageIcon logo_Icon = new ImageIcon(getClass().getResource("afe_logo.png"));
        JLabel logo_image = new JLabel(logo_Icon);
        logo_image.setBounds(190,5,100,110);
        sign_in = new JPanel(new BorderLayout());
        Container form_pane = new Container();
        JLabel title = new JLabel("Simple Access System Using Machine Learning");
        title.setFont(new Font("Lucida", Font.PLAIN, 20));
        title.setBounds(30,120,500,50);
        sign_in.setBackground(Color.WHITE);
        form_pane.setBackground(Color.WHITE);

        form_pane.add(logo_image);
        form_pane.add(title);
        create_student = new JButton("Create Student");
        create_student.setBounds(105,190,250,50);
        create_student.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new CreateStudents();
            }
        });
        form_pane.add(create_student);

        live_mode = new JButton("Live Mode");
        live_mode.setBounds(105,260,250,50);
        live_mode.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new Recognize();
            }
        });
        form_pane.add(live_mode);

        exit = new JButton("Exit");
        exit.setBounds(105,320,250,50);
        form_pane.add(exit);


        sign_in.add(form_pane,BorderLayout.CENTER);


        total_pane.add(sign_in);
        total_pane.add(login_image);

        content_frame.getContentPane().add(total_pane);
        content_frame.setSize(1000, 600);
        content_frame.setVisible(true);
        content_frame.setLocationRelativeTo(null);
        content_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) throws UnsupportedLookAndFeelException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        UIManager.setLookAndFeel("com.jtattoo.plaf.smart.SmartLookAndFeel");
        new Administration();
    }
}
