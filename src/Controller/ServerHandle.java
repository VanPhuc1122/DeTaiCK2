
package Controller;
import ViewServer.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandle implements Runnable { 
    ServerSocket server;
    Server main;
    
    // biến này dùng để kiểm tra server có hoạt động hay không ?
    boolean Going = true;
    
    public ServerHandle(int port, Server main){
        main.appendMessage("[Server]: Máy Chủ hiện đang khởi động ở port "+ port); // khi khởi đôgnj server bấm start nó sẽ hiển thị thông báo này
        try {
            this.main = main;
            // truyền port vào server
            server = new ServerSocket(port);
            main.appendMessage("[Server]: Máy Chủ đã khởi động.!");
        } 
        catch (IOException e) { main.appendMessage("[IOException]: "+ e.getMessage()); }
    }
    
    @Override // trong phần run này lát sẽ thực thi luồng nó chạy
    public void run() {
        try {
            while(Going){
                // khi client kết nối biến này để chấp nhận kết nối từ phía server
                Socket socket = server.accept();

                
                // khởi động luồng 
                new Thread(new SocketThread(socket, main)).start();
            }
        } catch (IOException e) {
            main.appendMessage("[ServerThreadIOException]: "+ e.getMessage());
        }
    }

    // hàm stop server
    public void stop(){
        try {
            server.close();
            Going = false;
            System.out.println("Máy chủ đã tiến hành đóng chương trình..!");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    
}
