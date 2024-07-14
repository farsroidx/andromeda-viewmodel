# Keep StringConcatFactory class
-keep class java.lang.invoke.StringConcatFactory { *; }

# Keep all classes and interfaces in ir.farsroidx.m31 package
-keep class ir.farsroidx.m31.** { *; }

# Keep all fields and methods in ir.farsroidx.m31 package
-keepclassmembers class ir.farsroidx.m31.** { *; }

# Keep all extension functions in ir.farsroidx.m31 package
-keepclassmembers class ir.farsroidx.m31.** {
    public <methods>;
}