
package ViewServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Server extends javax.swing.JFrame {
     // Tạo ra dateFormat để ép dữu liệu khi mình đẩy dữ liệu lên text thông báo thì nó hiển thị ra giờ mình đẩy lên.
    SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
    Thread t; // Tạo biến toàn cục cho cái luồng
    ServerHandle serverHandle;

    // Tạo ra vector để lưu trữ thông tin của client
    public Vector socketList = new Vector();
    public Vector clientList = new Vector();

    // phần này lưu trữ thông tin file của client
    public Vector clientFileSharingUsername = new Vector();
    public Vector clientFileSharingSocket = new Vector();

    ServerSocket server;

     
    public Server() {
        initComponents();
        MyInit();
        configureButton(start);
        configureButton(stop);
    }
    // hàm này dùng để xoá viền ngoài button
     private void configureButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }
    void MyInit(){
        chuchay();
         setLocationRelativeTo(null);
         setResizable(false);
         setTitle("Welcome to Server VanPhuc");
          setIconImage(new ImageIcon(getClass().getResource("/images/logoserver.png")).getImage());
     }
    // Hàm này dùng để đẩy dữ liệu lên text server
    public void appendMessage(String msg){
        Date date = new Date();
        // append thường nó sẽ thêm nội dung mới vào văn bản
        text_hienthithongbao.append(dateFormat.format(date) +": "+ msg +"\n");
        text_hienthithongbao.setCaretPosition(text_hienthithongbao.getText().length() - 1); // đặt vị trí con trỏ đén t hông báo cuối cùng trong bảng
    }
    
    public void setSocketList(Socket socket){
        try {
            
            socketList.add(socket);
            appendMessage("[List Socket]: Được thêm thành công");
        } catch (Exception e) { 
            appendMessage("[List Socket]: "+ e.getMessage()); 
        }
    }
    // hàm này khi mình đăng nhập vào nó hiển thị lên server đã tham gia chat.
    public void setClientList(String client){
        try {
            clientList.add(client);
            appendMessage("[List Client]: Được thêm thành công"); // đẩy thông tin lên phần text
        } catch (Exception e) {
            appendMessage("[List Client]: "+ e.getMessage()); }
    }
    // hàm này lưu trữ thông tin file của username
    public void setClientFileSharingUsername(String user){
        try {
            clientFileSharingUsername.add(user);
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    // lưu trữ thông tin file của socket
    public void setClientFileSharingSocket(Socket soc){
        try {
            clientFileSharingSocket.add(soc);
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
    
    // hàm này lấy ra danh sách client
    public Socket getClientList(String client){
        Socket tsoc = null;
        for(int x=0; x < clientList.size(); x++){
            if(clientList.get(x).equals(client)){ // so sánh giữa 2 client tránh bị tồn tại 2 client giống nhau
                tsoc = (Socket) socketList.get(x);
                break;
            }
        }
        return tsoc;
    }
    //  hàm này loại bỏ 1 phần tử ra khỏi danh sách
    public void removeFromTheList(String client){
        try {
            for(int x=0; x < clientList.size(); x++){
                if(clientList.elementAt(x).equals(client)){ // phần này dùng để lấy phần thứ x so sánh giữa client đã được chỉ định
                    clientList.removeElementAt(x); // xong loại bỏ phần tử đó ra khỏi danh sách client đã lưu trong vêctor
                    socketList.removeElementAt(x); // loại bỏ ra ln khỏi socket
                    appendMessage("[Remove]: "+ client);
                    break;
                }
            }
        } catch (Exception e) {
            appendMessage("[RemoveException]: "+ e.getMessage());
        }
    }
    // hàm này lấy danh sách file của client
    public Socket getClientFileSharingSocket(String username){
        Socket tsoc = null;
        for(int x=0; x < clientFileSharingUsername.size(); x++){
            if(clientFileSharingUsername.elementAt(x).equals(username)){ // lấy ra file ở vị trí x so sánh giữa username được chỉ đinhk
                tsoc = (Socket) clientFileSharingSocket.elementAt(x);  // truy cập phần tử thứ x trong file client
                break;
            }
        }
        return tsoc;
    }
     // hàm này loại bỏ file của client
        public void removeClientFileSharing(String username){
        for(int x=0; x < clientFileSharingUsername.size(); x++){
            if(clientFileSharingUsername.elementAt(x).equals(username)){
                try {
                    Socket rSock = getClientFileSharingSocket(username);
                    if(rSock != null){
                        rSock.close();
                    }
                    clientFileSharingUsername.removeElementAt(x); // loại bỏ file trong danh sách client
                    clientFileSharingSocket.removeElementAt(x); // loại bỏ file trong danh sách socket 
                    appendMessage("[FileSharing]: Đã được huỷ bỏ . "+ username);
                } catch (IOException e) {
                    appendMessage("[FileSharing]: "+ e.getMessage());
                    appendMessage("[FileSharing]: Không thể hủy bỏ file này. "+ username); // đẩy thông báo lên text
                }
                break;
            }
        }
    }
    // hàm chạy dòng chữ
public void chuchay() {
		new Thread() { // sử dụng hàm đa luồng để thực hiện chạy dòng chữ
			public void run() {
				while(true) {
					try {
						Thread.sleep(180); // tốc độ chạy của dòng chữ
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								String text = label_chaydongchu.getText() + "";
								// text = student manager
								text = text.substring(1) + text.charAt(0);
								// text substring (1) bắt đầu với kí tự 1 trở đi
								// text charAt lấy kí tự đầu tiên.
								label_chaydongchu.setText(text);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();// bắt đầu
	}

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        text_port = new javax.swing.JTextField();
        start = new javax.swing.JButton();
        stop = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        text_hienthithongbao = new javax.swing.JTextArea();
        label_chaydongchu = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Máy chủ RabbitChat");

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        jLabel2.setText("Port");
        jLabel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        text_port.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_port.setText("8888");
        text_port.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        text_port.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_portActionPerformed(evt);
            }
        });

        start.setFont(new java.awt.Font("Segoe UI Semibold", 1, 22)); // NOI18N
        start.setText("Start Server");
        start.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        start.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startActionPerformed(evt);
            }
        });

        stop.setFont(new java.awt.Font("Segoe UI Semibold", 1, 22)); // NOI18N
        stop.setText("Stop Server");
        stop.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        stop.setEnabled(false);
        stop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(207, 207, 207)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(64, 64, 64)
                .addComponent(text_port, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(52, 52, 52)
                .addComponent(start, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(55, 55, 55)
                .addComponent(stop, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(283, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(text_port, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(start, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(stop, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewServer/server.PNG"))); // NOI18N
        jLabel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        text_hienthithongbao.setColumns(20);
        text_hienthithongbao.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_hienthithongbao.setRows(5);
        text_hienthithongbao.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jScrollPane1.setViewportView(text_hienthithongbao);

        label_chaydongchu.setFont(new java.awt.Font("Segoe UI Semibold", 1, 24)); // NOI18N
        label_chaydongchu.setForeground(new java.awt.Color(0, 204, 255));
        label_chaydongchu.setText("Chào Mừng Bạn Đến Với Giao Diện Chương Trình Của Server.  ");

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logoserver.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 683, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(label_chaydongchu)
                .addGap(268, 268, 268))
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addComponent(label_chaydongchu)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 686, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void startActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startActionPerformed
    
        int port = Integer.parseInt(text_port.getText());
        serverHandle = new ServerHandle(port, this);
        // chạy luồng serverhandle để nó thực thi các câu lệnh trong class serverhandle
        t = new Thread(serverHandle);
        t.start(); // khởi động luồng ........

        // chạy luồng của danh sách online phần user để khi chúng ta bấm start để nó hiển thị lên server
        new Thread(new OnlineListThread(this)).start();
        start.setEnabled(false);
        stop.setEnabled(true);
    }//GEN-LAST:event_startActionPerformed

    private void stopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopActionPerformed

        int confirm = JOptionPane.showConfirmDialog(null, "Đã đóng máy chủ !");
        if(confirm == 0){
            serverHandle.stop();
        }
    }//GEN-LAST:event_stopActionPerformed

    private void text_portActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_portActionPerformed
     
    }//GEN-LAST:event_text_portActionPerformed

  
    public static void main(String args[]) {
     
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Server().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel label_chaydongchu;
    private javax.swing.JButton start;
    private javax.swing.JButton stop;
    private javax.swing.JTextArea text_hienthithongbao;
    private javax.swing.JTextField text_port;
    // End of variables declaration//GEN-END:variables

   
}
