
package ViewClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import org.mindrot.jbcrypt.BCrypt;

public class Encode {
    public static String hassPass(String password){
            return BCrypt.hashpw(password, BCrypt.gensalt());
    }
     // Vì mật khẩu được mã hóa một chiều, không cần có quá trình giải mã. 
    // Bạn chỉ cần xác minh mật khẩu bằng cách mã hóa đầu vào và so sánh nó với mã băm đã lưu.
    public static boolean decodeCode(String encodeText, String hassedPass){
     return BCrypt.checkpw(encodeText, hassedPass);
    }
}
