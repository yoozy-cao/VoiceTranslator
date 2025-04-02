package com.example.aicard;


import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private MediaRecorder mediaRecorder;
    private boolean isRecording = false;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    // 录音文件路径
    private String outputFile;
    //    private File outputDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
    private File outputDirectory;
    private ListView fileListView;
    private MediaPlayer mediaPlayer;
    private ListView recordingListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        fileListView = findViewById(R.id.file_list_view);
        fileListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        outputDirectory = getExternalFilesDir(null);

        // 初始化打开目录按钮
        Button openDirectoryButton = findViewById(R.id.open_directory_button);
        openDirectoryButton.setOnClickListener(v -> {
            openDirectory(outputFile);
        });

        // 初始化录音按钮
        Button recordButton = findViewById(R.id.btn_record);
        Button stopButton = findViewById(R.id.btn_stop);
        outputFile = outputDirectory + "/recording.3gp";

        // 请求存储权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
        }

        recordButton.setOnClickListener(v -> {
            if (checkPermission()) {
                Log.d(TAG, "开始录音");
                generateOutputFileName();
                startRecording();
            }
        });

        stopButton.setOnClickListener(v -> {
            Log.d(TAG, "停止录音");
            stopRecording();
        });


        // 初始化下拉列表
        Spinner formatSpinner = findViewById(R.id.format_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.recording_formats, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        formatSpinner.setAdapter(adapter);
        formatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFormat = (String) parent.getItemAtPosition(position);
                if (selectedFormat.equals("MP3")) {
                    currentFormat = RecordingFormat.MP3;
                } else if (selectedFormat.equals("FLAC")) {
                    currentFormat = RecordingFormat.FLAC;
                } else if (selectedFormat.equals("3GP")) {
                    currentFormat = RecordingFormat.THREEGP;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        // 初始化播放按钮
        Button playButton = findViewById(R.id.btn_play);
        playButton.setOnClickListener(v -> {
            playSelectedRecording();
        });
        recordingListView = fileListView;
        recordingListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取点击的文件名
                String selectedFileName = (String) parent.getItemAtPosition(position);
                // 拼接文件地址
                String selectedFilePath = outputDirectory + "/" + selectedFileName;
                File recordingFile = new File(selectedFilePath);
                if (recordingFile.exists()) {
                    showRecordingDetailsDialog(recordingFile);
                } else {
                    Toast.makeText(MainActivity.this, "未找到录音文件", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 2;

    /**
     * 检查写入外部存储的权限，如果没有权限则请求权限
     *
     * @return 如果已经拥有权限返回true，否则返回false
     */
    private boolean checkWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startRecording();
            }
        }
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予，可进行录音操作
                startRecording();
            } else {
                // 权限被拒绝，给出提示
                Toast.makeText(this, "未授予写入权限，无法进行录音", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * 检查录音权限，如果没有权限则请求权限
     *
     * @return 如果已经拥有权限返回true，否则返回false
     */
    private boolean checkPermission() {
        // 检查是否有录音权限，如果没有则请求权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
            return false;
        }
        return true;
    }

    // 新增录音格式枚举
    private enum RecordingFormat {
        MP3, FLAC, THREEGP
    }

    ;
    // 默认使用MP3格式
    private RecordingFormat currentFormat = RecordingFormat.MP3;

    private void startRecording() {
        // 初始化MediaRecorder并设置相关参数
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        if (currentFormat == RecordingFormat.MP3) {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            // 设置音频采样率
            mediaRecorder.setAudioSamplingRate(44100);
            // 设置音频比特率
            mediaRecorder.setAudioEncodingBitRate(128000);
        } else if (currentFormat == RecordingFormat.FLAC) {
            /*
            Android的MediaRecorder.OutputFormat枚举中未包含FLAC格式。支持的音频输出格式主要有：
            DEFAULT, THREE_GPP, MPEG_4, AMR_NB, AMR_WB, AAC_ADIF, AAC_ADTS, OUTPUT_FORMAT_RTP_AVP, WEBM
            FLAC作为无损压缩格式，通常需要第三方库支持（如FFmpeg或LibFLAC）
            */
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        } else if (currentFormat == RecordingFormat.THREEGP) {
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        }
        mediaRecorder.setOutputFile(outputFile);
        try {
            // 准备并开始录音
            mediaRecorder.prepare();
            mediaRecorder.start();
            isRecording = true;
        } catch (IOException e) {
            // 打印异常信息
            Log.e(TAG, "录音准备或开始失败", e);
        }
    }

    private void generateOutputFileName() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String timestamp = dateFormat.format(new Date());
        String extension = "";
        if (currentFormat == RecordingFormat.MP3) {
            extension = ".mp3";
        } else if (currentFormat == RecordingFormat.FLAC) {
            extension = ".flac";
        } else if (currentFormat == RecordingFormat.THREEGP) {
            extension = ".3gp";
        }
        outputFile = outputDirectory + "/recording_" + timestamp + extension;
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayFilesInDirectory();
    }

    private void openDirectory(String filePath) {
        java.io.File file = new java.io.File(filePath);
        if (file.exists()) {
            android.content.Intent intent = new android.content.Intent(android.content.Intent.ACTION_VIEW);
            android.net.Uri uri = android.net.Uri.fromFile(file.getParentFile());
            intent.setDataAndType(uri, "resource/folder");
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        displayFilesInDirectory();
    }

    private void stopRecording() {
        if (isRecording) {
            // 停止录音，重置并释放MediaRecorder资源
            mediaRecorder.stop();
            mediaRecorder.reset();
            mediaRecorder.release();
            mediaRecorder = null;
            isRecording = false;
            Log.d(TAG, "录音完成，文件地址: " + outputFile);
            displayFilesInDirectory(); // 刷新ListView
        }
    }

    private void displayFilesInDirectory() {
        // File directory = new File(getExternalCacheDir().getAbsolutePath());
        File directory = new File(outputDirectory.getAbsolutePath());
        File[] files = directory.listFiles();
        if (files != null) {
            List<String> fileNames = new ArrayList<>();
            for (File file : files) {
                fileNames.add(file.getName());
            }
            Log.d(TAG, "文件列表: " + fileNames.toString());
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, fileNames);
            fileListView.setAdapter(adapter);
        }
    }

    private void playSelectedRecording() {
        if (fileListView.getCheckedItemPosition() != ListView.INVALID_POSITION) {
            String selectedFileName = (String) fileListView.getItemAtPosition(fileListView.getCheckedItemPosition());
            String selectedFilePath = outputDirectory + "/" + selectedFileName;
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setDataSource(selectedFilePath);
                mediaPlayer.prepare();
                mediaPlayer.start();
                Log.i(TAG, "播放录音文件: " + selectedFileName);
            } catch (IOException e) {
                Log.e(TAG, "播放录音文件失败", e);
            }
        }
    }

    private File getRecordingFileAtPosition(int position) {
        // 实现获取指定位置录音文件的逻辑
        // 这里需要根据实际情况返回正确的录音文件
        return null;
    }

    private void showRecordingDetailsDialog(File recordingFile) {
        // 创建一个AlertDialog.Builder对象
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("录音文件详细信息");

        // 构建详细信息字符串
        StringBuilder details = new StringBuilder();
        details.append("文件名: ").append(recordingFile.getName()).append("\n");
        details.append("文件大小: ").append(recordingFile.length()).append(" 字节\n");
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(recordingFile.getAbsolutePath());
            String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            String bitrate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
            String samplerate = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_SAMPLERATE);
            details.append("时长: ").append(duration != null ? Long.parseLong(duration) / 1000 + " 秒" : "未知").append("\n");
            details.append("比特率: ").append(bitrate != null ? bitrate + " bps" : "未知").append("\n");
            details.append("采样率: ").append(samplerate != null ? samplerate + " Hz" : "未知").append("\n");
            retriever.release();
        } catch (Exception e) {
            Log.e(TAG, "获取音频元数据失败", e);
        }
        // 可以添加更多详细信息，如时长等

        // 设置对话框内容
        builder.setMessage(details.toString());

        // 设置确定按钮
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // 创建并显示对话框
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}