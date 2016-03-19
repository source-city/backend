package com.github.sourcecity.application;

import java.io.IOException;
import java.io.InputStream;

public class ProgressCapturingStream extends InputStream {

    private InputStream delegate;
    private int position;

    public ProgressCapturingStream(InputStream stream) {
        this.delegate = stream;
    }

    @Override
    public int read() throws IOException {
        position++;
        return delegate.read();
    }

    public int getProgress() {
        return position;
    }

}
