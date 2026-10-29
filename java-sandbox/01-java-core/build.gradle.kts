//Implementation(...) — основной тип.
// Библиотека нужна и для компиляции вашего кода, и для работы приложения во время выполнения (runtime).

//compileOnly(...) —
// нужна только на этапе компиляции. В итоговый .jar файл не упаковывается
// (так подключается Lombok, потому что после генерации кода он в рантайме не нужен).

//annotationProcessor(...) — подключает плагины компилятора, которые анализируют аннотации и генерируют код во время сборки
// (Lombok, MapStruct).

//testImplementation(...) — библиотека доступна только в тестах (JUnit, Mockito, Testcontainers).
// В боевой код она не попадет.

//runtimeOnly(...) — нужна только при выполнении программы, но не нужна для компиляции кода
// (например, JDBC-драйвер для PostgreSQL — вы пишете код против абстрактных интерфейсов Java,
// а конкретный драйвер подключается только в рантайме).

dependencies {
    // Lombok (для @Builder, @Getter, @Setter, @Slf4j и др.)
    compileOnly("org.projectlombok:lombok:1.18.48")
    annotationProcessor("org.projectlombok:lombok:1.18.48")

    // Jakarta Validation API (для @NotBlank, @Size, @Min и др.)
    implementation("jakarta.validation:jakarta.validation-api:3.1.0")

    // JUnit 5 для тестов
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.3")
}


