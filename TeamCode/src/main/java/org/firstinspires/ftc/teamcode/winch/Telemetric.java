package org.firstinspires.ftc.teamcode.winch;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.slf4j.event.Level;

public interface Telemetric {

    Level DEFAULT_TELEMETRY_LEVEL = Level.TRACE;

    Telemetry getTelemetry();

    default Level getTelemetryLevel() {
        return DEFAULT_TELEMETRY_LEVEL;
    }

    default void log(String caption, Object message) {
        this.log(caption, message, false);
    }

    default void log(String caption, Object message, boolean immediate) {
        this.log(Level.INFO, caption, message, immediate);
    }

    default void log(Level logLevel, String caption, Object message) {
        this.log(logLevel, caption, message, false);
    }

    default void log(Level logLevel, String caption, Object message, boolean immediate) {
        if (logLevel == null || (logLevel.toInt() >= getTelemetryLevel().toInt())) {
            this.getTelemetry().addData(caption, message);
            if (immediate) {
                this.getTelemetry().update();
            }
        }
    }

}
