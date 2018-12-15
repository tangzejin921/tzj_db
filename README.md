# tzj_db
目前是单表库

**只支持 android**
## 功能
flutter 调用android 的 sqlite

## 为什么要做下面的 工程引入
    因为工程结构被我改了，

    原来是：
    android
    ios
    lib
    pubspec.yaml

    被我改为了
    android
        src
            main
            flutter
                pubspec.yaml

    所以要做一些改动


## 工程引入
- pub 加入
    ```pub
    dev_dependencies:
      android_intent:
        git:
          url: git://github.com/tzjandroid/tzj_db.git
          path: tzj_db/src/flutter
    ```
- android 工程下的 settings.gradle 中改为如下
    ```Gradle
    def flutterProjectRoot = rootProject.projectDir.parentFile.toPath()

    def plugins = new Properties()
    def pluginsFile = new File(flutterProjectRoot.toFile(), '.flutter-plugins')
    if (pluginsFile.exists()) {
        pluginsFile.withReader('UTF-8') { reader -> plugins.load(reader) }
    }

    plugins.each { name, path ->
        def pluginDirectory = flutterProjectRoot.resolve(path).resolve('android').toFile()
        if(!pluginDirectory.exists()){
            pluginDirectory = flutterProjectRoot.resolve(path).getParent().getParent().toFile()
        }
        if(pluginDirectory.exists()){
            include ":$name"
            project(":$name").projectDir = pluginDirectory
        }
    }
    ```
- android 工程下的 build.gradle  加入
    ```Gradle
    rootProject.extensions.add("tzj_db",Type.isFlutterPlugin.name())
    enum Type{
        isAPP,
        isModule,
        isFlutterPlugin;
    }
    project.ext {
        ext._compileSdkVersion = 27
        ext._buildToolsVersion = '27.0.3'
        ext._minSdkVersion = 16
        ext._targetSdkVersion = 27
        ext._supportVersion = "27.1.1"
        ext.javaVersion = JavaVersion.VERSION_1_8
    }
    ```

## example
example 目录有个 demo