package ViewClient;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.text.StyledDocument;
public class ClientThread implements Runnable{
    Socket socket;
    DataInputStream dis;
    DataOutputStream dos;
    Client main;
    StringTokenizer st;
    protected DecimalFormat df = new DecimalFormat("##,#00");
    private static final String MESSAGE_FILE = "messages.txt";
    public ClientThread() {
    }
    public ClientThread(Socket socket, Client main){
        this.main = main;
        this.socket = socket;
        try {
            dis = new DataInputStream(socket.getInputStream()); // ghi dữ liệu đầu vào đã kết nối đến với Socket
            dos = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            main.appendMessage("[IOException]: "+ e.getMessage(), "Lỗi", Color.RED, Color.RED);
        }
    }
    
    @Override
    public void run() {
        try { 
            while(!Thread.currentThread().isInterrupted()){
                String data = dis.readUTF(); // đọc dữ liệu từ Server
                st = new StringTokenizer(data);
            
                String CMD = st.nextToken();
                // tạo ra các Switch case khi tiến hành chạy luồng gặp case sẽ thực thi case đó
                switch(CMD){
                    case "CMD_MESSAGE":
                        SoundEffect.MessageReceive.play();  // biến này âm thanh khi sử dụng chat tin nhắn sẽ báo ting
                        String msg = "";
                        String frm = st.nextToken();
                        while(st.hasMoreTokens()){
                            msg = msg +" "+ st.nextToken();
                        }
                        main.appendMessage(msg, frm, Color.BLUE, Color.BLUE);
                        savemess("CMD_MESSAGE", frm, msg, null);
                        break;
                        // casse này hiển thị danh sách online của client
                    case "CMD_ONLINE":
                        Vector online = new Vector();
                        while(st.hasMoreTokens()){ 
                            String list = st.nextToken();
                            if(!list.equalsIgnoreCase(main.username)){ // so sánh giữa đối tượng trong danh sách với username đã đăng nhập
                                online.add(list); // nếu khác với username trong ds add nó vào
                            }
                        }
                        main.appendOnlineList(online); // xuống dòng cho mỗi user
                        break;
                     // case này khi chúng ta thựuc thi nó hiển thị thông tin gửi File của client kia
                    case "CMD_FILE_XD":  
                        String sender = st.nextToken(); // lấy tên username gửi file
                        String receiver = st.nextToken(); // lấy tên usernmae nhận file
                        String fname = st.nextToken(); // lấy tên file
                        int confirm = JOptionPane.showConfirmDialog(main, "From: "+sender+"\nFile Name: "+fname+"\nDo you accept files sent by the Client...?");
                        SoundEffect.FileSharing.play();  // khi tiến hành gửi file nó thông báo âm thanh
                        if(confirm == 0){ 
                            main.openFolder(); // nếu người nhận chấp nhận tiên shanfh mở thư mục để lưu file
                            try {
                                dos = new DataOutputStream(socket.getOutputStream()); 
                                String format = "CMD_SEND_FILE_ACCEPT "+sender+" Chấp nhận File Này .";
                                dos.writeUTF(format); // ghi lệnh lên server          
                              
                                Socket fSoc = new Socket(main.getMyHost(), main.getMyPort()); // lấy ra đc host, post muốn gửi đến
                                DataOutputStream fdos = new DataOutputStream(fSoc.getOutputStream()); // gửi dữ liệu thông qua outputstream
                                fdos.writeUTF("CMD_SHARINGSOCKET "+ main.getMyUsername()); // gửi lệnh lên server
                                // chạy luồng giao nhận file để tiến hành xử lí 
                                new Thread(new ReceivingFileThread(fSoc, main)).start();
                                savemess("CMD_FILE_XD", sender, fname, null);
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        } else { 
                            try {
                                dos = new DataOutputStream(socket.getOutputStream());
                                String format = "CMD_SEND_FILE_ERROR "+sender+" The user has declined your request or lost connection.!";
                                dos.writeUTF(format); // gửi lệnh lên server
                            } catch (IOException e) {
                                System.out.println("[CMD_FILE_XD]: "+e.getMessage());
                            }
                        }                       
                        break;   
                       case "CMD_RECEIVING":
                            SoundEffect.MessageReceive.play();
                            String content = " ";
                            String fm = st.nextToken();
                           while(st.hasMoreTokens()){
                               content = content + " " + st.nextToken();
                               savemess("CMD_RECEIVING",fm, content, null);
                           }
                           main.appendMessage(content, fm, Color.yellow, Color.yellow);
                        break;
                        // case này tiếp nhận dữ liệu icon từ server gửi lên
                    case "SEND_MESSAGE_ICON":
                        SoundEffect.MessageReceive.play();
                        try {
                            // kiểm tra số token có đủ để thực thi
                            if (st.countTokens() < 2) {
                               main.appendMessage("[SEND_MESSAGE_ICON] Error: Missing parameters.", "Lỗi", Color.yellow, Color.yellow);
                                break;
                            }
                            String send = st.nextToken(); // lấy tên người gửi được nhận từ server
                            String iconName = st.nextToken(); // lấy biểu tượng icon nhận từ server
                            // Gọi hàm append để hiển thị biểu tượng icon trong giao diện của client
                            main.append(send, iconName);
                            savemess("SEND_MESSAGE_ICON", send, iconName, null);
                        } catch (NoSuchElementException e) {
                            // hiện thông báo lỗi
                           main.appendMessage("[SEND_MESSAGE_ICON] Error: Missing parameters.", "Error", Color.yellow, Color.yellow);
                        } catch (Exception e) {
                            main.appendMessage("[SEND_MESSAGE_ICON] Error: " + e.getMessage(), "Đã lỗi", Color.yellow, Color.yellow);
                        }
                        break;
                    case "CMD_MESSAGE_IMAGE": 
                        try {
                            String nguoigui = st.nextToken(); // lấy tên người gửi ảnh từ server gửi lên
                            String image = st.nextToken(); // lấy ảnh ra từ server gửi lên
                            // nhận độ dài của mảng byte
                            int length = dis.readInt();
                            // nhận dữ liệu ảnh 
                            byte[] imageBytes = new byte[length];
                            dis.readFully(imageBytes); // đọc tất cả dữ liệu từ ảnh
                            // Tạo tệp ảnh tạm thời để lưu trữ ảnh nhận được
                            File imageFile = new File("received_" + image); // cái cụm từ received đó được định nghĩa để dex dàng nhận diện được hình ảnh từ server gửi đến ví dụ như có 1 ảnh photo.jpg thì cụm từ này có nghĩa received_photo.jpg
                            try (FileOutputStream outputFile = new FileOutputStream(imageFile)) {
                                // ghi dữ liệu hình ảnh mảng byte xuống ouputFile
                                outputFile.write(imageBytes);
                            }
                            main.appendMessage("[CMD_MESSAGE_IMAGE]: Received an image from " + nguoigui, "Received Image", Color.yellow, Color.yellow);
                          // phần này hiển thị ảnh lên text tin nhắn
                            main.AppendImage(nguoigui, imageFile);
                            savemess("CMD_MESSAGE_IMAGE", nguoigui, image, imageBytes);
                        } catch (Exception e) {
                            main.appendMessage("[CMD_MESSAGE_IMAGE]: Error while receiving image", "ERROR", Color.yellow, Color.yellow);
                        }
                        break;
                    default: 
                       main.appendMessage("[CMDException]: Unknown command " + CMD, "CMDException", Color.RED, Color.RED);
                        break;
                }
            }
        } catch(IOException e){
          main.appendMessage("Connection to the server lost. Please try again later.", "Error", Color.RED, Color.RED);
        }
    }
   // hàm này để lưu trữ tin nhắn
    private void savemess(String type, String sender, String content, byte[] imageBytes) {
        try (FileWriter filewrite = new FileWriter(MESSAGE_FILE, true); BufferedWriter buff = new BufferedWriter(filewrite); PrintWriter print = new PrintWriter(buff)) {
            if (imageBytes == null) {
                print.println(String.format("%s| %s| %s", type, sender, content));
            } else {
                print.println(String.format("%s| %s| %s| %s", type, sender, content, new String(imageBytes)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
