# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/xiehehe/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

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
-keep class androidx.recyclerview.widget.**{*;}
-keep class androidx.viewpager2.widget.**{*;}

##***********************************混淆模板*************************##
###混淆规则
## 关闭压缩
#-dontshrink
## 关闭优化
#-dontoptimize
## 关闭混淆
#-dontobfuscate
## 表示保存该包下的类，但是子类会被混淆
#-keep class com.android.xx.*
## 表示保存该包下的类, 子类不会被混淆
#-keep class com.android.xx.**
## 想避免类中的成员不会被混淆
#-keep class com.android.xx.**{*;}
#指定压缩级别 1-7
-optimizationpasses 5

#不跳过非公共的库的类成员
-dontskipnonpubliclibraryclassmembers

#混淆是采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

#混淆类中额方法名也混淆了
#-useuniqueclassmembernames

#优化是允许访问并修改修饰符的类和类的成员
-allowaccessmodification

#将文件来源从命名为"SourceFile"字符串
-renamesourcefileattribute SourceFile

#保留行号
-keepattributes SourceFile,LineNumberTable

#保持泛型
-keepattributes Signature
##***********************************************************************##

#保持所有实现Serializable 接口的类成员
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator * ;
}

-keepclassmembers class * implements android.os.Parcelable {
    public <fields>;
    private <fields>;
}

# 保持测试相关代码
-dontnote junit.framework.**
-dontnote junit.runner.**
-dontwarn android.test.**
-dontwarn android.support.test.**
-dontwarn org.junit.**

#Fragment不需要在AndroidManifest.xml中注册，需要额外保护下
-keep public class * extends android.app.Fragment
-keep public class * extends androidx.fragment.app.Fragment

#Android SDK
#-keep class android.app.**{*;}
-keep public class * extends androidx.appcompat.app.AppCompatActivity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.preference.Preference
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.annotation.**
-keep public class * extends android.support.v7.**
-keep public class * extends androidx.**
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep class android.support.** {*;}
-keep class androidx.** {*;}
-keep class android.**{*;}

#自定义view
-keep public class * extends android.view.View {
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context,android.util.AttributeSet);
    public <init>(android.content.Context,android.util.AttributeSet,int);
}

# 自己的view
-keep class com.liuguilin.fragment.view.**{*;}

#枚举
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

#保持native方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

##****************************以上都是官方通用的模板*************************


##******************************第三方SDK****************

##******************** HttpClient *******************
-keep public class org.apache.commons.httpclient.** {*;}
-keep public class org.apache.commons.httpclient.auth.** {*;}
-keep public class org.apache.commons.httpclient.cookie.** {*;}
-keep public class org.apache.commons.httpclient.methods.** {*;}
-keep public class org.apache.commons.httpclient.params.** {*;}
-keep public class org.apache.commons.httpclient.util.** {*;}
-keep public class org.apache.commons.codec.** {*;}
-keep public class org.apache.commons.codec.net.** {*;}
-keep public class org.apache.commons.codec.binary.** {*;}
-keep public class org.apache.commons.logging.** {*;}
-keep public class org.apache.commons.logging.impl.** {*;}

##************************ Bmob ****************************
# 不混淆BmobSdk
-dontwarn cn.bmob.v3.**
-keep class cn.bmob.v3.** {*;}
-keep class c.b.** {*;}
-keep class c.b.a.** {*;}

#保证继承自BmobObject,BmobUser类的JavaBean不被混淆
-keep class * extends cn.bmob.v3.BmobObject {
    *;
}

#如果你是使用了okhttp,okio的包 请添加一下混淆代码
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** {*;}
-keep interface com.squareup.okhttp.** {*;}

#************************* Rxjava RxAndroid **********************
-dontwarn rx.*
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQuene*Field*{
    long produceIndex;
    long consumerIndex;
}



##********************** Gson *************************

#-keep class com.google.gson.stream.** {*;}

#Application classes that will be serialized/deserialized over Gson
-keep class com.google.gson.examples.android.model.** {*;}
-keep class com.liuguilin.framework.gone.TextBean.** {*;}
-keep class com.liuguilin.framework.gone.TokenBean.** {*;}
-keep class com.liuguilin.framework.gone.VoiceBean.** {*;}


##************************** Glide **************************
-dontwarn com.bumptech.glide.**
-keep class com.bumptech.glide.** {*;}
-keep public class * implements com.bumptech.glide.module.AppGlideModule
-keep public class * extends com.bumptech.glide.*
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
}


##************************** Bugly  检查bug *********************
-dontwarn com.tencent.bugly.**
-keep class com.tencent.bugly.** {*;}

##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

# Gson specific classes
-dontwarn sun.misc.**
#-keep class com.google.gson.stream.** { *; }

# Application classes that will be serialized/deserialized over Gson
-keep class cf.paradoxie.dizzypassword.bean.** { <fields>; }

# Prevent proguard from stripping interface information from TypeAdapterFactory,
# JsonSerializer, JsonDeserializer instances (so they can be used in @JsonAdapter)
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

# Prevent R8 from leaving Data object members always null
-keepclassmembers,allowobfuscation class * {
  @com.google.gson.annotations.SerializedName <fields>;
}

##---------------End: proguard configuration for Gson  ----------
##************************************ EventBus ********************
-keepattributes *Annotation*

##************************************ Tablayout *******************
-keep class com.google.android.** {*;}

##************************************ sweetalert *******************
-keep class cn.pedant.SweetAlert.* {*;}

##********************************* PhotoView *********************
-keep class com.github.chrisbanes.** {*;}

##******************************** ZxingLibaray *********************
-dontwarn com.google.zxing.**
-keep class com.google.zxing.** {*;}
-keep class cn.yipianfengye.android.** {*;}
-keepclasseswithmembernames class * {native <methods>;}

-keep class tv.danmaku.ijk.media.player.** {*;}
-dontwarn tv.danmaku.ijk.media.player.*
-keep interface tv.danmaku.ijk.media.player.** {*;}

-dontwarn
