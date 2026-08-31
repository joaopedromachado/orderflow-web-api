package br.com.orderflow.exception;

import java.time.Instant;

public class ApiError {

    private String error;
    private String message;
    private int status;
    private Instant timestamp;

    public ApiError(Builder builder) {
        this.error = builder.error;
        this.message = builder.message;
        this.status = builder.status;
        this.timestamp = builder.timestamp;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public static class Builder {
        private String error;
        private String message;
        private int status;
        private Instant timestamp;

        public Builder error(String error) {
            this.error = error;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder status(int status) {
            this.status = status;
            return this;
        }

        public Builder timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public ApiError build() {
            return new ApiError(this);
        }

    }
}
