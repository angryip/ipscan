package net.azib.ipscan.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.Socket;

public class IOUtils {
  public static void closeQuietly(Socket socket) {
    try {
      if (socket != null) socket.close();
    }
    catch (IOException ignore) {
    }
  }

  public static void closeQuietly(DatagramSocket socket) {
    if (socket != null) socket.close();
  }
}
