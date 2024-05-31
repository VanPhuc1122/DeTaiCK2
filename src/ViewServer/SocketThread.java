
package ViewServer;

import java.awt.Color;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.file.Files;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

public class SocketThread implements Runnable {
    Socket socket;
    Server main;
    DataInputStream dis;
    StringTokenizer st;
    String client, filesharing_username;
    private final int BUFFER_SIZE = 1024; // tạo biến toàn cục có kích thước 1024
    private static final String MESSAGE_FILE = "messages.txt"; // lưu trữ tin nhắn vào file text
    public SocketThread(Socket socket, Server main) {
        this.main = main;
        this.socket = socket;

        try {
            dis = new DataInputStream(socket.getInputStream()); // ghi dữ liệu luồng đầu vào kết nối socket
        } catch (IOException e) {
            main.appendMessage("[SocketThreadIOException]: " + e.getMessage());
        }
    }

    // hàm này tạo kết nối giữa 2 client để chia sẽ file với nhau
    private void createConnection(String receiver, String sender, String filename) {
        try {
            main.appendMessage("[createConnection]: Đang tiến hành tạo kết nối chia sẽ File.");
            Socket s = main.getClientList(receiver); // lây ra danh sách client 
            if (s != null) {  // có dữ liệu
                main.appendMessage("[createConnection]: Socket OK");
                DataOutputStream dosS = new DataOutputStream(s.getOutputStream());
                main.appendMessage("[createConnection]: DataOutputStream OK");
                
                String format = "CMD_FILE_XD " + sender + " " + receiver + " " + filename;
                dosS.writeUTF(format); // ghi nhận dữ liệu từ server gửi lệnh lên server
                main.appendMessage("[createConnection]: " + format);
            } else {
                main.appendMessage("[createConnection]: Client không được tìm thấy để chia sẽ '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                // gửi lệnh lên server thông báo về người gửi 
                dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + receiver + "' đã không được tìm tháy trong danh sách ."); 
            }
        } catch (IOException e) {
            main.appendMessage("[createConnection]: " + e.getLocalizedMessage());
        }
    }
// hàm này lưu trữ tin nhắn về phía server
    private void saveMessage(String type, String sender, String content, byte[] imageBytes){
        try(FileWriter file = new FileWriter(MESSAGE_FILE, true);
            BufferedWriter bw = new BufferedWriter(file); // đọc dữ liệu ghi từ file
            PrintWriter pr = new PrintWriter(bw)){ // hiển thị dữ liệu vừa đọc
            if(imageBytes == null){
               pr.println(String.format("%s| %s| %s", type, sender, content));
            }else{
                pr.println(String.format("%s| %s| %s| %s", type, sender, content, new String(imageBytes)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override // trong hàm run này chúng ta tạo ra các switch case để khi tiến hành chạy luồng nếu gặp trường hợp nào 
    // thì nó sẽ chạy vào cái case chúng ta đã đề xuất ra
    public void run() {
        try {
            while (true) {
                String data = dis.readUTF(); // đọc dữ liệu từ Server
                st = new StringTokenizer(data); // Stringtokenzier nó sẽ giúp phân tách các chuỗi riêng biệt
                String CMD = st.nextToken();
               
                switch (CMD) {
                     // case này dùng để khi client đăng nhậptham gia chat sẽ nhảy vào casse này hiện thông báo lên server
                    case "CMD_JOIN":
                        String clientUsername = st.nextToken(); // chuỗi tiếp theo đã phân tách ra
                        client = clientUsername;
                        main.setClientList(clientUsername); // đẩy username vào vector nó lưu thông tin
                        main.setSocketList(socket);
                        // hiển thị lên thông báo của server
                        main.appendMessage("[Client]: " + clientUsername + " đã tham gia chương trình Chat !");
                        break;
                        
                       // case này tiến hành chat
                    case "CMD_CHAT":
                        String from = st.nextToken(); // lấy tên username gửi đến
                        String sendTo = st.nextToken();
                        String msg = "";
                        while (st.hasMoreTokens()) { // chuyển tiếp vào các thành phần con đã phân tấch chuỗi
                            msg = msg + " " + st.nextToken();
                        }
                        Socket tsoc = main.getClientList(sendTo);
                        try {
                            DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                            String content = from + ": " + msg;
                            saveMessage("CMD_CHAT", from, content, null);
                            // gửi lệnh lên 
                            dos.writeUTF("CMD_MESSAGE " + content); // ghi dữ liệu nội dung lên Server
                            main.appendMessage("[Message]: Từ " + from + " Đến " + sendTo + " : " + msg);
                        } catch (IOException e) {
                            main.appendMessage("[IOException]: Không thể gửi tin nhắn đến " + sendTo);
                        }
                        break;

                        // case này dùng khi chúng ta chat nhóm luồng chạy tiên shanfh nhảy vào case này để thực thi
                    case "CMD_CHATALL":
                        String chatall_from = st.nextToken();
                        String chatall_msg = "";
                        while (st.hasMoreTokens()) {
                            chatall_msg = chatall_msg + " " + st.nextToken();
                        }
                        String chatall_content = chatall_from + " " + chatall_msg;
                        saveMessage("CMD_CHATALL", chatall_from, chatall_content, null);
                        for (int x = 0; x < main.clientList.size(); x++) {
                            if (!main.clientList.elementAt(x).equals(chatall_from)) {
                                try {
                                    Socket tsoc2 = (Socket) main.socketList.elementAt(x);
                                    DataOutputStream dos2 = new DataOutputStream(tsoc2.getOutputStream());
                                    dos2.writeUTF("CMD_MESSAGE " + chatall_content);
                                } catch (IOException e) {
                                    main.appendMessage("[CMD_CHATALL]: " + e.getMessage());
                                }
                            }
                        }
                        main.appendMessage("[CMD_CHATALL]: " + chatall_content);
                        break;
                        // case này dùng tiến hành chia sẻ file qua socket
                    case "CMD_SHARINGSOCKET":
                        main.appendMessage("CMD_SHARINGSOCKET : Client đang tiến hành thiết lập Socket để chấp nhận chia sẽ File........");
                        String file_sharing_username = st.nextToken();
                        filesharing_username = file_sharing_username;
                        main.setClientFileSharingUsername(file_sharing_username);
                        main.setClientFileSharingSocket(socket);
                        main.appendMessage("CMD_SHARINGSOCKET : Username: " + file_sharing_username);
                        main.appendMessage("CMD_SHARINGSOCKET : Chia Sẻ File đang được mở");
                        break;
                        // khi gửi file luồng chạy  và tiến hành nhảy vào case này để thực thi
                    case "CMD_SENDFILE":
                        main.appendMessage("CMD_SENDFILE : Client đang gửi một file...");
                        String file_name = st.nextToken(); // lấy tên file
                        String filesize = st.nextToken(); // lấy ra kích thước file
                        String sendto = st.nextToken(); // lấy tên người gửi
                        String consignee = st.nextToken(); // lấy tên người nhận
                        main.appendMessage("CMD_SENDFILE :  Gửi Từ: " + consignee);
                        main.appendMessage("CMD_SENDFILE : Đến: " + sendto);
                       
                        main.appendMessage("CMD_SENDFILE : Đã sẵn sàng cho các kết nối để gửi File........");
                        Socket cSock = main.getClientFileSharingSocket(sendto); 
                        if (cSock != null) { // có dữ liệu
                            try {
                                main.appendMessage("CMD_SENDFILE : Đã được kết nối thành công..!");
                                main.appendMessage("CMD_SENDFILE : Đang được tiến hành gửi File đến Client.......");
                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("CMD_SENDFILE " + file_name + " " + filesize + " " + consignee); // ghi dữu liệu lên server
                               
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[BUFFER_SIZE]; // tạo ra đối tượng lưu trữ tối đa
                                int cnt;
                                while ((cnt = input.read(buffer)) > 0) { // có dữu liệu bên trong
                                    sendFile.write(buffer, 0, cnt); // ghi dữ liệu file vào tệp
                                }
                                sendFile.flush(); // đẩy thẳng dữ liệu lên
                                sendFile.close(); // đóng lại
                                
                                main.removeClientFileSharing(sendto); // loại bỏ
                                main.removeClientFileSharing(consignee); // loại bỏ
                                main.appendMessage("CMD_SENDFILE : Đã tiến hành gửi File đến phía Client thành công !.....");
                                saveMessage("CMD_SEND_FILE", sendto, file_name, null);
                            } catch (IOException e) {
                                main.appendMessage("[CMD_SENDFILE]: " + e.getMessage());
                            }
                        } else { 
                            main.removeClientFileSharing(consignee);
                            main.appendMessage("CMD_SENDFILE : Client '" + sendto + "' không tìm thấy.!");
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + sendto + "' không tìm thấy .... Tiến hành gửi File đã thoát!");
                        }
                        break;

                        // case này dùng cho phía client nhận file hiện thông tin người gửi File 
                    case "CMD_SENDFILERESPONSE":
                        String receiver = st.nextToken(); 
                        String rMsg = ""; 
                        main.appendMessage("[CMD_SENDFILERESPONSE]: Username: " + receiver);
                        while (st.hasMoreTokens()) {
                            rMsg = rMsg + " " + st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) main.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("CMD_SENDFILERESPONSE" + " " + receiver + " " + rMsg);
                            saveMessage("CMD_SENDFILERESPONSE", receiver, rMsg, null);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_SENDFILERESPONSE]: " + e.getMessage());
                        }
                        break;
                        
                    case "CMD_SEND_FILE_XD":                     
                        try {
                            String send_sender = st.nextToken(); 
                            String send_receiver = st.nextToken();
                            String send_filename = st.nextToken();
                            main.appendMessage("[CMD_SEND_FILE_XD]: Host: " + send_sender);
                            this.createConnection(send_receiver, send_sender, send_filename);
                            saveMessage("CMD_SEND_FILE_XD", send_sender, send_filename, null);
                        } catch (Exception e) {
                            main.appendMessage("[CMD_SEND_FILE_XD]: " + e.getLocalizedMessage());
                        }
                        break;
                        // Case này hiển thị gửi FIle bị lỗi
                    case "CMD_SEND_FILE_ERROR":  
                        String eReceiver = st.nextToken();
                        String eMsg = "";
                        while (st.hasMoreTokens()) {
                            eMsg = eMsg + " " + st.nextToken();
                        }
                        try {     
                            Socket eSock = main.getClientFileSharingSocket(eReceiver); 
                            DataOutputStream eDos = new DataOutputStream(eSock.getOutputStream());
                            eDos.writeUTF("CMD_RECEIVE_FILE_ERROR " + eMsg);
                            saveMessage("CMD_SEND_FILE_ERROR", eReceiver, eMsg, null);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;
                        // case này dùng để hiển thị thông tin gửi File từ client kia và client này cs chấp nhận cho gửi File sang hay không
                    case "CMD_SEND_FILE_ACCEPT":
                        String aReceiver = st.nextToken();
                        String aMsg = "";
                        while (st.hasMoreTokens()) {
                            aMsg = aMsg + " " + st.nextToken();
                        }
                        try {
                            Socket aSock = main.getClientFileSharingSocket(aReceiver); 
                            DataOutputStream aDos = new DataOutputStream(aSock.getOutputStream());
                            aDos.writeUTF("CMD_RECEIVE_FILE_ACCEPT " + aMsg);
                            saveMessage("CMD_SEND_FILE_ACCEPT", aReceiver, aMsg, null);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;
                        // case này tiến hành thực thi gửi icon
                    case "CMD_SEND_ICON":
                        try {
                            // kiểm tra số luọng token để thực thi hay không ?
                            if (st.countTokens() < 2) {
                                main.appendMessage("[CMD_SEND_ICON] Error: Missing parameters.");
                                break;
                            }
                            String send = st.nextToken(); // lấy tên người gửi icon
                            String iconName = st.nextToken(); // lấy tên biểu tượng icon
                            for (int i = 0; i < main.clientList.size(); i++) { 
                                // gửi icon đến tất cả các user đang online trừ người gửi
                                String rece = (String) main.clientList.elementAt(i);
                                if (!rece.equals(send)) {
                                    try {
                                        Socket receiverSocket = (Socket) main.socketList.elementAt(i); // truy cập vào danh sách socket list
                                        DataOutputStream receiverDos = new DataOutputStream(receiverSocket.getOutputStream());
                                        receiverDos.writeUTF("SEND_MESSAGE_ICON " + send + " " + iconName); // gửi lệnh lên clientThread
                                    } catch (IOException e) {
                                        // hiển thị thông báo lên server nếu như gửi icon lỗi
                                        main.appendMessage("[CMD_SEND_ICON] Error sending icon to " + rece + ": " + e.getMessage());
                                    }
                                }
                            }
                            // hiển thị thông báo thành công khi gửi icon ok
                            main.appendMessage("[CMD_SEND_ICON]:  Icon sent an icon successfully.");
                            saveMessage("CMD_SEND_ICON", send, iconName, null);
                        } catch (NoSuchElementException e) {
                            main.appendMessage("[CMD_SEND_ICON] Error: Missing parameters.");
                        } catch (Exception e) {
                            main.appendMessage("[CMD_SEND_ICON] Error: " + e.getMessage());
                        }
                        break;
                        // case này tiến hành gửi ảnh tiến trình giữa 2 client
                    case "CMD_SEND_IMAGE":
                        try {
                            String nguoigui = st.nextToken(); // Lấy ra tên người gửi ảnh
                            String imageName = dis.readUTF(); // Đọc tên file ảnh

                            // Đọc dữ liệu ảnh vào mảng byte
                            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                            byte[] buffer = new byte[8192];
                            int bytesRead;

                            while ((bytesRead = dis.read(buffer)) != -1) {
                                byteArrayOutputStream.write(buffer, 0, bytesRead);
                            }
                            // ghi dữ liệu ảnh vào mảng byte
                            byte[] imageBytes = byteArrayOutputStream.toByteArray();
                            // gửi dữ liệu ảnh đến tất cả useronline trừ người gửi ra
                            for (int x = 0; x < main.clientList.size(); x++) {
                                String receiving = (String) main.clientList.elementAt(x); // truy cập phần từ vào danh sách client
                                if (!receiving.equals(nguoigui)) { // kiểm tra người nhận phải khac với người gửi 
                                    try {
                                        Socket socketClient = (Socket) main.socketList.elementAt(x);
                                        DataOutputStream out = new DataOutputStream(socketClient.getOutputStream());
                                        out.writeUTF("CMD_MESSAGE_IMAGE " + nguoigui + " " + imageName); // Gửi lệnh này lên clientThread
                                        out.writeInt(imageBytes.length); // gửi dữ liệu độ dài ảnh
                                        out.write(imageBytes); 
                                        out.flush(); // đảy dữ liệu ảnh lên
                                    } catch (Exception e) {
                                        main.appendMessage("[CMD_SEND_IMAGE]: An error occurred while sending photos to " + receiving + ": " + e.getMessage());
                                    }
                                }
                            }
                            main.appendMessage("[CMD_SEND_IMAGE]: Successfully sent 1 photo to another client");
                            saveMessage("CMD_SEND_IMAGE", nguoigui, imageName, imageBytes);
                        } catch (IOException e) {
                            main.appendMessage("[CMD_SEND_IMAGE]: Error while sending image: " + e.getMessage());
                        }
                        break;
                    default:
                        main.appendMessage("[CMDException]: Không rõ lệnh được thực thi! " + CMD);
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println(client);
            System.out.println("File Share: " + filesharing_username);
            main.removeFromTheList(client);
            if (filesharing_username != null) {
                main.removeClientFileSharing(filesharing_username);
            }
            main.appendMessage("[SocketThread]: Kết nối client bị đóng..!");
        }   
    }
    
    }
   
    



