import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.google.ksp)
}

ext.set("version", "2.5.0")
ext.set("versionCode", 589)

android {
    compileSdk = 36
    namespace = "com.itachi1706.cepaslib"

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("proguard-rules.pro")

        vectorDrawables.useSupportLibrary = true
        javaCompileOptions {
            annotationProcessorOptions {
                arguments["room.schemaLocation"] = "$projectDir/schemas"
                arguments["room.incremental"] = "true"
            }
        }
        ksp {
            arg("room.schemaLocation", "$projectDir/schemas")
            arg("room.incremental", "true")
        }
        kapt {
            arguments {
                arg("room.schemaLocation", "$projectDir/schemas")
                arg("room.incremental", "true")
            }
        }
    }

    buildTypes {
        getByName("debug") {
            multiDexEnabled = true
        }
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    lint {
        abortOnError = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.multidex)

    // Farebot
    implementation(libs.autodispose)
    implementation(libs.autodispose.android)
    implementation(libs.autodispose.lifecycle)
    implementation(libs.autodispose.error.prone)
    implementation(libs.dagger)
    implementation(libs.groupie)
    implementation(libs.groupie.databinding)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.magellan)
    implementation(libs.play.services.maps)
    implementation(libs.rxjava)
    implementation(libs.rxandroid)
    implementation(libs.rxrelay)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.cardview)
    implementation(libs.androidx.recyclerview)

    compileOnly(libs.auto.value.annotations)
    compileOnly(libs.auto.value.gson.runtime)

    api(libs.androidx.legacy.support.v4)
    api(libs.gson)
    api(libs.androidx.room.runtime)
    api(libs.androidx.room.ktx)

    kapt(libs.auto.value)
    ksp(libs.androidx.room.compiler)
    ksp(libs.dagger.compiler)
    kapt(libs.auto.value.gson)
    kapt(libs.auto.value.gson.runtime)
    kapt(libs.auto.value.annotations)

    annotationProcessor(libs.androidx.room.compiler)
    annotationProcessor(libs.auto.value)
    annotationProcessor(libs.auto.value.annotations)
    annotationProcessor(libs.auto.value.gson)
    annotationProcessor(libs.auto.value.gson.runtime)
}

apply(from = "./publish.gradle")
