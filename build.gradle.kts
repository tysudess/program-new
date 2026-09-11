import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.21"
    id("org.jetbrains.compose") version "1.7.3"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
}

group = "br.com.monitordenoticias"
version = "4.0.2-cleanroom"

kotlin { jvmToolchain(17) }

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")
    implementation("org.xerial:sqlite-jdbc:3.46.1.3")
    implementation("org.jsoup:jsoup:1.18.3")
    implementation("net.java.dev.jna:jna:5.14.0")
    implementation("net.java.dev.jna:jna-platform:5.14.0")
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "br.com.monitordenoticias.desktop.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi, TargetFormat.AppImage, TargetFormat.Deb)
            packageName = "MonitorDeNoticias"
            packageVersion = "4.0.2"
            description = "Monitor de notícias e vídeos com termos, demandas, histórico e automação"
            vendor = "Clean-room implementation"
            windows { menuGroup = "Monitor de Notícias"; shortcut = true; console = false; perUserInstall = true }
        }
    }
}

tasks.test { useJUnitPlatform() }
