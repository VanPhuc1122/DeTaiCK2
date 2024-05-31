
package ViewClient;

import com.mysql.cj.xdevapi.Result;
import com.sun.jdi.connect.spi.Connection;
import java.beans.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Login_dangki extends javax.swing.JFrame {

    public Login_dangki() {
        initComponents();
        setTitle("Form Register!");
        configureButton(button_login);
        configureButton(button_reghister);
          setIconImage(new ImageIcon(getClass().getResource("/images/logodangki.png")).getImage());
    }
    // hàm này dùng để xoá viền ngoài button
     private void configureButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }
    // Kết nối Mysql
    String driver =  "com.mysql.cj.jdbc.Driver";
    String url = "jdbc:mysql://localhost:3306/chatdoan";
    String username = "root";
    String password = "";
    Statement st;
    ResultSet rs;
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        label_username = new javax.swing.JLabel();
        label_IP = new javax.swing.JLabel();
        label_port = new javax.swing.JLabel();
        label_pass = new javax.swing.JLabel();
        label_confim = new javax.swing.JLabel();
        text_username = new javax.swing.JTextField();
        text_IP = new javax.swing.JTextField();
        text_port = new javax.swing.JTextField();
        text_pass = new javax.swing.JPasswordField();
        text_configpass = new javax.swing.JPasswordField();
        button_reghister = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        button_login = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Segoe UI Historic", 1, 36)); // NOI18N
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-register-100.png"))); // NOI18N
        jLabel1.setText("Register Now");
        getContentPane().add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 90, 351, 69));

        label_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_username.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-username-48.png"))); // NOI18N
        label_username.setText("UserName");
        getContentPane().add(label_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 210, -1, -1));

        label_IP.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_IP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-adress-35.png"))); // NOI18N
        label_IP.setText("Địa Chỉ IP");
        getContentPane().add(label_IP, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 300, -1, -1));

        label_port.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_port.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-port-35.png"))); // NOI18N
        label_port.setText("Port");
        getContentPane().add(label_port, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 380, 130, -1));

        label_pass.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_pass.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-password-48.png"))); // NOI18N
        label_pass.setText("Password");
        getContentPane().add(label_pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 450, -1, -1));

        label_confim.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_confim.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-show-password-48.png"))); // NOI18N
        label_confim.setText("Confim Password");
        getContentPane().add(label_confim, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 540, -1, -1));

        text_username.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_username.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_username, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 220, 348, 40));

        text_IP.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_IP.setText("127.0.0.2");
        text_IP.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_IP, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 300, 348, 40));

        text_port.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_port.setText("8888");
        text_port.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        text_port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_portActionPerformed(evt);
            }
        });
        getContentPane().add(text_port, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 380, 348, 40));

        text_pass.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_pass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_pass, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 460, 348, 40));

        text_configpass.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_configpass.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        getContentPane().add(text_configpass, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 547, 348, 40));

        button_reghister.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        button_reghister.setText("Register");
        button_reghister.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_reghister.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_reghisterActionPerformed(evt);
            }
        });
        getContentPane().add(button_reghister, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 610, 108, 40));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semilight", 3, 12)); // NOI18N
        jLabel2.setText("@Coppy Right By VanPhuc");
        getContentPane().add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 680, -1, -1));

        jLabel3.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-chat-48.png"))); // NOI18N
        jLabel3.setText("Chat Application!");
        getContentPane().add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 40));
        getContentPane().add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, -1));

        button_login.setFont(new java.awt.Font("Segoe UI Semibold", 1, 20)); // NOI18N
        button_login.setText("Login");
        button_login.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        button_login.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loginActionPerformed(evt);
            }
        });
        getContentPane().add(button_login, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 610, 110, 40));

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/Purple and Yellow 3D Illustration Online Course Presentation (2) (1).png"))); // NOI18N
        jLabel5.setText("jLabel5");
        getContentPane().add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1380, 710));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_reghisterActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_reghisterActionPerformed

          String plainTextPass = new String(text_pass.getPassword());
          String confimpassword = new String(text_configpass.getPassword());
        if(text_username.getText().isEmpty() || text_IP.getText().isEmpty() || text_port.getText().isEmpty() || 
                text_pass.getText().isEmpty() || text_configpass.getText().isEmpty()){
            JOptionPane.showMessageDialog(this, "Please do not leave the information blank", "Information cannot be left blank", JOptionPane.ERROR_MESSAGE);
          return;
        }
        if(!plainTextPass.equals(new String(text_configpass.getPassword()))){
            JOptionPane.showMessageDialog(this, "Password do not match !", "Please password again", JOptionPane.ERROR_MESSAGE);
          return;
        }
        // mã hoá mật khẩu trong my sql
        String hassedPassword = Encode.hassPass(plainTextPass);
        try {
            Class.forName(driver);
            java.sql.Connection con = DriverManager.getConnection(url, username, password);
            String sql = "INSERT INTO nguoidung  VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, text_username.getText());
            ps.setString(2, text_IP.getText());
            ps.setString(3, text_port.getText());
            ps.setString(4, hassedPassword);
            ps.setString(5, hassedPassword);
            int n = ps.executeUpdate(); // cập nhật dữ liệu
            if(text_username.getText().equals("") || text_IP.getText().equals("") ||
                    text_port.getText().equals("") || text_pass.getText().equals("") || text_configpass.getText().equals("")){
                JOptionPane.showMessageDialog(this, "Please do not leave the information blank !");
            }else if(n != 0){
                JOptionPane.showMessageDialog(this, "Regist Sucess", "Successfully registered account", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(this, "Regist Fail", "Error registered account !", JOptionPane.ERROR_MESSAGE);
            }
                    } catch (Exception e) {
          e.printStackTrace();
          JOptionPane.showMessageDialog(this, "An error occurred during registration", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_button_reghisterActionPerformed

    private void button_loginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loginActionPerformed
            
        Login_form lg = new Login_form();
            lg.setVisible(true);
            dispose();
    }//GEN-LAST:event_button_loginActionPerformed

    private void text_portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_portActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_text_portActionPerformed

    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Login_dangki().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton button_login;
    private javax.swing.JButton button_reghister;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel label_IP;
    private javax.swing.JLabel label_confim;
    private javax.swing.JLabel label_pass;
    private javax.swing.JLabel label_port;
    private javax.swing.JLabel label_username;
    private javax.swing.JTextField text_IP;
    private javax.swing.JPasswordField text_configpass;
    private javax.swing.JPasswordField text_pass;
    private javax.swing.JTextField text_port;
    private javax.swing.JTextField text_username;
    // End of variables declaration//GEN-END:variables
}
