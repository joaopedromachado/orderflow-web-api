package br.com.orderflow.exception;

import java.time.Instant;

public class ApiErrorDetails {

    private String error;
    private String message;
    private int status;
    private Instant timestamp;

    public ApiErrorDetails(final Builder builder) {
        this.error = builder.error;
        this.message = builder.message;
        this.status = builder.status;
        this.timestamp = builder.timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(final String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {
        private String error;
        private String message;
        private int status;
        private Instant timestamp;

        public Builder error(final String error) {
            this.error = error;
            return this;
        }

        public Builder message(final String message) {
            this.message = message;
            return this;
        }

        public Builder status(final int status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(final Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiErrorDetails build() {
            return new ApiErrorDetails(this);
        }

    }
}
