# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kenneylu/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:ans
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-ignorewarnings

# JSR 305 annotations are for embedding nullability information.
-dontwarn javax.annotation.**

# A resource is loaded with a relative path so the package of this class must be preserved.
#-keepnames class com.httpmodule.internal.publicsuffix.PublicSuffixDatabase

# Animal Sniffer compileOnly dependency to ensure APIs are compatible with older versions of Java.
-dontwarn org.codehaus.mojo.animal_sniffer.*

# OkHttp platform used only on JVM and when Conscrypt dependency is available.
-dontwarn com.httpmodule.internal.platform.ConscryptPlatform

-keep public class com.httpmodule.** { public *;}
-keep public class com.imgmodule.** { public *;}

-keep public class com.mobmixer.** { *; }
-keep public class androidx.** { *; }
-keep public class com.google.** { *; }
