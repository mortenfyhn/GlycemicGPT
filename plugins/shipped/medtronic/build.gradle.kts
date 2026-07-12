import org.jetbrains.kotlin.gradle.dsl.JvmTarget

// minimed-pebble-bridge fork: Hilt/KSP dropped. This app constructs the driver
// manually (see PumpController), so no annotation processing is needed; the Hilt
// annotations in the DI/plugin classes stay on the compile classpath only. This
// also sidesteps KSP's tight coupling to the Kotlin version (no KSP for 2.3.20).
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.glycemicgpt.mobile.pump.medtronic"
    compileSdk = 35
    defaultConfig {
        minSdk = 30
        consumerProguardFiles("consumer-proguard-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
}

dependencies {
    implementation(project(":pump-driver-api"))

    // Android BLE
    implementation(libs.androidx.core.ktx)

    // Hilt annotations only (no processor) — compile classpath so the DI classes compile.
    compileOnly(libs.hilt.android)

    // OpenMinimed JavaSake: SAKE handshake + SeqCrypt session cipher (GPL-3.0, used with
    // the author's permission). Formerly vendored under org.openminimed.sake; now consumed
    // from Maven Central so Renovate tracks upstream releases (Tier D crypto -- manual review).
    implementation(libs.javasake)

    // Cryptography. JavaSake derives its session keys with AES-CMAC, which the JDK/Android
    // JCE does not provide; BouncyCastle supplies it (org.openminimed.sake.crypto.AesCmac).
    // Explicit pin so we stay on 1.84+ (timing-channel advisory) over JavaSake's transitive
    // 1.79. AES-CTR/ECB use the platform JCE.
    implementation(libs.bouncycastle)

    // Coroutines
    implementation(libs.coroutines.core)
    implementation(libs.coroutines.android)

    // Logging
    implementation(libs.timber)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.coroutines.test)
}
