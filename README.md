# Android Permission Launcher

Easily Request permission with recommended rationale message with default shouldShowRequestPermissionRationale checks.

### Installation

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:
```groovy
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Step 2. Add the dependency

```groovy
dependencies {
    implementation "com.github.kevintorch:PermissionLauncher:1.0.3"
}
```

### Usage
```kotlin
class MainActivity : AppCompatActivity() {
    // just add a field in Activity or fragment. 
    private val permissionLauncher = PermissionLauncher(this, ::onPermissionGranted)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // then request permission
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    private fun onPermissionGranted() {
        // Do Something if Permission Granted.
    }
}
```

### Extra Features

```kotlin

permissionLauncher.setEnableRationale(false)   // default is true.

// or

permissionLauncher.setEnableRationale(true, "Your Cuatom Rationale Message.")

```

