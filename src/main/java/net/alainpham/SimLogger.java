package net.alainpham;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SimLogger {

    private static final Logger logger = LoggerFactory.getLogger(SimLogger.class);

    private static SimLogger instance = new SimLogger();

    private String inputFileName;
    private String timestampFieldName;
    private String timestampPattern;
    private String timezone;
    private SimpleDateFormat df;
    private List<String> rawLines = new ArrayList<String>();
    private String rawString;
    private List<Map<String, Object>> processedLines = new ArrayList<Map<String, Object>>();
    private long lastIterationSecondsOfDay = 0;
    private Integer currentLineIndex = 0;

    private ObjectMapper objectMapper = new ObjectMapper();

    public SimLogger() {
        this.inputFileName = System.getenv("SLOG_INPUT_FILE");
        this.timestampFieldName = System.getenv("SLOG_TIMESTAMP_FIELD");
        this.timestampPattern = System.getenv("SLOG_TIMESTAMP_PATTERN");
        this.timezone = System.getenv("SLOG_TIMEZONE");
        this.df = new SimpleDateFormat(timestampPattern);
        this.df.setTimeZone(TimeZone.getTimeZone(timezone));
        try {
            readFile();
        } catch (IOException e) {
            logger.error("Error reading file: " + inputFileName);
        }
    }

    public static SimLogger getInstance() {
        return instance;
    }

    public List<String> getRawLines() {
        return rawLines;
    }

    public String getRawString() {
        return rawString;
    }

    public List<Map<String, Object>> getProcessedLines() {
        return processedLines;
    }

    public void readFile() throws IOException {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
        String line;
        while ((line = reader.readLine()) != null) {
            rawLines.add(line);
            content.append(line);
            content.append("\n");
        }
        rawString = content.toString();
        reader.close();
    }

    public Map<String, Object> addProcessedLine(Map<String, Object> line) throws ParseException {
        Date parsedDate = df.parse(line.get(timestampFieldName).toString());
        line.put("SLOGTS", parsedDate);
        line.put("00_INDEX",processedLines.size());
        processedLines.add(line);
        return line;
    }

    public void logNextLines() {
        Date currentDateTime = new Date();
        // test
        // try {
        // currentDateTime = df.parse("20240305150133.000");
        // } catch (ParseException e) {
        // e.printStackTrace();
        // }

        long currentSecondsOfDay = currentDateTime.toInstant().atZone(ZoneId.of(timezone)).toLocalTime()
                .toSecondOfDay();

        // day has reset print the rest of the logs from the previous day and start from
        // scratch
        if (currentSecondsOfDay < lastIterationSecondsOfDay) {
            while (currentLineIndex < processedLines.size()) {
                Map<String, Object> line = processedLines.get(currentLineIndex);
                try {
                    logger.info(objectMapper.writeValueAsString(line));
                } catch (JsonProcessingException e) {
                    logger.info(line.toString());
                }
                currentLineIndex++;
            }
            currentLineIndex = 0;
        }

        while (currentLineIndex < processedLines.size()) {
            Date parsedDate = (Date) processedLines.get(currentLineIndex).get("SLOGTS");
            long logLineSecondsOfDay = parsedDate.toInstant().atZone(ZoneId.of(timezone)).toLocalTime().toSecondOfDay();
            if (logLineSecondsOfDay > currentSecondsOfDay) {
                break;
            }

            Map<String, Object> line = processedLines.get(currentLineIndex);

            try {
                logger.info(objectMapper.writeValueAsString(line));
            } catch (JsonProcessingException e) {
                logger.info(line.toString());
            }
            currentLineIndex++;
        }

        lastIterationSecondsOfDay = currentSecondsOfDay;

    }

    public static Map<String, String> helperCollectionToMap(Collection<String> header, Collection<String> values) {
        Map<String, String> map = new TreeMap<String, String>();
        for (String key : header) {
            map.put(key, values.iterator().next());
            values.remove(values.iterator().next());
        }
        return map;
    }

}
