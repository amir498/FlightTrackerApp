# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep your model classes
-keep class com.example.flighttrackerappnew.data.** { *; }
-keep class com.example.flighttrackerappnew.domain.** { *; }
-keep class com.example.flighttrackerappnew.presentation.** { *; }
-keep class com.example.flighttrackerappnew.FlightApp { *; }

# Preserve generic type info for Gson
-keepattributes Signature, InnerClasses, EnclosingMethod, Exceptions

# Keep all TypeToken subclasses
-keep class * extends com.google.gson.reflect.TypeToken { *; }

# Keep Gson internals and annotations
-keep class com.google.gson.** { *; }
-keep class com.google.gson.internal.** { *; }
-keepattributes *Annotation*

# Retrofit + Gson converter
-keep class retrofit2.** { *; }
-keep interface retrofit2.** { *; }
-keepclassmembers class **$TypeAdapterFactory { *; }
-keep class **$TypeAdapterFactory { *; }

# Keep Gson TypeToken subclasses (stronger version)
-keep class * extends com.google.gson.reflect.TypeToken {
    <fields>;
    <methods>;
}

# Keep generic signatures on everything (R8 can be aggressive)
-keepattributes Signature, InnerClasses, EnclosingMethod

# Prevent obfuscation of anonymous inner classes inside data package
-keep class com.example.flighttrackerappnew.data.**$* { *; }
