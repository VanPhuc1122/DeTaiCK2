
package ViewServer;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class OnlineListThread implements Runnable {
    Server main;
    public OnlineListThread(Server main){
        this.main = main;
    }
    @Override
    public void run() {
        try {
            // cái biến trong hàm while  interupted nó sẽ kiểm tra luồng có gián đoạn hay không
            while(!Thread.interrupted()){
                String msg = ""; // ban đầu danh sách rỗng
                for(int x=0; x < main.clientList.size(); x++){ // duyệt qua danh sách client
                    msg = msg+" "+ main.clientList.elementAt(x); // truy cập vào phần tử đó
                }
                
                for(int x=0; x < main.socketList.size(); x++){
                    Socket tsoc = (Socket) main.socketList.elementAt(x);
                    DataOutputStream dos = new DataOutputStream(tsoc.getOutputStream()); // ghi dữ liệu luồngd dầu ra kết nối từ Socket
                    if(msg.length() > 0){ // cs dữ liệu
                        dos.writeUTF("CMD_ONLINE "+ msg); // ghi dữ liệu xuống Server
                    }
                }
                Thread.sleep(2000);
            }
        } catch(InterruptedException e){
            main.appendMessage("[InterruptedException]: "+ e.getMessage());
        } catch (IOException e) {
            main.appendMessage("[IOException]: "+ e.getMessage());
        }
    }
    // hàm gửi nhận hình ảnh
    public void sendImage(String receiver, int imageSize, byte[] imageData) throws IOException {
        Socket reSocket = main.getClientList(receiver);
        if (reSocket != null) {
            try {
                DataOutputStream dataOutputStream = new DataOutputStream(reSocket.getOutputStream());
                dataOutputStream.writeUTF("CMD_SEND_IMAGE");
                dataOutputStream.writeInt(imageSize);
                dataOutputStream.write(imageData);
                main.appendMessage("[OnlineListThread]: Image sent to " + receiver);
            } catch (IOException e) {
                main.appendMessage("[OnlineListThread]: Error sending image to " + receiver + " - " + e.getMessage());
                throw e;
            }
        } else {
            main.appendMessage("[OnlineListThread]: Người nhận không tìm thấy trong danh sách");
            throw new IOException("Receiver not found in the client list");
        }
    }
    
}
