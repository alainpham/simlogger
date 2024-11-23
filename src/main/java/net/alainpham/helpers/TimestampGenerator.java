package net.alainpham.helpers;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TimestampGenerator {    
    
    private static final int PERIOD_HOURS = 3;
    private static final int START_RAMP_MINUTES = 20;
    private static final int START_RAMP_DPM_NOISE = 5;
    private static final int START_RAMP_DPM = 10;
    private static final int DPM = 30;
    private static final int DPM_NOISE = 13;
    private static final int END_RAMP_MINUTES = 30;
    private static final int END_RAMP_DPM = 45;
    private static final int END_RAMP_DPM_NOISE = 4;

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endTime = startTime.plusHours(PERIOD_HOURS);
        LocalDateTime currentTime = startTime;
        List<LocalDateTime> timestampsList = new ArrayList<>();
        Random random = new Random();

        /* loop until the end of time each loop generates timestamps for a minute */
        while (currentTime.isBefore(endTime)) {
            int timestampsPerMinute;
            if (currentTime.isBefore(startTime.plusMinutes(START_RAMP_MINUTES))) {
                timestampsPerMinute = START_RAMP_DPM + random.nextInt(START_RAMP_DPM_NOISE);
            } else if (currentTime.isBefore(endTime.minusMinutes(END_RAMP_MINUTES))) {
                timestampsPerMinute = DPM + random.nextInt(DPM_NOISE);
            } else {
                timestampsPerMinute = END_RAMP_DPM + random.nextInt(END_RAMP_DPM_NOISE);
            }

            long stepsMS = 1000*60 / timestampsPerMinute;
            int i=0;
            for (i = 0; i < timestampsPerMinute; i++) {
                timestampsList.add(currentTime);
                currentTime = currentTime.plus(stepsMS, ChronoUnit.MILLIS);
            }

            /* in case its not rounded up to exact next minute */
            if (currentTime.getSecond() != 0 || currentTime.getNano() != 0) {
                currentTime = currentTime.withSecond(0).withNano(0).plusMinutes(1);
            }
            
            System.out.println("Generated " + i + " timestamps for " + currentTime + " stepMs " + stepsMS);
        }

        // Write timestamps from the list to the file
        try (FileWriter writer = new FileWriter("timestamps.txt")) {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

            for (LocalDateTime timestamp : timestampsList) {
                String formattedTimestamp = timestamp.atOffset(ZoneOffset.UTC).format(formatter);
                writer.write(formattedTimestamp + "\n");
            }
            System.out.println("Timestamps have been written to timestamps.txt");
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
