# 🔒 Dependency Locking Sample

An Android demonstration project showcasing **dependency version conflicts** and how **Gradle's dependency locking** can help prevent unexpected version changes in production builds.

## 📱 Screenshot

![Dependency Locking Demo](https://github.com/franzandel/DependencyLockingSample/blob/master/DependencyLockingDemo.png)
*The app demonstrates version conflicts between declared and resolved dependencies*

## 🔄 Retrofit + OkHttp Version Conflict

### Declared Dependencies
```toml
# gradle/libs.versions.toml
okhttp = "3.3.1"           # Ancient version from 2016
retrofit = "2.8.1"         # Modern version expecting newer OkHttp
```

### Version Bumping by Gradle
When you build the project, Gradle automatically resolves version conflicts:

```
+--- com.squareup.okhttp3:okhttp:3.3.1 -> 3.14.7
+--- com.squareup.okhttp3:logging-interceptor:3.3.1  
     \--- com.squareup.okhttp3:okhttp:3.3.1 -> 3.14.7
+--- com.squareup.retrofit2:retrofit:2.8.1
     \--- com.squareup.okhttp3:okhttp:3.14.7
```

**What happens:**
- **Declared**: OkHttp 3.3.1 (ancient, 2016)
- **Retrofit requires**: OkHttp 3.14.7 (modern, 2019)
- **Gradle resolves**: Automatically bumps to 3.14.7
- **Version jump**: 11+ versions difference!

### The Problem
Without dependency locking, this automatic version bumping can cause:
- ❌ **Runtime errors**: `NoClassDefFoundError: Failed resolution of: Lokhttp3/internal/http/HttpEngine`
- ❌ **Unpredictable builds**: Different versions between dev/prod
- ❌ **API incompatibilities**: Breaking changes in major version jumps

### The Solution
```kotlin
// Real Solution : /dependency-locking.gradle.kts
// Sample :
dependencyLocking {
    lockAllConfigurations()
}
```

With dependency locking enabled:
- ✅ **Explicit version control** via lock files
- ✅ **Early detection** of version conflicts

## 🎯 Key Takeaways

### 1. **Transitive Dependencies Awareness**
- In libs.versions.toml, OkHttp is specified as 3.3.1
- Retrofit 2.8.1 automatically brings OkHttp 3.14.7
- Gradle resolves to highest version 3.14.7

### 2. **Production vs Lower Environment Version Difference**
In Lower Environment, usually we have Dev Tools which introduces additional dependencies. This might trigger version difference between Production and Lower Environment.

## How to execute Dependency Locking?

**Both Create and Update lock files**
   ```bash
   ./gradlew app:dependencies --write-locks
   ```