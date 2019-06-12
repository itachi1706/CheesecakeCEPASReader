CheesecakeCEPASReader
========

This is my personal module for reading CEPAS cards based off FareBot

To use this library, run in your main project the following command

`git submodule add https://github.com/itachi1706/CheesecakeCEPASReader.git cepaslib`

## How to use

* Add the following lines to your project-level (/) build.gradle dependency
```gradle
classpath 'com.squareup.sqldelight:gradle-plugin:1.1.3'
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
* Note: If your theme is a dark theme, add the following to your Main Activity (if you have initialized SharedPreferece, you do not need to reinitialize it)
// TODO: To reimplement dark mode toggle
```java
SharedPreference sp = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
sp.edit().putBoolean("cepas_dark_theme", true).apply();
```
* In your settings screen activity where you wish to add the settings to, add the following line 
```java
new SettingsHandler(getActivity()).initSettings(this);
```
* Make the Preferences button in the library to point to your own settings screen if you wish for it, add the following to add the class to your SharedPreferences
```java
PreferenceManager.getDefaultSharedPreferences(this).edit().putString("utility_preference_class", "<class to your Settings Screen Activity").apply();
```