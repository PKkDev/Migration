plugins {
    id("java")
}

allprojects {
    group = "com.sandbox"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    // Принудительный UTF-8 для компиляции исходников
    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    // Принудительный UTF-8 для запуска JavaExec (включая Main.main())
    tasks.withType<JavaExec> {
        jvmArgs("-Dfile.encoding=UTF-8", "-Dsun.stdout.encoding=UTF-8", "-Dsun.stderr.encoding=UTF-8")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}