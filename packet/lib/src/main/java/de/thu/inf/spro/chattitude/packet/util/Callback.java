package de.thu.inf.spro.chattitude.packet.util;

public interface Callback<T> {
    void call(T parameter);
}
