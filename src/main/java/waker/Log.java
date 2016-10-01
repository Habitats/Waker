package waker;

import java.time.LocalDateTime;

public class Log {

  public static void v(Object o) {
    System.out.println(LocalDateTime.now().toLocalTime() + " > " + o.toString());
  }
}
