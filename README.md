CheesecakeCEPASReader
========

This is my personal module for reading CEPAS cards based off FareBot

To use this library, run in your main project the following command

`git submodule add https://github.com/itachi1706/CheesecakeCEPASReader.git cepaslib`

## How to use

* Add the following lines to your project-level (/) build.gradle dependency
```gradle
classpath 'com.squareup.sqldelight:gradle-plugin:1.1.3'
plugins {
    id 'com.github.ben-manes.versions' version '0.21.0'
}
```
* Add the following lines to your app-level (/app) build.gradle dependency
```gradle
implementation project(':cepaslib')
```
* Add the following lines to settings.gradle
```gradle
include ':cepaslib'
```
* To invoke the activity add the following code
```java
startActivity(new Intent(this, MainActivity.class));
finish();
```
* Note: If your theme is a dark theme, ensure that AppCompatDelegate has properly updated it. View options possible [here](https://developer.android.com/reference/android/support/v7/app/AppCompatDelegate.html#mode_night_auto)
```java
AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES); // View more at AppCompatDelegate file
```
* In your settings screen activity where you wish to add the settings to, add the following line 
```java
new SettingsHandler(getActivity()).initSettings(this);
```
* Make the Preferences button in the library to point to your own settings screen if you wish for it, add the following to add the class to your SharedPreferences
```java
CEPASLibBuilder.setPreferenceClass(YourPreference.class);
```
* To show the "About" menu option, add the following  

```java
// Java
CEPASLibBuilder.INSTANCE.shouldShowAboutMenuItem(true);
```

```kotlin
// Kotlin
CEPASLibBuilder.shouldShowAboutMenuItem(true)
```

* For more information and other library modification capabilities look at [CEPASLibBuilder.kt](https://github.com/itachi1706/CheesecakeCEPASReader/blob/master/src/main/java/com/itachi1706/cepaslib/CEPASLibBuilder.kt)
