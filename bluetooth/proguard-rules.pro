# This is a configuration file for ProGuard.
# http://proguard.sourceforge.net/index.html#manual/usage.html
#
# This file is no longer maintained and is not used by new (2.2+) versions of the
# Android plugin for Gradle. Instead, the Android plugin for Gradle generates the
# default rules at build time and stores them in the build directory.

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
-dontoptimize
-dontpreverify
# Note that if you want to enable optimization, you cannot just
# include optimization flags in your own project configuration file;
# instead you will need to point to the
# "proguard-android-optimize.txt" file instead of this one from your
# project.properties file.

-keepattributes *Annotation*
# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable.Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

-keep public class com.wp.bluetooth.BleManager{
    public <fields>;
    public <methods>;
    public static <methods>;
    public static <fields>;
}

-keep public class com.wp.bluetooth.BuildConfig{
    public <fields>;
    public <methods>;
    public static <methods>;
    public static <fields>;
}
-keep public class com.wp.bluetooth.R{
    public <fields>;
    public <methods>;
    public static <methods>;
    public static <fields>;
}
-keep class com.wp.bluetooth.BlueBaseHandler{
 public  <methods>;
 public  <fields>;
}

-keep class com.wp.bluetooth.BleJsInterface{
 public  <methods>;
 public  <fields>;
}
-keep public enum com.wp.bluetooth.ErrorCode{
    public static <methods>;
    public <methods>;
    public static <fields>;
}
-keep public enum com.wp.bluetooth.BleResult{
    public static <methods>;
    public <methods>;
    public static <fields>;
}
-keep  public class com.wp.bluetooth.JsBlueToothDevice{
    private  <methods>;
    private  <fields>;
}


#忽略警告
-ignorewarnings
#保证是独立的jar,没有任何项目引用,如果不写就会认为我们所有的代码是无用的,从而把所有的代码压缩掉,导出一个空的jar
-dontshrink
#保护泛型
-keepattributes Signature