package org.firstinspires.ftc.teamcode.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.qualcomm.robotcore.hardware.HardwareMap;

import java.util.Optional;
import java.util.function.Consumer;

public class DeviceUtils {

    /**
     * Gets a hardware device, if it exists.
     *
     * @param hardware Hardware map
     * @param clazz Device class
     * @param name Name of device
     * @return Device, if found. Otherwise, null optional.
     * @param <T> Device type
     */
    public static <T> Optional<T> getOptionalDevice(
            @NonNull final HardwareMap hardware,
            @NonNull final Class<? extends T> clazz,
            @NonNull final String name
    ) {
        return getOptionalDevice(hardware, clazz, name, null);
    }

    /**
     * Gets a hardware device, if it exists, and configures it with the supplied consumer.
     *
     * @param hardware Hardware map
     * @param clazz Device class
     * @param name Name of device
     * @param configure Function to configure device
     * @return Device, if found. Otherwise, null optional.
     * @param <T> Device type
     */
    public static <T> Optional<T> getOptionalDevice(
            @NonNull final HardwareMap hardware,
            @NonNull final Class<? extends T> clazz,
            @NonNull final String name,
            @Nullable final Consumer<T> configure
    ) {
        T device;
        try {
            device = getDevice(hardware, clazz, name, configure);
        } catch (DeviceNotFoundException e) {
            device = null;
        }
        return Optional.ofNullable(device);
    }

    /**
     * Gets a hardware device.
     *
     * @param hardware Hardware map
     * @param clazz Device class
     * @param name Name of device as configured in FTC
     * @param <T> Device class
     * @return Device
     * @throws DeviceNotFoundException If device not found
     */
    public static <T> T getDevice(
            @NonNull final HardwareMap hardware,
            @NonNull final Class<? extends T> clazz,
            @NonNull final String name
    ) throws DeviceNotFoundException {
        return getDevice(hardware, clazz, name, null);
    }

    /**
     * Gets a hardware device and configures it with the supplied consumer.
     *
     * @param hardware Hardware map
     * @param clazz Device class
     * @param name Name of device as configured in FTC
     * @param configure Function to configure device
     * @return Device, if found. Otherwise, null optional.
     * @param <T> Device type
     * @throws DeviceNotFoundException If device not found
     */
    public static <T> T getDevice(
            @NonNull final HardwareMap hardware,
            @NonNull final Class<? extends T> clazz,
            @NonNull final String name,
            @Nullable final Consumer<T> configure
    ) throws DeviceNotFoundException {
        try {
            T device = hardware.get(clazz, name);
            if (configure != null) {
                configure.accept(device);
            }
            return device;
        } catch (Throwable t) {
            throw new DeviceNotFoundException(name, t.getMessage());
        }
    }

}
