package waker;

import java.time.LocalDateTime;

/**
 * Created by mail on 01.05.2015.
 */
public class Log {

  public static void v(Object o) {
    System.out.println(LocalDateTime.now().toLocalTime() + " > " + o.toString());
  }
}
