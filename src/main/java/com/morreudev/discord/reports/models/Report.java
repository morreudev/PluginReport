package com.morreudev.discord.reports.models;

import java.util.Date;
import java.util.UUID;

public class Report {
    private final UUID id;
    private final String reporter;
    private final String reported;
    private final String reason;
    private final Date date;
    private boolean resolved;

    public Report(String reporter, String reported, String reason) {
        this.id = UUID.randomUUID();
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.date = new Date();
        this.resolved = false;
    }

    public Report(String reporter, String reported, String reason, Date date) {
        this.id = UUID.randomUUID();
        this.reporter = reporter;
        this.reported = reported;
        this.reason = reason;
        this.date = date;
        this.resolved = false;
    }

    public UUID getId() {
        return id;
    }

    public String getReporter() {
        return reporter;
    }

    public String getReported() {
        return reported;
    }

    public String getReason() {
        return reason;
    }

    public Date getDate() {
        return date;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }
} 