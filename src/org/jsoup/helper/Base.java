package org.jsoup.helper;

import java.net.URL;
import java.util.Map;

import org.jsoup.Connection.Method;

public interface Base<T extends Base> {

    URL url();

    T url(URL url);

    Method method();

    T method(Method method);

    String header(String name);
    
    T header(String name, String value);

    boolean hasHeader(String name);
    
    boolean hasHeaderWithValue(String name, String value);

    T removeHeader(String name);

    Map<String, String> headers();

    String cookie(String name);

    T cookie(String name, String value);

    boolean hasCookie(String name);

    T removeCookie(String name);

    Map<String, String> cookies();
}
