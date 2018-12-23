# TODO check https://github.com/ReadyTalk/avian-swt-examples/blob/master/swt.pro

-keepclasseswithmembernames class * {
	native <methods>;
}
-keepclassmembers class * extends java.lang.Enum {
	public static **[] values();
	public static ** valueOf(java.lang.String);
}
-keep public class * {
	public protected *;
}
-keep public class org.eclipse.swt.widgets.Display {
	<methods>;
}
-keep class org.eclipse.swt.internal.ole.win32.COMObject {
	<methods>;
}
-keep class org.eclipse.swt.dnd.ClipboardProxy {
	<methods>;
}
-keep class * {
	<methods>;
	<fields>;
}
-keep class org.eclipse.swt.internal.image.*FileFormat
-keep class * {
	% *Proc(...);
}
