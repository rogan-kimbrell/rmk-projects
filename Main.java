import java.io.*;

public class Main{
   public static void main(String []args){
   InputStream in = new FileInputStream("../words.txt");
   Doublets doublets = new Doublets(in);
   }
}