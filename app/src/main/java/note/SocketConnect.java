package note;

import com.alibaba.fastjson.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketConnect {
  private static Socket socket;
  private static PrintWriter pw;
  private static BufferedReader in;

  public static void initSocket(){
    try {
      socket = new Socket("192.168.1.101", 4399);
      pw = new PrintWriter(socket.getOutputStream());
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * 发送指令
   * @param j json 串{code, msg, data[{}]}
   * @return
   */
  public static String sendMsg(JSONObject j) throws IOException {
    String r = "";
    try {
      if(null != pw){
        pw.print(j.toString());
        pw.flush();
        r = in.readLine();
      }else{
        r = "{\"code\":0,\"msg\":\"socket has not been initilize\"}";
      }
    } catch (IOException e) {
      e.printStackTrace();
      closeAll();
    }
    return r;
  }

  private static void closeAll() throws IOException {
    if (null != in) {
      in.close();
    }
    if (null != pw) {
      pw.close();
    }
    if (null != socket) {
      socket.close();
    }
  }
}
