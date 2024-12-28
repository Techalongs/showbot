package org.firstinspires.ftc.teamcode.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DeviceNotFoundException extends RobotException {

    private final String name;

    public DeviceNotFoundException(@NonNull String name) {
        this(name, null);
    }

    public DeviceNotFoundException(@NonNull String name, @Nullable String message) {
        super(message);
        this.name = name;
    }

    public DeviceNotFoundException(@NonNull String name, @Nullable String message, @Nullable Throwable cause) {
        super(message, cause);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format("Device not found: %s. Message: %s", name, this.getMessage());
    }

}
