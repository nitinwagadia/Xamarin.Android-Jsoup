package org.jsoup.helper;

import java.io.InputStream;


public interface KeyVal {

	
    KeyVal key(String key);

    String key();

    KeyVal value(String value);

    String value();

    KeyVal inputStream(InputStream inputStream);

    InputStream inputStream();

    boolean hasInputStream();
}
