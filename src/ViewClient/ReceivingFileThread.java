package ViewClient;


import ViewClient.SendFile;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.DecimalFormat;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.ProgressMonitorInputStream;

public class ReceivingFileThread implements Runnable {
    
    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected Client main;
    protected StringTokenizer st;
    protected DecimalFormat df = new DecimalFormat("##,#00");
    private final int BUFFER_SIZE = 100;
    
    public ReceivingFileThread(Socket soc, Client m){
        this.socket = soc;
        this.main = m;
        try {
            dos = new DataOutputStream(socket.getOutputStream());
            dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " +e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // biến trong while nó kiểm tra luồng hiện tại có ngừng hay tắt 
            while(!Thread.currentThread().isInterrupted()){
                String data = dis.readUTF(); // đọc dữ liệu từ server
                st = new StringTokenizer(data); // lấy chuỗi tách ra
                String CMD = st.nextToken(); // lấy biến từ server
                switch(CMD){
                    
                    //   hàm này sẽ xử lý việc nhận một file trong một tiến trình nền xử lý từ một user khác
                    case "CMD_SENDFILE":
                        String consignee = null; // ban đầu file null
                            try {
                                String filename = st.nextToken(); // lấy file name từ server gử đến
                                int filesize = Integer.parseInt(st.nextToken());
                                consignee = st.nextToken(); // lấy tên người gửi file đến
                                main.setMyTitle("Đang tải File....");
                                System.out.println("Đang tải File....");
                                System.out.println("From: "+ consignee);
                                String path = main.getMyDownloadFolder() + filename;       // dowload file xuống                          
                             
                                FileOutputStream fos = new FileOutputStream(path);// ghi dữ liệu file đầu ra
                                InputStream input = socket.getInputStream();                                 // lấy dữ liệu file đầu vào
                                
                                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(main, "Downloading file please wait...", input);
                     
                                BufferedInputStream bis = new BufferedInputStream(pmis);
                               
                                // phần này đọc xem có dữ liệu hay không và truyền tốc độ phần trăm khi load file
                                byte[] buffer = new byte[BUFFER_SIZE];
                                int count, percent = 0;
                                while((count = bis.read(buffer)) != -1){
                                    percent = percent + count;
                                    int p = (percent / filesize);
                                    main.setMyTitle("Downloading File  "+ p +"%");
                                    fos.write(buffer, 0, count);
                                }
                                fos.flush(); // đẩy dữ liệu thẳng lên
                                fos.close(); // đóng dữ liệu lại
                                main.setMyTitle("You are logged in as: " + main.getMyUsername());
                                JOptionPane.showMessageDialog(null, "File đã được tải đến \n'"+ path +"'");
                                System.out.println("File đã được lưu: "+ path);
                            } catch (IOException e) {
                                // báo lỗi về người gửi
                                DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                                eDos.writeUTF("CMD_SENDFILERESPONSE "+ consignee + " Kết nối bị mất, vui lòng thử lại lần nữa.!");
                                
                                System.out.println(e.getMessage());
                                main.setMyTitle("Bạn đã được đăng nhập với tên: " + main.getMyUsername());
                                JOptionPane.showMessageDialog(main, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                                socket.close(); // đóng ct
                            }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " +e.getMessage());
        }
    }
}

