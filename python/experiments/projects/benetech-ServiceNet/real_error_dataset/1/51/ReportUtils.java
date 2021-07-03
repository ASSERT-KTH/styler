package org.benetech.servicenet.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;


public final class ReportUtils {
    private static final Logger LOG = LoggerFactory.getLogger(ReportUtils.class);

    public static <T> File createCsvReport(String reportName, List<T> entities, String[] headers, String[] valueMappings) {
        DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());

        String fileName = reportName + "-" + currentDateTime + ".csv";
        File csvOutputFile = new File(fileName);
        try (PrintWriter pw = new PrintWriter(csvOutputFile)) {
            ICsvBeanWriter csvWriter = new CsvBeanWriter(pw, CsvPreference.STANDARD_PREFERENCE);

            csvWriter.writeHeader(headers);
            for (Object entity : entities) {
                csvWriter.write(entity, valueMappings);
            }

            csvWriter.close();
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }
        return csvOutputFile;
    }

    private ReportUtils() { }
}
