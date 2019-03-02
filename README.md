CheesecakeCEPASReader
========

This is my personal module for reading CEPAS cards based off FareBot

To use this library, run in your main project the following command

`git submodule add https://github.com/itachi1706/CheesecakeCEPASReader.git cepaslib`

## How to use

* Add the following lines to your project-level (/) build.gradle dependency
```gradle
classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.6'
```
* Add the following lines to your app-level (/app) build.gradle dependency
```gradle
implementation project(':cepaslib')
```
* Add the following lines to settings.gradle
```gradle
include ':cepaslib'
```
* In your AndroidManifest.xml file you will define the activity that will call the fragment CEPASCardScanFragment, make sure it contains the following
```xml
<activity android:name="<path>"
        android:configChanges="keyboardHidden|orientation"
        android:label="<name>"
        android:screenOrientation="sensorPortrait"
        tools:ignore="AppLinkUrlError">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />
            <action android:name="android.intent.action.EDIT" />
            <action android:name="android.intent.action.PICK" />

            <category android:name="android.intent.category.DEFAULT" />

            <data android:mimeType="vnd.android.cursor.dir/${applicationId}.card" />
        </intent-filter>
</activity>
 ```
* In the onCreate method of your activity, call the following to invoke the fragment
```java
if (getSupportFragmentManager().findFragmentById(android.R.id.content)==null) {
    getSupportFragmentManager().beginTransaction()
            .add(android.R.id.content, new CEPASCardScanFragment())
            .commit();
}
```
* In your settings screen activity where you wish to add the settings to, add the following line 
```java
new SettingsHandler(getActivity()).initSettings(this);
```
* In your application styles folder, add the following items
```xml
<item name="TransportIcons">@array/TransportIcons</item>
<item name="ListHeaderTextColor">@android:color/background_dark</item>
<item name="LockImage">@drawable/locked</item>
<item name="DrawableClosedIndicator">@drawable/expander_close_holo_light</item>
<item name="DrawableOpenIndicator">@drawable/expander_open_holo_light</item>
```
