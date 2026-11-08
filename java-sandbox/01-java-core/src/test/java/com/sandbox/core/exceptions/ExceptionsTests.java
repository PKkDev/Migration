package com.sandbox.core.exceptions;

import com.sandbox.core.exceptions.custom.AppException;
import com.sandbox.core.exceptions.custom.NotFoundException;
import com.sandbox.core.exceptions.custom.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import static com.sandbox.core.exceptions.ExceptionsDemo.doWork;
import static org.junit.jupiter.api.Assertions.*;

public class ExceptionsTests {
    @Test
    void catchNotFoundException() {
        Executable fn = () -> {
            throw new NotFoundException("Some NotFoundException");
        };
        assertThrows(NotFoundException.class, fn);
    }

    @Test
    void catchValidationException() {
        Executable fn = () -> {
            throw new ValidationException("Some ValidationException", "field");
        };
        assertThrows(ValidationException.class, fn);
    }

    @Test
    void catchAppException() {
        Executable fn = () -> {
            throw new ValidationException("Some ValidationException", "field");
        };
        assertThrows(AppException.class, fn);
    }

    @Test
    void catchSuppressed() {
        Executable fn = () -> {
            try (var res = new Res("Res")) {
                throw new IllegalStateException("body");
            } catch (Exception e) {
                throw e;
            }
        };
        var e = assertThrows(IllegalStateException.class, fn);
        assertEquals(1, e.getSuppressed().length);
    }

    @Test
    void catchCause() {
        Executable fn = () -> {
            try {
                doWork();
            } catch (Exception e) {
                throw e;
            }
        };
        var e = assertThrows(AppException.class, fn);
        assertNotNull(e.getCause());
    }
}
