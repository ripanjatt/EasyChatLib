EasyChatLib
-
> A simple UI library for creating chatbots and chatting apps.

Features
-
* Simple text messages
* Show incoming and outgoing images with/without message
* Show custom views (incoming/outgoing)
and much more...

Currently working on
-
* Long press messages
* Collapsable (For chatbot popup)
* More animations and UI features

Implementation
-

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.ripanjatt:EasyChatLib:v1.0.0'
}

<com.ripanjatt.easychatlib.EasyChatLib
    android:id="@+id/chatView"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    app:layout_constraintBottom_toBottomOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent" />
```

Screenshots
-
<img src='app/s01.png' width=480/>
<img src='app/s02.png' width=480/>
