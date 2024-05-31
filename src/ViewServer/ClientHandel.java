package ViewServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.util.StringTokenizer;

public class ClientHandel implements Runnable {
    private String username;
    private Socket socket;
    private Server server;
    private DataInputStream input;
    private DataOutputStream output;

    public ClientHandel(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
        try {
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            while (true) {
                String data = input.readUTF(); // đọc dữ liệu từ server
                StringTokenizer token = new StringTokenizer(data);
                String CMD = token.nextToken();
                switch (CMD) {
                    case "CMD_SEND_IMAGE":
                        receivingImage(token.nextToken());
                        break;
                    default:
                        throw new AssertionError();
                }
            }
        }  catch (EOFException eof) {
            System.out.println("Client disconnected");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (input != null) input.close();
                if (output != null) output.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void receivingImage(String imageName) {
        try {
            int imageSize = input.readInt();
            byte[] byteImage = new byte[imageSize];
            input.readFully(byteImage); // đọc dữ liệu từ datainputstream
            server.appendMessage("[Image Received] " + imageName);
            synchronized (server.socketList) {
            for (Object clientSocket : server.socketList) {
                 Socket cliSocket = (Socket) clientSocket;
                 if(cliSocket != this.socket){
                     DataOutputStream dos = new DataOutputStream(cliSocket.getOutputStream());
                     dos.writeUTF("CMD_SEND_IMAGE" + imageName);
                     dos.writeInt(imageSize);
                     dos.write(byteImage);
                     dos.flush();
                 }
            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
