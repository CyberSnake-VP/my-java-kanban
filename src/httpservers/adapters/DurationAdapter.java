package httpservers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;

public class DurationAdapter extends TypeAdapter<Duration> {


    @Override
    public void write(JsonWriter out, Duration duration) throws IOException {
        out.value(Objects.nonNull(duration) ? duration.toMinutes() : null);
    }


    @Override
    public Duration read(JsonReader in) throws IOException {
        if(in.peek() == null) {
            in.nextNull();
            return null;
        }
        return Duration.ofMinutes(in.nextInt());
    }
}
