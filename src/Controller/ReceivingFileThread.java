package Controller;
import ViewClient.Client;
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
            while(!Thread.currentThread().isInterrupted()){
                String data = dis.readUTF(); // đọc dữ liệu từ Server
                st = new StringTokenizer(data);
                String CMD = st.nextToken();
                
                switch(CMD){
                    
                // xử lí nhận file từ client kia
                    case "CMD_SENDFILE":
                        String consignee = null;
                            try {
                                String filename = st.nextToken();
                                int filesize = Integer.parseInt(st.nextToken());
                                consignee = st.nextToken();  // lấy username gửi file
                                main.setMyTitle("Đang tải File....");
                                System.out.println("Đang tải File....");
                                System.out.println("From: "+ consignee);
                                String path = main.getMyDownloadFolder() + filename;                                
                             
                                FileOutputStream fos = new FileOutputStream(path);
                                InputStream input = socket.getInputStream();                                
                               
                                ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(main, "Downloading file please wait...........!", input);
                         
                                BufferedInputStream bis = new BufferedInputStream(pmis);
                         
                                byte[] buffer = new byte[BUFFER_SIZE]; // tạo 1 mảng byte chứa tối đa kí tự
                                int count, percent = 0;
                                while((count = bis.read(buffer)) != -1){
                                    percent = percent + count;
                                    int p = (percent / filesize);
                                    main.setMyTitle("Downloading File  "+ p +"%");
                                    fos.write(buffer, 0, count);
                                }
                                fos.flush();
                                fos.close();
                                main.setMyTitle("Bạn đã đăng nhập tên: " + main.getMyUsername());
                                JOptionPane.showMessageDialog(null, "File đã tiến hành được tải đến \n'"+ path +"'");
                                System.out.println("File đã tiến hành được lưu: "+ path);
                            } catch (IOException e) {
                             
                                DataOutputStream eDos = new DataOutputStream(socket.getOutputStream());
                                eDos.writeUTF("CMD_SENDFILERESPONSE "+ consignee + " Kết nối bị mất, vui lòng thử lại sau.......!");
                                
                                System.out.println(e.getMessage());
                                main.setMyTitle("Bạn đã được đăng nhập với tên: " + main.getMyUsername());
                                JOptionPane.showMessageDialog(main, e.getMessage(), "Exception", JOptionPane.ERROR_MESSAGE);
                                socket.close(); // đóng chương t rình
                            }
                        break;
                }
            }
        } catch (IOException e) {
            System.out.println("[ReceivingFileThread]: " +e.getMessage());
        }
    }
}

