
# Java
## build.gradle (Project)
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

## build.gradle (Modlue)
Download the latest version 
```gradle
dependencies {
    ...
    implementation 'com.github.nadtata:NadNR:0.0.5'
}
```

## Usage
```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BroadcastManager.init(this, appCode);
    }
```

```init()``` return int value
* EXECUTE = 0;
* NO_RESPONSE = -1;
