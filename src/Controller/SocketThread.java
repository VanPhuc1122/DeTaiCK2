
package Controller;

import ViewServer.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

public class SocketThread implements Runnable {
    Socket socket;
    Server main;
    DataInputStream dis;
    StringTokenizer st;
    String client, filesharing_username;
    private final int BUFFER_SIZE = 1024; // tạo biến toàn cục có kích thước 1024

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
                dosS.writeUTF(format); // ghi nhận dữ liệu từ server
                main.appendMessage("[createConnection]: " + format);
            } else {
                main.appendMessage("[createConnection]: Client không được tìm thấy để chia sẽ '" + receiver + "'");
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + receiver + "' đã không được tìm tháy trong danh sách ."); 
            }
        } catch (IOException e) {
            main.appendMessage("[createConnection]: " + e.getLocalizedMessage());
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
                    // case này dùng để khi client đăng nhập sẽ nhảy vào casse này hiện thông báo lên server
                    case "CMD_JOIN":
                        String clientUsername = st.nextToken(); // chuỗi tiếp theo đã phân tách ra
                        client = clientUsername;
                        main.setClientList(clientUsername); // đẩy usernmae vào vector nó lưu thông tin
                        main.setSocketList(socket);
                        main.appendMessage("[Client]: " + clientUsername + " đã tham gia chương trình Chat !");
                        break;
                        
                        // case này tiến hành chat
                    case "CMD_CHAT":
                        String from = st.nextToken();
                        String sendTo = st.nextToken();
                        String msg = "";
                        while (st.hasMoreTokens()) { // chuyển tiếp vào các thành phần con đã phân tấch chuỗi
                            msg = msg + " " + st.nextToken();
                        }
                        Socket tsoc = main.getClientList(sendTo);
                        try {
                            DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream());
                            String content = from + ": " + msg;
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
                        main.appendMessage("CMD_SHARINGSOCKET : Client thiết lập một socket cho kết nối chia sẻ file...");
                        String file_sharing_username = st.nextToken();
                        filesharing_username = file_sharing_username;
                        main.setClientFileSharingUsername(file_sharing_username);
                        main.setClientFileSharingSocket(socket);
                        main.appendMessage("CMD_SHARINGSOCKET : Username: " + file_sharing_username);
                        main.appendMessage("CMD_SHARINGSOCKET : Chia Sẻ File đang được mở");
                        break;

                    case "CMD_SENDFILE":
                        main.appendMessage("CMD_SENDFILE : Client đang gửi một file...");
                        
                        String file_name = st.nextToken();
                        String filesize = st.nextToken();
                        String sendto = st.nextToken();
                        String consignee = st.nextToken();
                        main.appendMessage("CMD_SENDFILE : Từ: " + consignee);
                        main.appendMessage("CMD_SENDFILE : Đến: " + sendto);
                       
                        main.appendMessage("CMD_SENDFILE : sẵn sàng cho các kết nối..");
                        Socket cSock = main.getClientFileSharingSocket(sendto); 
                       

                        if (cSock != null) {

                            try {
                                main.appendMessage("CMD_SENDFILE : Đã được kết nối..!");
                                
                                main.appendMessage("CMD_SENDFILE : đang gửi file đến client...");
                                DataOutputStream cDos = new DataOutputStream(cSock.getOutputStream());
                                cDos.writeUTF("CMD_SENDFILE " + file_name + " " + filesize + " " + consignee);
                               
                                InputStream input = socket.getInputStream();
                                OutputStream sendFile = cSock.getOutputStream();
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int cnt;
                                while ((cnt = input.read(buffer)) > 0) {
                                    sendFile.write(buffer, 0, cnt);
                                }
                                sendFile.flush();
                                sendFile.close();
                                
                                main.removeClientFileSharing(sendto);
                                main.removeClientFileSharing(consignee);
                                main.appendMessage("CMD_SENDFILE : File đã được gửi đến client...");
                            } catch (IOException e) {
                                main.appendMessage("[CMD_SENDFILE]: " + e.getMessage());
                            }
                        } else { 

                            main.removeClientFileSharing(consignee);
                            main.appendMessage("CMD_SENDFILE : Client '" + sendto + "' không tìm thấy.!");
                            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                            dos.writeUTF("CMD_SENDFILEERROR " + "Client '" + sendto + "' không tìm thấy, Chia Sẻ File sẽ thoát.");
                        }
                        break;

                    case "CMD_SENDFILERESPONSE":
                      
                        String receiver = st.nextToken(); 
                        String rMsg = ""; 
                        main.appendMessage("[CMD_SENDFILERESPONSE]: username: " + receiver);
                        while (st.hasMoreTokens()) {
                            rMsg = rMsg + " " + st.nextToken();
                        }
                        try {
                            Socket rSock = (Socket) main.getClientFileSharingSocket(receiver);
                            DataOutputStream rDos = new DataOutputStream(rSock.getOutputStream());
                            rDos.writeUTF("CMD_SENDFILERESPONSE" + " " + receiver + " " + rMsg);
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
                        } catch (Exception e) {
                            main.appendMessage("[CMD_SEND_FILE_XD]: " + e.getLocalizedMessage());
                        }
                        break;

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
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;

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
                        } catch (IOException e) {
                            main.appendMessage("[CMD_RECEIVE_FILE_ERROR]: " + e.getMessage());
                        }
                        break;

                    default:
                        main.appendMessage("[CMDException]: Không rõ lệnh " + CMD);
                        break;
                }
            }
        } catch (IOException e) {

            System.out.println(client);
            System.out.println("File Sharing: " + filesharing_username);
            main.removeFromTheList(client);
            if (filesharing_username != null) {
                main.removeClientFileSharing(filesharing_username);
            }
            main.appendMessage("[SocketThread]: Kết nối client bị đóng..!");
        }
    }

}
