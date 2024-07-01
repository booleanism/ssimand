import org.gradle.process.internal.ExecException

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.whoevencares.ssimand"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.whoevencares.ssimand"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildToolsVersion = "34.0.0"
    ndkVersion = "27.0.11902837 rc2"
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.documentfile)
    // https://mvnrepository.com/artifact/commons-io/commons-io
    implementation(libs.commons.io)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.androidx.media3.common)
}

enum class SsimersBuildType(val type: String) {
    Debug("debug"),
    Release("release")
}

enum class SsimersTargetArch(val rustTarget: String, val jniTarget: String) {
    Arm64("aarch64-linux-android", "arm64-v8a"),
    Armv7("armv7-linux-androideabi", "armeabi-v7a")
}

data class Link(val src: String, val dst: String)

abstract class SsimersBuilder : DefaultTask() {
    @get:Input
    abstract val buildType: Property<SsimersBuildType>

    @get:Input
    abstract val targetArch: Property<SsimersTargetArch>

    @get:Input
    abstract val buildOnly: Property<Boolean>

    private val projectDir = System.getenv("PWD")

    private fun buildSsimers() {
//        logger.quiet("$projectDir/ssimers")
        val res = project.exec {
            commandLine(
                "cargo",
                "build",
                "--target",
                targetArch.get().rustTarget,
                "--${buildType.get().type}",
                "--manifest-path",
                "$projectDir/ssimers/Cargo.toml"
            )
        }

        if (res.exitValue != 0) {
            throw ExecException("Failed to build SSIMers on ${buildType.get().type} target to ${targetArch.get().jniTarget}")
        }

        println("Succeed to build ${buildType.get().type} build on ${targetArch.get().rustTarget} target")
    }

    private fun setUpDstDir(dst: String) {
        val res = project.exec {
            commandLine("mkdir", "-p", dst.replace("/libssim.so", ""))
        }

        if (res.exitValue != 0) {
            throw ExecException("failed to setup JNI directory")
        }
    }

    private fun linkSsimers() {
        val link = Link(
            "$projectDir/ssimers/target/${targetArch.get().rustTarget}/${buildType.get().type}/libssim.so",
            "$projectDir/app/src/main/jniLibs/${targetArch.get().jniTarget}/libssim.so"
        )

        setUpDstDir(link.dst)

        val res = project.exec {
            commandLine("ln", "-sf", link.src, link.dst)
        }

        if (res.exitValue != 0) {
            throw ExecException("Failed to link SSIMers ${buildType.get().type} build on ${targetArch.get().rustTarget} target to ${targetArch.get().jniTarget}")
        }

        println("Succeed to linking ${buildType.get().type} build on ${targetArch.get().rustTarget} target to ${targetArch.get().jniTarget}")
    }

    @TaskAction
    fun build() {
        logger.quiet("Building SSIMers");
        buildSsimers()
        if (!this.buildOnly.get()) {
            logger.quiet("linking SSIMers to Android project")
            linkSsimers()
        }
    }
}

val buildSsimersArm64Debug = tasks.register<SsimersBuilder>("buildSsimersArm64Debug") {
    buildOnly = true
    buildType = SsimersBuildType.Debug
    targetArch = SsimersTargetArch.Arm64
}

val buildSsimersArm64 = tasks.register<SsimersBuilder>("buildSsimersArm64") {
    buildOnly = true
    buildType = SsimersBuildType.Release
    targetArch = SsimersTargetArch.Arm64
}

val buildSsimersArmeabiDebug = tasks.register<SsimersBuilder>("buildSsimersArmeabiDebug") {
    buildOnly = true
    buildType = SsimersBuildType.Debug
    targetArch = SsimersTargetArch.Armv7
}

val buildSsimersArmeabi = tasks.register<SsimersBuilder>("buildSsimersArmeabi") {
    buildOnly = true
    buildType = SsimersBuildType.Release
    targetArch = SsimersTargetArch.Armv7
}

val buildAndLinkSsimersArm64Debug = tasks.register<SsimersBuilder>("buildAndLinkSsimersArm64Debug") {
    buildOnly = false
    buildType = SsimersBuildType.Debug
    targetArch = SsimersTargetArch.Arm64
}

val buildAndLinkSsimersArm64 = tasks.register<SsimersBuilder>("buildAndLinkSsimersArm64") {
    buildOnly = false
    buildType = SsimersBuildType.Release
    targetArch = SsimersTargetArch.Arm64
}

val buildAndLinkSsimersArmeabiDebug = tasks.register<SsimersBuilder>("buildAndLinkSsimersArmeabiDebug") {
    buildOnly = false
    buildType = SsimersBuildType.Debug
    targetArch = SsimersTargetArch.Armv7
}

val buildAndLinkSsimersArmeabi = tasks.register<SsimersBuilder>("buildAndLinkSsimersArmeabi") {
    buildOnly = false
    buildType = SsimersBuildType.Release
    targetArch = SsimersTargetArch.Armv7
}

tasks.register("ssimersAllDebug") {
    dependsOn(buildAndLinkSsimersArmeabiDebug)
    dependsOn(buildAndLinkSsimersArm64Debug)
}

tasks.register("ssimersAll") {
    dependsOn(buildAndLinkSsimersArmeabi)
    dependsOn(buildAndLinkSsimersArm64)
}