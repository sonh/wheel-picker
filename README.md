## WheelPicker [![](https://jitpack.io/v/com.sonhvp/wheel-picker.svg)](https://jitpack.io/#com.sonhvp/wheel-picker)
### Gradle Setup
In your project level build.gradle
```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```
In your app level build.gradle
```gradle
dependencies {
    implementation 'com.sonhvp:wheel-picker:0.0.1'
}
```
### Overview
WheelPicker is extend from the base `View` class to minimize performance impact by using native draw operations. We currently only support timepicker.
### Use wheel picker as a view
Define the `WheelTimePicker` in XML:
```xml
<com.sonhvp.wheelpicker.WheelTimePicker
            android:id="@+id/wheelTimePicker"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:background="@color/colorPrimary"
            app:wtp_textColor="#212121"
            app:wtp_textSize="36sp"
            app:wtp_dividerColor="#727272"
            app:wtp_dividerHeight="1dp"/>
```
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelTimePicker.onTimeSelected = { hour, minute ->
            //Update hour and minute
            
        }
    }
}
```
### Use wheel picker dialog
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn.setOnClickListener {
            WheelTimePickerDialog.show(this) {
                onNegative {
                  
                }
                onPositive { hour, minute ->
                    
                }
            }
        }
    }
}
```
### Customization
You can customize the `WheelTimePicker` via XML:
```
    app:wtp_textColor="#212121"
    app:wtp_textSize="36sp"
    app:wtp_dividerColor="#727272"
    app:wtp_dividerHeight="1dp"
```
### License
Copyright 2019 Son Huynh.
Licensed under the [Apache License, Version 2.0](LICENSE)
