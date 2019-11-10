package com.mobilegenomics.f5n.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.mobilegenomics.f5n.GUIConfiguration;
import com.mobilegenomics.f5n.R;
import com.mobilegenomics.f5n.core.AppMode;
import com.mobilegenomics.f5n.support.PermissionResultCallback;
import com.mobilegenomics.f5n.support.PermissionUtils;
import com.mobilegenomics.f5n.support.TimeFormat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        ActivityCompat.OnRequestPermissionsResultCallback, PermissionResultCallback {

    private static final String TAG = MainActivity.class.getSimpleName();

    PermissionUtils permissionUtils;                    // An instance of the permissionUtils

    ArrayList<String> permissions = new ArrayList<>();

    CheckBox checkBoxDumpLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkBoxDumpLog = findViewById(R.id.checkbox_dump_logcat);

        checkBoxDumpLog.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(final CompoundButton buttonView, final boolean isChecked) {
                if (isChecked) {
                    new DumpLogToFile().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });

        // Setup the permissions
        permissionUtils = new PermissionUtils(MainActivity.this);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            TextView txtVersionName = findViewById(R.id.txt_app_version);
            txtVersionName.setText("App Version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void downloadDataSet(View view) {
        GUIConfiguration.setAppMode(AppMode.DOWNLOAD_DATA);
        startActivity(new Intent(MainActivity.this, DownloadActivity.class));
    }

    public void startStandaloneMode(View view) {
        GUIConfiguration.setAppMode(AppMode.STANDALONE);
        startActivity(new Intent(MainActivity.this, PipelineActivity.class));
    }

    public void startMinITMode(View view) {
        GUIConfiguration.setAppMode(AppMode.SLAVE);
        startActivity(new Intent(MainActivity.this, MinITActivity.class));
    }

    public void startDemoMode(View view) {
        GUIConfiguration.setAppMode(AppMode.DEMO);
        startActivity(new Intent(MainActivity.this, DemoActivity.class));
    }

    /////////////////////////////
    // Permission functions
    /////////////////////////////

    @Override
    public void onStart() {
        permissionUtils.check_permission(permissions,
                "The app needs storage permission for reading images and camera permission to take photos", 1);
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        // redirects to utils
        permissionUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void NeverAskAgain(int request_code) {
        Log.i("PERMISSION", "NEVER ASK AGAIN");
        permissionUtils.check_permission(permissions,
                "The app needs storage permission for reading images and camera permission to take photos", 1);
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Log.i("PERMISSION PARTIALLY", "GRANTED");
        permissionUtils.check_permission(permissions,
                "The app needs storage permission for reading images and camera permission to take photos", 1);
    }

    @Override
    public void PermissionDenied(int request_code) {
        Log.i("PERMISSION", "DENIED");
        permissionUtils.check_permission(permissions,
                "The app needs storage permission for reading images and camera permission to take photos", 1);
    }

    // Callback functions
    @Override
    public void PermissionGranted(int request_code) {
        Log.i("PERMISSION", "GRANTED");
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_help:
                startActivity(new Intent(MainActivity.this, HelpActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    static class DumpLogToFile extends AsyncTask<Void, String, Void> {

        private static final String folderName = "mobile-genomics";

        private File logFile;

        private FileOutputStream fOut;

        private OutputStreamWriter myOutWriter;

        @Override
        protected void onPreExecute() {
            try {
                Runtime.getRuntime().exec("logcat -c");
                String dirPath = Environment.getExternalStorageDirectory() + "/" + folderName;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                logFile = new File(dir.getAbsolutePath() + "/f5n-logcat-dump.txt");
                if (!logFile.exists()) {
                    logFile.createNewFile();
                }
                fOut = new FileOutputStream(logFile, true);
                myOutWriter = new OutputStreamWriter(fOut);
                String header = "\n\n----------- Log for app session " + TimeFormat
                        .millisToDateTime(System.currentTimeMillis())
                        + " -----------\n";
                myOutWriter.append(header);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Process process = Runtime.getRuntime().exec("logcat");
                BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(process.getInputStream()));

                String line = "";

                while ((line = bufferedReader.readLine()) != null) {
                    myOutWriter.append(line);
                    myOutWriter.append("\n");
                    myOutWriter.flush();
                }
            } catch (IOException e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                myOutWriter.append("-------------------- End of Log --------------------\n\n");
                myOutWriter.flush();
                myOutWriter.close();
                fOut.close();
            } catch (IOException e) {
                Log.e(TAG, "Logcat Dump Error: " + e);
            }
        }
    }

}
