package httpservers.adapters;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class InstantAdapter extends TypeAdapter<Instant> {

    public static  DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd.MM.yyyy|HH:mm")
            .withZone(ZoneId.systemDefault());

    @Override
    public void write(JsonWriter out, Instant instant) throws IOException {
        out.value(Objects.nonNull(instant)? formatter.format(instant) : null);
    }

    @Override
    public Instant read(JsonReader in) throws IOException {
        if(in.peek() == null) {
            in.nextNull();
            return null;
        }
        return Instant.from(formatter.parse(in.nextString()));
    }
}
