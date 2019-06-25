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
### Use wheel picker as a view
Initialize in your Application.onCreate() method
```kotlin
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelPicker.onTimeSelected = { hour, minute ->
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
### License
Copyright 2019 Son Huynh.
Licensed under the [Apache License, Version 2.0](LICENSE)
