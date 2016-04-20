package org.jsoup.helper;

import java.net.Proxy;
import java.util.Collection;
import org.jsoup.parser.Parser;

public interface Request extends Base<Request> {
   
    Proxy proxy();

    Request proxy(Proxy proxy);

    Request proxy(String host, int port);
    
    int timeout();
    
    Request timeout(int millis);

    int maxBodySize();

    Request maxBodySize(int bytes);

    boolean followRedirects();

    Request followRedirects(boolean followRedirects);

    boolean ignoreHttpErrors();

    Request ignoreHttpErrors(boolean ignoreHttpErrors);

    boolean ignoreContentType();

    Request ignoreContentType(boolean ignoreContentType);

    boolean validateTLSCertificates();

    void validateTLSCertificates(boolean value);

    Request data(KeyVal keyval);

    Collection<KeyVal> data();

    Request requestBody(String body);

    String requestBody();

    Request parser(Parser parser);

    Parser parser();

    Request postDataCharset(String charset);

    String postDataCharset();

}