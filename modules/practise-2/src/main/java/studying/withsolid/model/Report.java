package studying.withsolid.model;

import lombok.Getter;
import studying.withsolid.exception.ApplicationErrorCode;
import studying.withsolid.exception.ApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class Report {
    private final String title;
    private final LocalDate date;
    private final LocalTime time;
    private final int carsSold;
    private final int motorcyclesSold;

    private Report(ReportBuilder builder) {
        title = builder.title;
        date = builder.date;
        time = builder.time;
        carsSold = builder.carsSold;
        motorcyclesSold = builder.motorcyclesSold;
    }

    /**
     * Starts step-by-step construction of a sales report.
     *
     * @return a new report builder
     */
    public static ReportBuilder builder() {
        return new ReportBuilder();
    }

    /**
     * Returns the report in the text format used for file storage.
     *
     * @return formatted report text
     */
    @Override
    public String toString() {
        return title + System.lineSeparator()
                + "Дата: " + date + System.lineSeparator()
                + "Время: " + time + System.lineSeparator()
                + "--------------------------------" + System.lineSeparator()
                + "Продано автомобилей: " + carsSold + " шт." + System.lineSeparator()
                + "Продано мотоциклов: " + motorcyclesSold + " шт." + System.lineSeparator()
                + "--------------------------------" + System.lineSeparator();
    }

    public static class ReportBuilder {
        private String title;
        private LocalDate date;
        private LocalTime time;
        private int carsSold;
        private int motorcyclesSold;

        /**
         * Sets the report title.
         *
         * @param title report title
         * @return this builder
         */
        public ReportBuilder title(String title) {
            this.title = title;
            return this;
        }

        /**
         * Sets the date for which the report is created.
         *
         * @param date report date
         * @return this builder
         */
        public ReportBuilder date(LocalDate date) {
            this.date = date;
            return this;
        }

        /**
         * Sets the report creation time.
         *
         * @param time report time
         * @return this builder
         */
        public ReportBuilder time(LocalTime time) {
            this.time = time;
            return this;
        }

        /**
         * Sets the number of sold cars.
         *
         * @param carsSold number of sold cars
         * @return this builder
         */
        public ReportBuilder carsSold(int carsSold) {
            this.carsSold = carsSold;
            return this;
        }

        /**
         * Sets the number of sold motorcycles.
         *
         * @param motorcyclesSold number of sold motorcycles
         * @return this builder
         */
        public ReportBuilder motorcyclesSold(int motorcyclesSold) {
            this.motorcyclesSold = motorcyclesSold;
            return this;
        }

        /**
         * Validates the specified data and creates an immutable report.
         *
         * @return completed report
         * @throws ApplicationException if a required field is blank or missing,
         *                              or a sales count is negative
         */
        public Report build() {
            if (title == null || title.isBlank()) {
                throw validationError("Report title must not be blank");
            }
            if (date == null) {
                throw validationError("Report date must not be null");
            }
            if (time == null) {
                throw validationError("Report time must not be null");
            }
            if (carsSold < 0) {
                throw validationError("Number of sold cars must not be negative");
            }
            if (motorcyclesSold < 0) {
                throw validationError("Number of sold motorcycles must not be negative");
            }

            return new Report(this);
        }

        private ApplicationException validationError(String message) {
            return new ApplicationException(ApplicationErrorCode.VALIDATION_ERROR, message, null);
        }
    }
}
