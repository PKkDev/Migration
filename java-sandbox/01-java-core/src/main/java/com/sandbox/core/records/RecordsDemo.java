package com.sandbox.core.records;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.Objects;

public class RecordsDemo {
    /// Обычный классический класс (требует гору бойлерплейта)
    public static final class OldSchoolDeveloper {
        private final String name;
        private final int expYears;

        public OldSchoolDeveloper(String name, int expYears) {
            this.name = name;
            this.expYears = expYears;
        }

        public String getName() {
            return name;
        }

        public int getExpYears() {
            return expYears;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof OldSchoolDeveloper that)) return false;
            return expYears == that.expYears && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, expYears);
        }

        @Override
        public String toString() {
            return "OldSchoolDeveloper{name='" + name + "', expYears=" + expYears + "}";
        }
    }

    /// Эквивалент на record (все вышеперечисленное генерируется компилятором)
    ///
    /// Record — это всегда неизменяемый final-класс, наследующийся от java.lang.Record.
    /// У него все поля private final, а методы доступа генерируются без префикса get (developer.name(), а не developer.getName()).
    // Либо, если объект содержит много полей, подключают аннотацию Lombok
    @Builder(toBuilder = true)
    public record ModernDeveloper(
            // Это стандартная спецификация валидации данных в Java (Bean Validation / Hibernate Validator).
            @NotBlank(message = "Имя обязательно для заполнения") @Size(min = 2, max = 50, message = "Имя должно быть от 2 до 50 символов") String name,

            @Min(value = 0, message = "Опыт не может быть отрицательным") @Max(value = 70, message = "Некорректный стаж") int expYears) {

        /// Компактный конструктор (Compact Constructor) — валидация ДО присваивания полей
        public ModernDeveloper {
            if (expYears < 0) {
                throw new IllegalArgumentException("Опыт не может быть отрицательным");
            }
            if (name == null || name.isBlank()) {
                name = "Unknown"; // можно скорректировать значение на лету
            }
        }

        /// Дополнительный метод в record
        public boolean isSenior() {
            return expYears >= 5;
        }

        /// Ручной "with" (в Java пока нет оператора 'with', мутация делается методами-копировщиками)
        public ModernDeveloper withExpYears(int newExp) {
            return new ModernDeveloper(this.name, newExp);
        }

        @Override
        public String toString() {
            return "Имя: %s Опыт: %s".formatted(name, expYears);
        }
    }

    static void main(String[] args) {
        ModernDeveloper dev1 = new ModernDeveloper("Alex", 7);
        ModernDeveloper dev2 = new ModernDeveloper("Alex", 7);

        // Сравнение значений из коробки (structural equality)
        System.out.println("dev1.equals(dev2): " + dev1.equals(dev2)); // true

        // Геттеры без префикса get:
        System.out.println("Имя: " + dev1.name() + ", Опыт: " + dev1.expYears() + ", Senior: " + dev1.isSenior());

        // Создает новый экземпляр через клонирующий билдер:
        ModernDeveloper updated1 = dev1.toBuilder().expYears(10).build();
        System.out.println("Обновленный 1: " + updated1);

        // Создание копии с измененным полем
        ModernDeveloper updated2 = dev1.withExpYears(11);
        System.out.println("Обновленный 2: " + updated2);
    }
}
