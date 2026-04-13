import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
public class BcryptCheck {
  public static void main(String[] args) {
    BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
    String h = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH";
    String[] cands = {"123456","admin123","password","123123","admin","zhangsan","12345678"};
    for(String c: cands){
      System.out.println(c+"="+enc.matches(c,h));
    }
  }
}