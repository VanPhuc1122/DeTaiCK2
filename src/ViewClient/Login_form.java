package ViewClient;

import com.mysql.cj.jdbc.Driver;
import com.sun.jdi.connect.spi.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Login_form extends javax.swing.JFrame {

    public Login_form() {
        initComponents();
        setTitle("Welcome to InterFace : VanPhuc");
        setResizable(false);
         setIconImage(new ImageIcon(getClass().getResource("/images/logologin.png")).getImage());
    }

    // Kết nối đến MYSQL
    String driver = "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/chatdoan";
    String username = "root";
    String password = "";
    Statement st;
    ResultSet rs;

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        label_sign = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        label_password = new javax.swing.JLabel();
        Button_login = new javax.swing.JButton();
        label_cauhoi = new javax.swing.JLabel();
        Button_clickdangkiii = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        button_giaima = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        label_diachiIP = new javax.swing.JLabel();
        label_port = new javax.swing.JLabel();
        text_password1 = new javax.swing.JPasswordField();
        jLabel7 = new javax.swing.JLabel();
        text_Port = new javax.swing.JTextField();
        text_username = new javax.swing.JTextField();
        text_diachiIP = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-chat-48.png"))); // NOI18N
        jLabel1.setText("Chat Application");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));

        label_sign.setFont(new java.awt.Font("Segoe UI Semibold", 1, 36)); // NOI18N
        label_sign.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-log-in-100.png"))); // NOI18N
        label_sign.setText("Sign In Now");
        getContentPane().add(label_sign, new org.netbeans.lib.awtextra.AbsoluteConstraints(900, 130, -1, -1));

        label_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_username.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-username-48.png"))); // NOI18N
        label_username.setText("UserName");
        getContentPane().add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 240, -1, -1));

        label_password.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_password.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-password-48.png"))); // NOI18N
        label_password.setText("PassWord");
        getContentPane().add(label_password, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 330, -1, -1));

        Button_login.setBackground(new java.awt.Color(0, 0, 0));
        Button_login.setFont(new java.awt.Font("Segoe UI Semibold", 1, 19)); // NOI18N
        Button_login.setForeground(new java.awt.Color(255, 255, 255));
        Button_login.setText("Log In");
        Button_login.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Button_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_loginActionPerformed(evt);
            }
        });
        getContentPane().add(Button_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(990, 550, 160, 50));

        label_cauhoi.setFont(new java.awt.Font("Segoe UI", 2, 20)); // NOI18N
        label_cauhoi.setText("Would you like to create an account?");
        getContentPane().add(label_cauhoi, new org.netbeans.lib.awtextra.AbsoluteConstraints(510, 640, -1, -1));

        Button_clickdangkiii.setBackground(new java.awt.Color(0, 0, 0));
        Button_clickdangkiii.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        Button_clickdangkiii.setForeground(new java.awt.Color(255, 255, 255));
        Button_clickdangkiii.setText("Click Register");
        Button_clickdangkiii.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Button_clickdangkiii.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_clickdangkiiiActionPerformed(evt);
            }
        });
        getContentPane().add(Button_clickdangkiii, new org.netbeans.lib.awtextra.AbsoluteConstraints(850, 630, 160, 50));

        jLabel4.setText("jLabel4");
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        button_giaima.setBackground(new java.awt.Color(0, 0, 0));
        button_giaima.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        button_giaima.setForeground(new java.awt.Color(255, 255, 255));
        button_giaima.setText("Decryption Password");
        button_giaima.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_giaima.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_giaimaActionPerformed(evt);
            }
        });
        getContentPane().add(button_giaima, new org.netbeans.lib.awtextra.AbsoluteConstraints(1130, 630, 200, 50));

        jLabel2.setText("jLabel2");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        label_diachiIP.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_diachiIP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-adress-35.png"))); // NOI18N
        label_diachiIP.setText("Địa Chỉ IP");
        getContentPane().add(label_diachiIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 420, -1, -1));

        label_port.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_port.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-port-35.png"))); // NOI18N
        label_port.setText("Port");
        getContentPane().add(label_port, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 490, -1, -1));

        text_password1.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_password1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_password1, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 330, 358, 42));

        jLabel7.setText("jLabel7");
        getContentPane().add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        text_Port.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_Port.setText("8888");
        text_Port.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_Port, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 490, 358, 42));

        text_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 240, 358, 42));

        text_diachiIP.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_diachiIP.setText("127.0.0.2");
        text_diachiIP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_diachiIP, new org.netbeans.lib.awtextra.AbsoluteConstraints(890, 410, 358, 42));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/Purple and Yellow 3D Illustration Online Course Presentation (3) (1).png"))); // NOI18N
        jLabel5.setText("jLabel5");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 700));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Button_clickdangkiiiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_clickdangkiiiActionPerformed
        Login_dangki dk = new Login_dangki();
        dk.setVisible(true);
        dispose();
    }//GEN-LAST:event_Button_clickdangkiiiActionPerformed
    // sử dụng các câu lệnh để truy vấn vào SQL
    private void Button_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_loginActionPerformed

        try {
            Class.forName(driver);
            java.sql.Connection con = DriverManager.getConnection(url, username, password);
            String sql = "SELECT Password FROM nguoidung WHERE Username = ? ";
            PreparedStatement pr = con.prepareStatement(sql); // tránh trường hợp hacker sử dụng câu lệnh chèn Or 1 1 để lấy thông tin
            pr.setString(1, text_username.getText());
            rs = pr.executeQuery(); // thực thi câu lệnh
            if (text_username.getText().equals("") || text_password1.getText().equals("") || text_diachiIP.getText().equals(" ") || text_Port.getText().equals(" ")) {
                JOptionPane.showMessageDialog(this, "Please Enter Information User", "Lỗi điền form", JOptionPane.INFORMATION_MESSAGE);
            } else if (rs.next()) { // cs dữ liệu
                // phần này nó so sánh giữa mật khẩu ban đầu đã lưu so sánh với mật khẩu đã mã hoá mảng băm để tiến hành login
                String storedHashedPassword = rs.getString("Password");
                String plainTextPassword = new String(text_password1.getPassword());
                if (Encode.decodeCode(plainTextPassword, storedHashedPassword)) {
                    JOptionPane.showMessageDialog(this, "Login Sucessful", "Đăng nhập thành công sau khi giải mã", JOptionPane.INFORMATION_MESSAGE);
                    connectToServer();
                } else {
                    JOptionPane.showMessageDialog(this, "Login Fail", "Mật khẩu mã hoá không giống với mật khẩu bạn nhập!", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Login information is incorrect");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_Button_loginActionPerformed


    private void button_giaimaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_giaimaActionPerformed
        try {
            Class.forName(driver);
            java.sql.Connection con = DriverManager.getConnection(url, username, password);
            String sql = "SELECT Password FROM nguoidung WHERE Username = ?";
            PreparedStatement pr = con.prepareStatement(sql);
            pr.setString(1, text_username.getText());
            rs = pr.executeQuery();
            if (rs.next()) {
                String storedHashedPassword = rs.getString("Password");
                String plainTextPassword = new String(text_password1.getPassword());
                if (Encode.decodeCode(plainTextPassword, storedHashedPassword)) {
                    JOptionPane.showMessageDialog(this, "Successful login!");
                } else {
                    
                }

            } else {
                JOptionPane.showMessageDialog(this, "Username not found !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // hàm kết nối đến Server
    private void connectToServer() {
        if (text_diachiIP.getText().length() > 0 && text_Port.getText().length() > 0 && text_username.getText().length() > 0) {
            if (text_username.getText().length() <= 20) {
                /*   xóa Username  */
                String username = text_username.getText();
                String u = username.replace(" ", "_");

                Client client = new Client();
                client.initFrame(u, text_diachiIP.getText(), Integer.parseInt(text_Port.getText()));
                //  kiểm tra nếu như được kết nối
                if (client.isConnected()) { // kết nối đến server
                    client.setLocationRelativeTo(null);
                    client.setVisible(true);
                    setVisible(false);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Account must be maximum 15 characters including [space].!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Form not completed.!", "Error!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_button_giaimaActionPerformed

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login_form().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_clickdangkiii;
    private javax.swing.JButton Button_login;
    private javax.swing.JButton button_giaima;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel label_cauhoi;
    private javax.swing.JLabel label_diachiIP;
    private javax.swing.JLabel label_password;
    private javax.swing.JLabel label_port;
    private javax.swing.JLabel label_sign;
    private javax.swing.JLabel label_username;
    private javax.swing.JTextField text_Port;
    private javax.swing.JTextField text_diachiIP;
    private javax.swing.JPasswordField text_password1;
    private javax.swing.JTextField text_username;
    // End of variables declaration//GEN-END:variables
}
