plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.kapt")
    alias(libs.plugins.google.ksp)
}

val isGHActions: Boolean = System.getenv("GITHUB_ACTIONS")?.toBoolean() ?: false

ext.set("version", "2.4.4")
ext.set("versionCode", 526)

android {
    compileSdk = 34
    namespace = "com.itachi1706.cepaslib"

    defaultConfig {
        minSdk = 19

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
    kotlinOptions {
        jvmTarget = "17"
    }
    lint {
//        abortOnError = !isGHActions
        abortOnError = false
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

val autoDisposeVersion   = "2.2.1"
val autoValueGsonVersion = "1.3.1"
val autoValueVersion     = "1.11.0"
val daggerVersion        = "2.51.1"
val groupieVersion       = "2.10.1"
val magellanVersion      = "1.1.0"
val roomVersion          = "2.6.1"

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