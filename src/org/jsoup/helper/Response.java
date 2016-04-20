package org.jsoup.helper;

import java.io.IOException;
import org.jsoup.nodes.Document;

public  interface Response extends Base<Response> {

    int statusCode();

    String statusMessage();

    String charset();

    String contentType();

    Document parse() throws IOException;
    
    String body();

    byte[] bodyAsBytes();
}