plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")

    // kapt (Annotation 읽기)
    id("kotlin-kapt")
}

android {
    namespace = "com.example.unittestingbasics"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.unittestingbasics"
        minSdk = 26
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        dataBinding = true // 데이터 바인딩 시
    }
}

dependencies {

    // 프로젝트 생성 시 built-in 되어있던 Dependency들 (범주화를 위해, 이 중 일부는 아래쪽 코드로 옮겼음)
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Android용으로 특별히 만들어진 사용자 인터페이스 테스트 프레임워크

    // JUnit: 자바와 안드로이드 개발에서 표준으로 사용되는 테스트 프레임워크. 다른 Dependency들의 기반으로도 널리 쓰인다
    testImplementation("junit:junit:4.13.2") // 프로젝트 생성 시 built-in 되어있던 Dependency.
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // 프로젝트 생성 시 built-in 되어있던 Dependency. JUnit을 확장해 안드로이드에서 테스트할 수 있게 해줌
    testImplementation("androidx.test.ext:junit:1.2.1") // JUnit을 확장해 안드로이드에서 테스트할 수 있게 해줌 (Local unit test용)

    // Truth: 테스트 성공 및 실패 메시지를 더 읽기 쉽게 만들어주는 라이브러리
    val truthVersion = "1.4.3"
    testImplementation("com.google.truth:truth:$truthVersion")
    testImplementation("com.google.truth.extensions:truth-java8-extension:$truthVersion") // Truth가 Java 8에 도입된 기능을 이용해 테스트하도록 만들어주는 추가 라이브러리
}