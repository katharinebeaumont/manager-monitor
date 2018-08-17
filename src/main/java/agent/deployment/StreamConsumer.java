package agent.deployment;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

/**
 * Consumes string input and outputs to the consumer.
 * Required for local deployment service debugging.
 */
public class StreamConsumer implements Runnable {

    private InputStream inputStream;
    private Consumer<String> consumer;
 
    public StreamConsumer(InputStream inputStream, Consumer<String> consumer) {
        this.inputStream = inputStream;
        this.consumer = consumer;
    }
 
    @Override
    public void run() {
        new BufferedReader(new InputStreamReader(inputStream)).lines()
          .forEach(consumer);
    }
}

