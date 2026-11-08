package com.sandbox.core.exceptions;

import com.sandbox.core.common.ConsoleColor;
import com.sandbox.core.exceptions.custom.AppException;
import com.sandbox.core.exceptions.custom.NotFoundException;
import com.sandbox.core.exceptions.custom.ValidationException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExceptionsDemo {
    static void main() throws FileNotFoundException { // Задание 5 - добавил в throws

        try {
            var ae1 = new Exception("Some Inner Exception");
            var ae2 = new AppException("Some AppException", ae1);

            var nfe = new NotFoundException("Some NotFoundException");
            var ve = new ValidationException("Some ValidationException", "field");

            throw nfe;

        }
        // Объявление catch должно быть от частного к общему иначе: Exception 'SomeException' has already been caught
        // т.е. сверху мы ловим конкретное RuntimeException, а в самом конце (если ничего не поймали) - Exception (базовый)
        // Exception -> ... -> RuntimeException -> AppException -> NotFoundException | ValidationException
        catch (NotFoundException e) {
            // тут поймаются все: NotFoundException
            ConsoleColor.redLog("NotFoundException: " + e.getClass().getSimpleName() + " message: " + e.getMessage());
        } catch (ValidationException e) {
            // тут поймаются все: ValidationException
            ConsoleColor.redLog("ValidationException Exception: " + e.getClass().getSimpleName() + " message: " + e.getMessage());
        } catch (AppException e) {
            // тут поймаются все: AppException, NotFoundException, ValidationException
            ConsoleColor.redLog("AppException: " + e.getClass().getSimpleName() + " message: " + e.getMessage());
        } catch (RuntimeException e) {
            // тут поймаются все: RuntimeException, AppException, NotFoundException, ValidationException
            ConsoleColor.redLog("RuntimeException: " + e.getClass().getSimpleName() + " message: " + e.getMessage());
        } catch (Exception e) {
            // тут поймаются любые Exception
            ConsoleColor.redLog("Exception: " + e.getClass().getSimpleName() + " message: " + e.getMessage());
        }

        System.out.println("-----");

        try (var res = new Res("Res")) {
            throw new IllegalStateException("body");
        } catch (Exception e) {
            ConsoleColor.redLog("Exception: " + e.getClass().getSimpleName() + " message: " + e.getMessage() + "Suppressed: " + e.getSuppressed().length);
        }
        // IllegalStateException + getSuppressed(): [0] = IOException

        try (var res = new Res("Res")) {
        } catch (Exception e) {
            ConsoleColor.redLog("Exception: " + e.getClass().getSimpleName() + " message: " + e.getMessage() + "Suppressed: " + e.getSuppressed().length);
        }
        // IOException  + getSuppressed(): []

        // и оба выведут
        // open Res
        // close Res

        try (var res1 = new Res("Res1"); var res2 = new Res("Res2")) {
            throw new IllegalStateException("body");
        } catch (Exception e) {
            ConsoleColor.redLog("Exception: " + e.getClass().getSimpleName() + " message: " + e.getMessage() + "Suppressed: " + e.getSuppressed().length);
        }

        // Выведут т.к. ресурсы закрываются в порядке, обратном открытию (как стек)
        // open Res1
        // open Res2
        // close Res2
        // close Res1

        System.out.println("-----");

        try {
            doWork();
        } catch (Exception e) {
            ConsoleColor.redLog("Exception: " + e.getClass().getSimpleName());
            ConsoleColor.redLog("Cause: " + e.getCause());
            //e.printStackTrace(); // нашёл Caused by: java.nio.file.NoSuchFileException: no.txt
        }

        System.out.println("-----");

        // выведет 42 и ошибка просто потеряется
        System.out.println(doWork2());

        // выбросит ошибку - from finally
        // doWork3();

        System.out.println("-----");

        // Ошибка: Unhandled exception: java.io.FileNotFoundException
        try {
            // Нет ошибки т.к. есть try-catch
            FileReaderMock();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        // Нет ошибки т.к. есть в разделе throws у метода
        FileReaderMock();
    }

    public static void doWork() {
        try {
            doSomeWork();
        } catch (IOException e) {
            throw new AppException("не прочитали файл", e);
        }
    }

    private static void doSomeWork() throws IOException {
        Files.readString(Path.of("no.txt"));
    }

    private static Integer doWork2() {
        try {
            throw new RuntimeException("lost");
        } finally {
            return 42;
        }
    }

    private static void doWork3() {
        try {
            throw new IllegalStateException("original");
        } finally {
            throw new IllegalArgumentException("from finally");
        }
    }

    private static void FileReaderMock() throws FileNotFoundException {
        //new FileReader("x.txt");
    }
}


/*

1) Чем checked-исключения отличаются от unchecked, и почему в Spring-проектах их обычно оборачивают?
Компилятор требует поймать их или объявлять в разделе throws; иначе сборка падает.

Checked-исключения «заражают» сигнатуры всех вызывающих методов, не работают в лямбдах и стримах (интерфейсы вроде Function не объявляют throws),
 и в Spring по умолчанию откат транзакции (@Transactional) происходит только для unchecked-исключений

2) Что произойдёт, если и тело try-with-resources, и close() бросили исключения?
Ошибка из тела try-with-resources будет проброшена дальше, а ошибка из close() будет в getSuppressed()
в C# будет только ошибка из close()/dispose()

3) Почему return в finally опасен?
Мы потеряем брошенное исключение и вернём значение

4) Зачем передавать cause, и что вы увидите в stack trace?
Так строится цепочка исключение, если есть вложенные try-catch
В C# это InnerException
Но главное, что без cause теряется первопричина (где и почему реально упало)

5) Чем using из C# отличается от try-with-resources при исключении в Dispose/close?
Конструкция try-with-resources закрывает ресурсы в обратном порядке открытия.
Если исключение бросили и тело, и close(), главным остаётся исключение из тела, а ошибки закрытия лежат в getSuppressed().
В C# при using исключение из Dispose() заменяет исходное, и первая ошибка теряется
*/