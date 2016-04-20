package org.jsoup;

import org.jsoup.helper.Base;
import org.jsoup.helper.KeyVal;
import org.jsoup.helper.Request;
import org.jsoup.helper.Response;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.util.Collection;
import java.util.Map;


public interface Connection {

    
    enum Method {
        GET(false), POST(true), PUT(true), DELETE(false), PATCH(true), HEAD(false), OPTIONS(false), TRACE(false);

        private final boolean hasBody;

        Method(boolean hasBody) {
            this.hasBody = hasBody;
        }

        /**
         * Check if this HTTP method has/needs a request body
         * @return if body needed
         */
        public final boolean hasBody() {
            return hasBody;
        }
    }

   
    Connection url(URL url);
  
    Connection url(String url);
   
    Connection proxy(Proxy proxy);
  
    Connection proxy(String host, int port);

    Connection userAgent(String userAgent);

    Connection timeout(int millis);
    
    Connection maxBodySize(int bytes);

    Connection referrer(String referrer);

    Connection followRedirects(boolean followRedirects);

    Connection method(Method method);

    Connection ignoreHttpErrors(boolean ignoreHttpErrors);

  
    Connection ignoreContentType(boolean ignoreContentType);

    Connection validateTLSCertificates(boolean value);

    Connection data(String key, String value);

    Connection data(String key, String filename, InputStream inputStream);

    Connection data(Collection<KeyVal> data);

    Connection data(Map<String, String> data);

    Connection data(String... keyvals);

    KeyVal data(String key);

    Connection requestBody(String body);
    
    Connection header(String name, String value);

    Connection cookie(String name, String value);

    Connection cookies(Map<String, String> cookies);

    Connection parser(Parser parser);

    Connection postDataCharset(String charset);

    Document get() throws IOException;

    Document post() throws IOException;

    Response execute() throws IOException;

    Request request();

    Connection request(Request request);

    Response response();

    Connection response(Response response);

  
}
