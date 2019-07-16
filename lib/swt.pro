# many classes in SWT check package names in their constructors to
# prevent subclassing by classes outside SWT.  Rather than list them
# all here, we simply preserve all class names in SWT from
# obfuscation.

-keepnames class org.eclipse.swt.**


# the fields of many classes in org.eclipse.swt.internal are used from
# native code.

-keepclassmembernames class org.eclipse.swt.internal.** {
   <fields>;
 }
-keepclassmembers class org.eclipse.swt.internal.** {
   <fields>;
 }


# the following methods are called via reflection:

-keepclassmembers class org.eclipse.swt.graphics.Device {
   *** *Proc(...);
 }
-keepclassmembers class org.eclipse.swt.graphics.GC {
   *** convertRgn(...);
 }
-keepclassmembers class org.eclipse.swt.graphics.Path {
   *** newPathProc(...);
   *** lineProc(...);
   *** curveProc(...);
   *** closePathProc(...);
   *** applierFunc(...);
 }
-keepclassmembers class org.eclipse.swt.graphics.TextLayout {
   *** regionToRects(...);
 }
-keepclassmembers class org.eclipse.swt.graphics.Region {
   *** regionToRects(...);
   *** convertRgn(...);
 }
-keepclassmembers class org.eclipse.swt.dnd.DragSource {
   *** DragGetData(...);
   *** DragEnd(...);
   *** DragDataDelete(...);
   *** DragSendDataProc(...);
 }
-keepclassmembers class org.eclipse.swt.dnd.ClipboardProxy {
   *** getFunc(...);
   *** clearFunc(...);
 }
-keepclassmembers class org.eclipse.swt.dnd.DropTarget {
   *** Drag_Motion(...);
   *** Drag_Leave(...);
   *** Drag_Data_Received(...);
   *** Drag_Drop(...);
   *** DragTrackingHandler(...);
   *** DragReceiveHandler(...);
 }
-keepclassmembers class org.eclipse.swt.dnd.TableDropTargetEffect {
   *** AcceptDragProc(...);
 }
-keepclassmembers class org.eclipse.swt.dnd.TreeDropTargetEffect {
   *** AcceptDragProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Control {
   *** regionToRects(...);
   *** enterNotifyEventProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Display {
   *** *Proc(...);
 }
-keepclassmembers class org.eclipse.swt.accessibility.AccessibleFactory {
   <methods>;
 }
-keepclassmembers class org.eclipse.swt.accessibility.AccessibleObject {
   <methods>;
 }
-keepclassmembers class org.eclipse.swt.graphics.FontData {
   *** EnumLocalesProc(...);
 }
-keepclassmembers class org.eclipse.swt.internal.BidiUtil {
   *** windowProc(...);
   *** EnumSystemLanguageGroupsProc(...);
 }
-keepclassmembers class org.eclipse.swt.internal.ole.win32.COMObject {
   <methods>;
 }
-keepclassmembers class org.eclipse.swt.widgets.ColorDialog {
   *** CCHookProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Tree {
   *** CompareFunc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Tracker {
   *** transparentProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Display {
   *** monitorEnumProc(...);
   *** embeddedProc(...);
   *** windowProc(...);
   *** messageProc(...);
   *** msgFilterProc(...);
   *** foregroundIdleProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Composite {
   *** getMsgProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.FileDialog {
   *** OFNHookProc(...);
   *** filterProc(...);
   *** eventProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.Combo {
   *** CBTProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.DirectoryDialog {
   *** BrowseCallbackProc(...);
 }
-keepclassmembers class org.eclipse.swt.widgets.ToolBar {
   *** MenuItemSelectedProc(...);
 }
-keepclassmembers class org.eclipse.swt.ole.win32.OleFrame {
   *** getMsgProc(...);
 }

# image file format parsing is done via reflection.

-keep class org.eclipse.swt.internal.image.PNGFileFormat {
   <init>();
 }
