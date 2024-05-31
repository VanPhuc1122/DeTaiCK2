package ViewClient;
import static ViewClient.Client.main;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class Client extends javax.swing.JFrame {
    String username;
    String host;
    int port;
    Socket socket;
    DataOutputStream dos;
    Client client;
    public boolean attachmentOpen = false;
    private boolean isConnected = false;
    private String mydownloadfolder = "D:\\";
    ClientThread clientThread = new ClientThread();
    private JPanel messagePanel;
    private JTextPane textPane;
    private static final String SERVER_IP = "127.0.0.2";
    private static final int SERVER_PORT = 8888;
     private static final String FILENAME = "messages.txt";
    public Client() {
        initComponents();
        MyInit();
        messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        configureButton(button_send);
        configureButton(button_anh);
        configureButton(button_buon);
        configureButton(button_exit);
        configureButton(button_guifile);
        configureButton(button_kâka);
        configureButton(button_like);
        configureButton(button_yeu);
        configureButton(button_dethuong);
        configureButton(button_ngacnhien);
        configureButton(button_battay);
        configureButton(button_alo);
        configureButton(button_bonghoa);
        configureButton(button_load);
    }
        public void initFrame(String username, String host, int port){
        this.username = username;
        this.host = host;
        this.port = port;
        setTitle("You are logged in as: " + username);
        
        //Kết nối 
        connect();
    }
     void MyInit(){
         chuchay();
         setLocationRelativeTo(null);
         setResizable(false);
         setIconImage(new ImageIcon(getClass().getResource("/images/logosend.png")).getImage());
     }
     // hàm này dùng để xoá viền ngoài button
     private void configureButton(JButton button) {
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
    }

    public void connect(){
        appendMessage(" Connecting...", "Status", Color.RED, Color.RED);
        try {
            socket = new Socket(host, port);
            dos = new DataOutputStream(socket.getOutputStream());
            // gửi username đang kết nối
            dos.writeUTF("CMD_JOIN "+ username); // ghi dữ liệu lên server
            appendMessage(" Connected", "Status", Color.RED, Color.RED);
            appendMessage(" Please send a message now!", "Status", Color.RED, Color.RED);
            
            // Khởi động Client Thread 
           new Thread(new ViewClient.ClientThread(socket, this)).start();
            button_send.setEnabled(true);
            // đã được kết nối
            isConnected = true;
            
        }
        catch(IOException e) {
            isConnected = false;
            JOptionPane.showMessageDialog(this, "Unable to connect to the server, please try again later!","Connec Fail",JOptionPane.ERROR_MESSAGE);
            appendMessage("[IOException]: "+ e.getMessage(), "Lỗi", Color.RED, Color.RED);
        }
    }
    
    // hàm kết nối đến SV
    public boolean isConnected(){
        return this.isConnected;
    }
    
    // hàm này dùng để đẩy dữ liệu lên text
    public void appendMessage(String msg, String header, Color headerColor, Color contentColor){
        Text_hienthitinnhan.setEditable(true);
        getMsgHeader(header, headerColor);
        getMsgContent(msg, contentColor);
        Text_hienthitinnhan.setEditable(false);
    }
    
    public void appendMessageIcon(String message, String sender, Color textColor, Color senderColor) {
        try {
            StyleContext sc = StyleContext.getDefaultStyleContext();
            AttributeSet aset = sc.addAttribute(SimpleAttributeSet.EMPTY, StyleConstants.Foreground, textColor);

            int len = Text_hienthitinnhan.getDocument().getLength();
            Text_hienthitinnhan.setCaretPosition(len);
            Text_hienthitinnhan.setCharacterAttributes(aset, false);
            Text_hienthitinnhan.replaceSelection("\n" + sender + ": "); // Hiển thị tên người gửi

            // Tạo một biểu tượng từ đường dẫn và chèn nó vào văn bản
            String imagePath = message.trim();
            InputStream stream = getClass().getResourceAsStream(imagePath);
            if (stream != null) {
                BufferedImage image = ImageIO.read(stream);
                Icon icon = new ImageIcon(image);
                Text_hienthitinnhan.setCaretPosition(Text_hienthitinnhan.getDocument().getLength());
                Text_hienthitinnhan.insertIcon(icon); // Chèn biểu tượng vào văn bản
            } else {
                System.err.println("Không thể tìm thấy hình ảnh: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // hàm này lấy tin nhắn chat của clinet
    public void appendMyMessage(String msg, String header){
        Text_hienthitinnhan.setEditable(true);
        getMsgHeader(header, Color.BLUE);
        getMsgContent(msg, Color.BLACK);
        Text_hienthitinnhan.setEditable(false);
    }


    public void getMsgHeader(String header, Color color){
        int len = Text_hienthitinnhan.getDocument().getLength(); // lấy ra độ dài cảu đoạn văn bản
        Text_hienthitinnhan.setCaretPosition(len); // dùng để trỏ tới cuối ohanaf văn bản
        Text_hienthitinnhan.replaceSelection(header+":"); // replace thay thế văn bản hiện tại
    }
  
    // lấy ra phần nội dung của tin nhắn
    public void getMsgContent(String msg, Color color){
        int len = Text_hienthitinnhan.getDocument().getLength();
        Text_hienthitinnhan.setCaretPosition(len);
        Text_hienthitinnhan.replaceSelection(msg +"\n\n");
    }
    
    // tạo ra dòng của user online
    public void appendOnlineList(Vector list){
        sampleOnlineList(list); 
    }
    

        // Hiển thị danh sách đang online
    public void showOnLineList(Vector list){
        try {
            Text_user_online.setEditable(true);
            // trong phần này tạo ra phần table của HTML định dạng HTML để tạo ra danh sách USer online theo thứ tự
            Text_user_online.setContentType("text/html");
            StringBuilder sb = new StringBuilder(); // xây dựng 
            Iterator it = list.iterator(); // duyệt qua các user 
            sb.append("<html><table>");
            while(it.hasNext()){ // duyệt vào các thành phần con
                Object e = it.next(); // tạo đối tượng cho mỗi user
                URL url = getImageFile();
                Icon icon = new ImageIcon(this.getClass().getResource("/images/dot.png"));
                sb.append("<tr><td><b>></b></td><td>").append(e).append("</td></tr>");
                System.out.println("Online: "+ e);
            }
            sb.append("</table></body></html>");
            Text_user_online.removeAll();
            Text_user_online.setText(sb.toString());
            Text_user_online.setEditable(false);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    private void sampleOnlineList(Vector list){
        Text_user_online.setEditable(true);
        Text_user_online.removeAll();
        Text_user_online.setText("");
        Iterator i = list.iterator();
        while(i.hasNext()){
            Object e = i.next();
            JPanel panel = new JPanel();
            panel.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel.setBackground(Color.white);
           
            Icon icon = new ImageIcon(this.getClass().getResource("/images/dot.png"));
            JLabel label = new JLabel(icon);
            label.setText("  "+ e);
            panel.add(label);
            int len = Text_user_online.getDocument().getLength();
            Text_user_online.setCaretPosition(len);
            Text_user_online.insertComponent(panel);
            sampleAppend();
        }
        Text_user_online.setEditable(false);
    }
    private void sampleAppend(){
        int len = Text_user_online.getDocument().getLength();
        Text_user_online.setCaretPosition(len);
        Text_user_online.replaceSelection("\n");
    }
 
// lấy đường dẫn ảnh 
    public URL getImageFile(){
        URL url = this.getClass().getResource("/images/dot.png");
        return url;
    }
    
   
    public void setMyTitle(String s){
        setTitle(s);
    }
    
    public String getMyDownloadFolder(){
        return this.mydownloadfolder;
    }
    
    public String getMyHost(){
        return this.host;
    }
    
   
    public int getMyPort(){
        return this.port;
    }
    

    public String getMyUsername(){
        return this.username;
    }
    
    public void updateAttachment(boolean b){
        this.attachmentOpen = b;
    }

    // dùng mở thư mục 
    public void openFolder(){
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int open = chooser.showDialog(this, "Mở thư mục thành công !");
        if(open == chooser.APPROVE_OPTION){
            mydownloadfolder = chooser.getSelectedFile().toString()+"\\";
        } else {
            mydownloadfolder = "D:\\";
        }
    }
// hàm này dùng để hiển thị tin nhắn biểu tượng của icon
  public void append(String send, String iconName) {
    SwingUtilities.invokeLater(() -> { // phần lamda này truyền tham số và xử lí nhanh hơn
        try {
            String iconPath = "/images/" + iconName; // Lấy đường dẫn của icon
            Icon icon = new ImageIcon(getClass().getResource(iconPath));
            // Lấy tài liệu từ text gửi icon để định dạng văn bản
            
            StyledDocument doc = Text_hienthitinnhan.getStyledDocument();
            // Thêm tên người gửi trước biểu tượng
            doc.insertString(doc.getLength(), send + ": ", null);

            // Thêm biểu tượng vào văn bản
            Text_hienthitinnhan.setCaretPosition(doc.getLength()); 
            Text_hienthitinnhan.insertIcon(icon); // Thêm biểu tượng icon vào text tin nhắn

            // Thêm dòng mới sau biểu tượng đã gửi
            doc.insertString(doc.getLength(), "\n", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
}
  // hàm này dùng để gửi hình ảnh
    private void sendImage(String command, File file){
        try(Socket socket = new Socket(SERVER_IP, SERVER_PORT)) {  // địa chỉ ip và port muốn gửi ảnh và đã set sẵn phía trên
            // gửi dữ liệu ảnh qua mạng output stream
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            FileInputStream inputStream = new FileInputStream(file);
              // gửi lệnh và tên người gửi lên server
            dataOutputStream.writeUTF(command);
            // gửi độ dài tên file
            dataOutputStream.writeUTF(file.getName());
            
            byte[] buffer = new byte[8192];
            int byteRead;
            // tiến hành đọc và gửii dữ liệu hình ảnh
             while ((byteRead = inputStream.read(buffer)) != -1) {
                  dataOutputStream.write(buffer, 0, byteRead);
                }
             dataOutputStream.flush(); // đẩy dữ liệu ảnh lên
             inputStream.close(); // đóng tài nguyên inputfile lại
             System.out.println("Image sent Succesfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
  }
  // hàm này dùng để hiển thị ảnh khi gửi
  public void AppendImage(String nguoigui, File image){
      try {
           BufferedImage img = ImageIO.read(image); // đọc dữ liệu ảnh từ file
           ImageIcon icon = new ImageIcon(img);
           JLabel imageLabel = new JLabel(icon);
           
           // hiển thị dạng text tin nhắn
           StyledDocument style = Text_hienthitinnhan.getStyledDocument();
           SimpleAttributeSet center = new SimpleAttributeSet();
           try {
              // thêm tên người gửi ảnh
              style.insertString(style.getLength(), nguoigui + " đã gửi một ảnh: \n", null);
              // đặt hình ảnh vào
              Text_hienthitinnhan.setCaretPosition(style.getLength());
              Text_hienthitinnhan.insertComponent(imageLabel);
              
              // thêm dòng mới trắng sau khi gửi ảnh
              style.insertString(style.getLength(), "\n", null);
          } catch (BadLocationException e) {
              e.printStackTrace();
          }
      } catch (Exception e) {
          appendMessage("Error display image", "Error", Color.yellow, Color.yellow);
      }
  }
  // hàm này dùng để điều chỉnh kích thước của hình ảnh
   public byte[] resizeImage(File originalFile, int width, int height) throws IOException {
    BufferedImage originalImage = ImageIO.read(originalFile); // đọc ảnh từ tệp
    // lấy ra ảnh đã được thay đổi kích thước chiều rộng và chiều cao
    Image resizedImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
    
    BufferedImage bufferedResizedImage = new BufferedImage(width, height, originalImage.getType());
    Graphics2D g2d = bufferedResizedImage.createGraphics();
    g2d.drawImage(resizedImage, 0, 0, null);
    g2d.dispose();
    
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ImageIO.write(bufferedResizedImage, "jpg, png", byteArrayOutputStream); 
    
    return byteArrayOutputStream.toByteArray();
}
            // hàm này dùng để lưu trữ tin nhắn
    private void sendMEss(){
        String message = text_guitinnhan.getText();
        if(!message.isEmpty()){
            try {
                String fullMess = "CMD_CHATALL " + "You" + message;
                clientThread.dos.writeUTF(fullMess);
                appMess("You", message);
                text_guitinnhan.setText("");
                saveMess(fullMess);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    // Phương thức hiển thị tin nhắn văn bản
    private void appMess(String sender, String message) {
        if (sender != null && message != null) {
            String formattedMessage = String.format("%s: %s\n", sender, message);
            appendTextTinNhan(formattedMessage);
        }
    }
   // lưu
private void saveMess(String message){
    try(BufferedWriter write = new BufferedWriter(new FileWriter(FILENAME, true))) {
        write.write(message);
        write.newLine();
    } catch (Exception e) {
        e.printStackTrace();
    }
}

// Phương thức load dữ liệu tin nhắn từ file
private void loadPreMess() {
    List<String> messages = loadMessage(); // Gọi hàm loadMessage để lấy tin nhắn từ file
    for (String msg : messages) {
        // Sử dụng AppendForm để hiển thị từng tin nhắn
        AppendForm(msg);
    }
}
// Phương thức load tin nhắn từ file
    private List<String> loadMessage() {
        List<String> message = new ArrayList<>();
        File file = new File(FILENAME);
        // Tạo tệp nếu không tồn tại
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return message;
            }
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(FILENAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                message.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }

    // Phương thức này hiển thị từng tin nhắn
    private void AppendForm(String text) {
        String[] part = text.split(" ", 3);
        if (part.length >= 3) {
            String type = part[0];
            String sender = part[1];
            String content = part[2];
            if (type.equals("CMD_CHATALL")) {
                appMess(sender, content);
            } else if (type.equals("CMD_SEND_ICON")) {
                appMess(sender, "[Icon: " + content + "]");
            } else if (type.equals("CMD_SEND_IMAGE")) {
                byte[] imageBytes = getImageText(content);
                ImageIcon imageIcon = new ImageIcon(imageBytes);
                JLabel imageLabel = new JLabel(sender + ": ");
                imageLabel.setIcon(imageIcon);
                appendComponentToChat(imageLabel);
            }
        }
    }

    // Phương thức xử lý để lấy mảng byte từ tệp hình ảnh file text
    private byte[] getImageText(String imageName) {
        try {
            File imageFile = new File(imageName);
            return Files.readAllBytes(imageFile.toPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
// Phương thức này thêm tin nhắn từ file text vào giao diện
    private void appendTextTinNhan(String message) {
        StyledDocument styleDoc = Text_hienthitinnhan.getStyledDocument();
        SimpleAttributeSet key = new SimpleAttributeSet();
        StyleConstants.setForeground(key, Color.BLACK); // Thiết lập màu chữ
        try {
            styleDoc.insertString(styleDoc.getLength(), message + "\n", key);
            // Tự động cuộn xuống dòng cuối cùng
            Text_hienthitinnhan.setCaretPosition(styleDoc.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
// Phương thức này thêm thành phần giao diện (hình ảnh hoặc biểu tượng) vào giao diện chat
private void appendComponentToChat(Component component) {
    if (component != null) {
        Text_hienthitinnhan.insertComponent(component); // Thêm hình ảnh hoặc biểu tượng vào TextPane
        Text_hienthitinnhan.revalidate(); // Revalidate để cập nhật giao diện
        Text_hienthitinnhan.repaint(); // Repaint để vẽ lại giao diện
    }
}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jButton2 = new javax.swing.JButton();
        jMenuBar2 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuBar3 = new javax.swing.JMenuBar();
        jMenu3 = new javax.swing.JMenu();
        jMenu4 = new javax.swing.JMenu();
        jMenu5 = new javax.swing.JMenu();
        jMenu6 = new javax.swing.JMenu();
        jLabel3 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        Text_hienthitinnhan = new javax.swing.JTextPane();
        text_guitinnhan = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        Text_user_online = new javax.swing.JTextPane();
        button_send = new javax.swing.JButton();
        label_danhsachusser = new javax.swing.JLabel();
        label_interface = new javax.swing.JLabel();
        label_account = new javax.swing.JLabel();
        button_guifile = new javax.swing.JButton();
        button_anh = new javax.swing.JButton();
        button_yeu = new javax.swing.JButton();
        button_dethuong = new javax.swing.JButton();
        button_ngacnhien = new javax.swing.JButton();
        button_kâka = new javax.swing.JButton();
        button_buon = new javax.swing.JButton();
        button_like = new javax.swing.JButton();
        button_battay = new javax.swing.JButton();
        button_alo = new javax.swing.JButton();
        button_bonghoa = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        button_exit = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        label_admin = new javax.swing.JLabel();
        lb_admin = new javax.swing.JLabel();
        button_load = new javax.swing.JButton();
        gifload = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();

        jButton2.setText("jButton2");

        jMenu1.setText("File");
        jMenuBar2.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar2.add(jMenu2);

        jMenu3.setText("File");
        jMenuBar3.add(jMenu3);

        jMenu4.setText("Edit");
        jMenuBar3.add(jMenu4);

        jMenu5.setText("jMenu5");

        jMenu6.setText("jMenu6");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        Text_hienthitinnhan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Text_hienthitinnhan.setFont(new java.awt.Font("Yu Gothic UI Semibold", 1, 18)); // NOI18N
        jScrollPane1.setViewportView(Text_hienthitinnhan);

        text_guitinnhan.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        text_guitinnhan.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        text_guitinnhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                text_guitinnhanActionPerformed(evt);
            }
        });

        Text_user_online.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        Text_user_online.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        Text_user_online.setForeground(new java.awt.Color(120, 14, 3));
        Text_user_online.setAutoscrolls(false);
        Text_user_online.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane3.setViewportView(Text_user_online);

        button_send.setBackground(new java.awt.Color(0, 153, 204));
        button_send.setForeground(new java.awt.Color(51, 153, 255));
        button_send.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-send-34.png"))); // NOI18N
        button_send.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_send.setEnabled(false);
        button_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_sendActionPerformed(evt);
            }
        });

        label_danhsachusser.setFont(new java.awt.Font("Segoe UI Historic", 1, 18)); // NOI18N
        label_danhsachusser.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-online-35 (1).png"))); // NOI18N
        label_danhsachusser.setText("Danh Sách User Online");

        label_interface.setFont(new java.awt.Font("Segoe UI Semibold", 0, 18)); // NOI18N
        label_interface.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-interface-35.png"))); // NOI18N
        label_interface.setText("Chat User Interface");

        label_account.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        label_account.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-account-35.png"))); // NOI18N
        label_account.setText("Account Infomation");

        button_guifile.setBackground(new java.awt.Color(51, 204, 255));
        button_guifile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-send-folder-34.png"))); // NOI18N
        button_guifile.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_guifile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_guifileActionPerformed(evt);
            }
        });

        button_anh.setBackground(new java.awt.Color(51, 204, 255));
        button_anh.setForeground(new java.awt.Color(255, 255, 255));
        button_anh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/icons8-image-35.png"))); // NOI18N
        button_anh.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_anh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_anhActionPerformed(evt);
            }
        });

        button_yeu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/yeu.png"))); // NOI18N
        button_yeu.setBorder(null);
        button_yeu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_yeuActionPerformed(evt);
            }
        });

        button_dethuong.setBackground(new java.awt.Color(51, 204, 255));
        button_dethuong.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/dethuong.png"))); // NOI18N
        button_dethuong.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_dethuong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_dethuongActionPerformed(evt);
            }
        });

        button_ngacnhien.setBackground(new java.awt.Color(51, 204, 255));
        button_ngacnhien.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ngacnhien.png"))); // NOI18N
        button_ngacnhien.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_ngacnhien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_ngacnhienActionPerformed(evt);
            }
        });

        button_kâka.setBackground(new java.awt.Color(51, 204, 255));
        button_kâka.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/kâka.png"))); // NOI18N
        button_kâka.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_kâka.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_kâkaActionPerformed(evt);
            }
        });

        button_buon.setBackground(new java.awt.Color(102, 204, 255));
        button_buon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/buon.png"))); // NOI18N
        button_buon.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_buon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_buonActionPerformed(evt);
            }
        });

        button_like.setBackground(new java.awt.Color(0, 204, 204));
        button_like.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/thich.png"))); // NOI18N
        button_like.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_like.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_likeActionPerformed(evt);
            }
        });

        button_battay.setBackground(new java.awt.Color(0, 204, 204));
        button_battay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/battay.png"))); // NOI18N
        button_battay.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_battay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_battayActionPerformed(evt);
            }
        });

        button_alo.setBackground(new java.awt.Color(0, 204, 204));
        button_alo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/alo.png"))); // NOI18N
        button_alo.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_alo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_aloActionPerformed(evt);
            }
        });

        button_bonghoa.setBackground(new java.awt.Color(0, 204, 204));
        button_bonghoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bonghoa.png"))); // NOI18N
        button_bonghoa.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_bonghoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_bonghoaActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N

        button_exit.setBackground(new java.awt.Color(0, 153, 255));
        button_exit.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        button_exit.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/log out.png"))); // NOI18N
        button_exit.setText("Exit Account");
        button_exit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        button_exit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_exitActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        label_admin.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ViewClient/amen.png"))); // NOI18N

        lb_admin.setBackground(new java.awt.Color(0, 0, 0));
        lb_admin.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        lb_admin.setForeground(new java.awt.Color(0, 204, 204));
        lb_admin.setText(" Welcome Admin. ");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(label_admin)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lb_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(243, 243, 243)
                        .addComponent(label_admin))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(260, 260, 260)
                        .addComponent(lb_admin, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        button_load.setFont(new java.awt.Font("Segoe UI Semibold", 1, 18)); // NOI18N
        button_load.setText("Load Message");
        button_load.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                button_loadActionPerformed(evt);
            }
        });

        gifload.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/load.gif"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jLabel2))
                    .addComponent(label_danhsachusser, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(304, 304, 304)
                .addComponent(label_interface)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(label_account)
                .addGap(301, 301, 301))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(gifload)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(13, 13, 13)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 803, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(button_yeu)
                                .addGap(46, 46, 46)
                                .addComponent(button_dethuong, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(button_ngacnhien, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(button_kâka, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(32, 32, 32)
                                .addComponent(button_buon, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(26, 26, 26)
                                .addComponent(button_like, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(31, 31, 31)
                                .addComponent(button_battay, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(33, 33, 33)
                                .addComponent(button_alo, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(button_bonghoa, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(text_guitinnhan, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_anh, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_guifile, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(button_send, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(button_exit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(277, 277, 277))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(label_interface, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(label_danhsachusser, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(label_account, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane3))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                            .addGap(10, 10, 10)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(button_ngacnhien, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(button_kâka)))
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(button_yeu, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_dethuong, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(10, 10, 10)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(button_buon, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_like, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_battay, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_alo, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(button_bonghoa, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(button_anh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(text_guitinnhan)
                                    .addComponent(button_guifile, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(button_send, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(button_load, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(gifload)))))
                    .addComponent(button_exit))
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1420, 1420, 1420)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 1374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void button_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_sendActionPerformed
                      
        try {
            String content = username+" "+ text_guitinnhan.getText();
            dos.writeUTF("CMD_CHATALL "+ content); // ghi dữ liệu lên Server tiếp nhận
            appendMyMessage(" "+text_guitinnhan.getText(), username);
            text_guitinnhan.setText("");
        } catch (IOException e) {
            appendMessage(" Bạn không thể gửi tin nhắn bây giờ. Bạn đã gặp sự cố kết nối đến Serrver", "Lỗi", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_button_sendActionPerformed

    private void text_guitinnhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_text_guitinnhanActionPerformed
       
        try {
            String content = username+" "+ evt.getActionCommand();
            dos.writeUTF("CMD_CHATALL "+ content);
            appendMyMessage(" "+evt.getActionCommand(), username);
            text_guitinnhan.setText("");
        } catch (IOException e) {
            appendMessage(" Không thể gửi tin nhắn đi bây giờ, không thể kết nối đến Máy Chủ tại thời điểm này, xin vui lòng thử lại sau hoặc khởi động lại ứng dụng này.!", "Lỗi", Color.RED, Color.RED);
        }
    }//GEN-LAST:event_text_guitinnhanActionPerformed

    private void button_guifileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_guifileActionPerformed
              
        try {
            if (!attachmentOpen) {
                SendFile s = new SendFile();
                if (s.prepare(username, host, port, this)) {
                    s.setLocationRelativeTo(null);
                    s.setVisible(true);
                    attachmentOpen = true;
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể thiết lập Chia sẻ File tại thời điểm này, xin vui lòng thử lại sau.!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_guifileActionPerformed
 // hàm chạy dòng chữ
public void chuchay() {
		new Thread() { // sử dụng hàm đa luồng để thực hiện chạy dòng chữ
			public void run() {
				while(true) {
					try {
						Thread.sleep(180); // tốc độ chạy của dòng chữ
						SwingUtilities.invokeAndWait(new Runnable() {
							public void run() {
								String text = lb_admin.getText() + "";
								// text = student manager
								text = text.substring(1) + text.charAt(0);
								// text substring (1) bắt đầu với kí tự 1 trở đi
								// text charAt lấy kí tự đầu tiên.
								 lb_admin.setText(text);
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}.start();// bắt đầu
	}

    
    private void button_anhActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_anhActionPerformed
        try {
            JFileChooser jFileChooser = new JFileChooser();
            // Chỉ cho phép chọn các định dạng ảnh nhất định không cho phép gửi mấy cái khác
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Image Files", "jpg", "png", "gif");
            jFileChooser.setFileFilter(filter);
            int open = jFileChooser.showDialog(null, "Send Image");
            if (open == JFileChooser.APPROVE_OPTION) {
                File selectFile = jFileChooser.getSelectedFile(); // chọn file
                try {
                    // Gửi lệnh và ảnh đến server
                    sendImage("CMD_SEND_IMAGE " + username, selectFile);
                    // Hiển thị ảnh lên text tin nhắn
                    AppendImage(username, selectFile);
                } catch (Exception e) {
                    appendMessage("Error sending image.", "Error", Color.RED, Color.RED);
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
    }//GEN-LAST:event_button_anhActionPerformed
    }
    private void button_yeuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_yeuActionPerformed

        try {
            String iconName = "yeu.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_yeuActionPerformed

    private void button_dethuongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_dethuongActionPerformed
       
       try {
            String iconName = "dethuong.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
         
    }//GEN-LAST:event_button_dethuongActionPerformed

    private void button_ngacnhienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_ngacnhienActionPerformed
      
        try {
            String iconName = "ngacnhien.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_ngacnhienActionPerformed

    private void button_kâkaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_kâkaActionPerformed
        
        try {
            String iconName = "kâka.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_kâkaActionPerformed

    private void button_buonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_buonActionPerformed

        try {
            String iconName = "buon.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_buonActionPerformed

    private void button_likeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_likeActionPerformed
       
        try {
            String iconName = "thich.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_likeActionPerformed

    private void button_battayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_battayActionPerformed
       try {
            String iconName = "battay.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_battayActionPerformed

    private void button_aloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_aloActionPerformed
       try {
            String iconName = "alo.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_aloActionPerformed

    private void button_bonghoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_bonghoaActionPerformed
      try {
            String iconName = "bonghoa.png"; // tên biểu tượng
            // gửi lệnh lên server
            dos.writeUTF("CMD_SEND_ICON " + username + " " + iconName);
            // hiển thị biểu tượng
           append(username, iconName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_button_bonghoaActionPerformed

    private void button_exitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_exitActionPerformed
        int thongbao = JOptionPane.showConfirmDialog(null, "Do you want to log out?");
        if(thongbao == 0){
            try {
                socket.close();
                setVisible(false);
                new Login_form().setVisible(true);
            } catch (Exception e) {
                System.out.println(e.getMessage()); // hiển thị lỗi nếu có
            }
        }
    }//GEN-LAST:event_button_exitActionPerformed

    private void button_loadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_button_loadActionPerformed
          loadPreMess();
    }//GEN-LAST:event_button_loadActionPerformed

   
    public static void main(String args[]) {
       
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Client().setVisible(true);
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextPane Text_hienthitinnhan;
    private javax.swing.JTextPane Text_user_online;
    private javax.swing.JButton button_alo;
    private javax.swing.JButton button_anh;
    private javax.swing.JButton button_battay;
    private javax.swing.JButton button_bonghoa;
    private javax.swing.JButton button_buon;
    private javax.swing.JButton button_dethuong;
    private javax.swing.JButton button_exit;
    private javax.swing.JButton button_guifile;
    private javax.swing.JButton button_kâka;
    private javax.swing.JButton button_like;
    private javax.swing.JButton button_load;
    private javax.swing.JButton button_ngacnhien;
    private javax.swing.JButton button_send;
    private javax.swing.JButton button_yeu;
    private javax.swing.JLabel gifload;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenu jMenu5;
    private javax.swing.JMenu jMenu6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuBar jMenuBar2;
    private javax.swing.JMenuBar jMenuBar3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel label_account;
    private javax.swing.JLabel label_admin;
    private javax.swing.JLabel label_danhsachusser;
    private javax.swing.JLabel label_interface;
    private javax.swing.JLabel lb_admin;
    private javax.swing.JTextField text_guitinnhan;
    // End of variables declaration//GEN-END:variables
}
