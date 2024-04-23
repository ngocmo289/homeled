package com.nnmo.homeled;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {


    private SwitchCompat switchCompatPN,switchCompatPK,switchCompatNS, switchCompatCong;

    private SwitchCompat switchTC; // SwitchCompat ở trên (AUTO/HAND)
    private ImageView imageViewPN, imageViewPK, bgr_main;
    private static final int SPEECH_REQUEST_CODE = 100;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnVoiceSearch = findViewById(R.id.btnVoiceSearch);
        btnVoiceSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceSearch();
            }
        });

        bgr_main = findViewById(R.id.bgr_main);
        // Lấy đối tượng Calendar hiện tại
        Calendar calendar = Calendar.getInstance();
        // Lấy thời gian hiện tại
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Giờ theo định dạng 24 giờ
        if(hour>=18 || hour<=5){
            bgr_main.setBackgroundResource(R.drawable.dem);
        } else {
            bgr_main.setBackgroundResource(R.drawable.sky);
        }

        switchTC = findViewById(R.id.nutTC);
        switchCompatPN = findViewById(R.id.nutPN);
        switchCompatPK = findViewById(R.id.nutPK);
        switchCompatNS = findViewById(R.id.nutNS);
        switchCompatCong = findViewById(R.id.nut_cong);
        imageViewPN = findViewById(R.id.pn);
        imageViewPK = findViewById(R.id.pk);

// Lấy trạng thái ban đầu của switchCompatPN và switchCompatPK
        boolean checkpnBANDAU = switchCompatPN.isChecked();
        boolean checkpkBANDAU = switchCompatPK.isChecked();

// Cập nhật nền ban đầu cho imageViewPN và imageViewPK dựa trên trạng thái ban đầu của SwitchCompat
        updateBackground(checkpnBANDAU, imageViewPN, R.drawable.pn, R.drawable.pn_off);
        updateBackground(checkpkBANDAU, imageViewPK, R.drawable.pk, R.drawable.pk_off);

        // Lắng nghe sự kiện khi trạng thái của switch thủ công thay đổi
        switchTC.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Nếu switchTC được chọn là "AUTO"
                if (isChecked) {
                    // Đóng băng switchNS (ON/OFF)
                    switchCompatNS.setEnabled(false); // Không thể bấm
                    if(hour>=18 && hour<=5){
                        //Bật đèn ngoài sân
                    }
                } else {
                    // Nếu switchTC được chọn là "HAND"
                    switchCompatNS.setEnabled(true); // Có thể bấm
                }
            }
        });


        //đèn phòng ngủ
        switchCompatPN.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Gọi lại updateBackground khi trạng thái của switchCompatPN thay đổi
                updateBackground(isChecked, imageViewPN, R.drawable.pn, R.drawable.pn_off);
            }
        });


        //đèn phòng khách
        switchCompatPK.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Gọi lại updateBackground khi trạng thái của switchCompatPK thay đổi
                updateBackground(isChecked, imageViewPK, R.drawable.pk, R.drawable.pk_off);
            }
        });


        switchCompatNS.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //Bật tắt ngoài sân nè
            }
        });

        switchCompatCong.setOnCheckedChangeListener(new SwitchCompat.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //mở tắt cổng nè
            }
        });


    }

    // Phương thức updateBackground để cập nhật nền của imageView dựa trên trạng thái của SwitchCompat
    private void updateBackground ( boolean isChecked, ImageView imageView,int drawableOn, int drawableOff){
        if (isChecked) {
            // Nếu SwitchCompat được kiểm tra (ON), thiết lập hình nền khi ON cho imageView
            imageView.setBackgroundResource(drawableOn); // Sử dụng hình nền khi ON
        } else {
            // Nếu SwitchCompat không được kiểm tra (OFF), thiết lập hình nền khi OFF cho imageView
            imageView.setBackgroundResource(drawableOff); // Sử dụng hình nền khi OFF
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
       if(item.getItemId() == R.id.Home) {
           Intent intent = new Intent(this, MainActivity.class);
           startActivity(intent); // Bắt đầu Activity mới
           return true;
       }
       else{
            Intent intent1 = new Intent(this, chart.class);
            startActivity(intent1); // Bắt đầu Activity mới
            return true;
       }
    }


    public void startVoiceSearch() {
        // Bắt đầu Intent nhận dạng giọng nói
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Bật/Tắt tầng..., ngoài sân, hoặc Mở/Đóng cửa");

        // Kiểm tra xem thiết bị của bạn có hỗ trợ nhận dạng giọng nói không
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } else {
            Toast.makeText(this, "Your device does not support speech recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && results.size() > 0) {
                String result = results.get(0); // Lấy kết quả đầu tiên
                TextSubmit(result);
            }
        }
    }

    public void TextSubmit(String command) {
        // Process the recognized text command here
        if (command != null) {
            if (command.contains("bật đèn tầng 1") || command.contains("bật đèn lầu trệt")) {
                updateBackground(true, imageViewPK, R.drawable.pk, R.drawable.pk_off);
                switchCompatPK.setChecked(true);
            } else if (command.contains("tắt đèn tầng 1") || command.contains("tắt đèn lầu trệt")) {
                updateBackground(false, imageViewPK, R.drawable.pk, R.drawable.pk_off);
                switchCompatPK.setChecked(false);
            } else if (command.contains("bật đèn tầng 2") || command.contains("bật đèn lầu 1")) {
                updateBackground(true, imageViewPN, R.drawable.pn, R.drawable.pn_off);
                switchCompatPN.setChecked(true);
            } else if (command.contains("tắt đèn tầng 2") || command.contains("tắt đèn lầu 1")) {
                updateBackground(false, imageViewPN, R.drawable.pn, R.drawable.pn_off);
                switchCompatPN.setChecked(false);
            } else if (command.contains("mở cửa") || command.contains("mở cổng")) {
                switchCompatCong.setChecked(true);
            } else if (command.contains("đóng cửa") || command.contains("đóng cổng")) {
                switchCompatCong.setChecked(false);
            } else if (command.contains("bật đèn ngoài sân")) {
                if (switchTC.isChecked()) {
                    Toast.makeText(this, "Chế độ tự động đang được bật, tắt chế độ tự động để thực hiện", Toast.LENGTH_SHORT).show();
                } else {
                    switchCompatNS.setChecked(true);
                }
            } else if (command.contains("tắt đèn ngoài sân")) {
                if (switchTC.isChecked()) {
                    Toast.makeText(this, "Chế độ tự động đang được bật, tắt chế độ tự động để thực hiện", Toast.LENGTH_SHORT).show();
                } else {
                    switchCompatNS.setChecked(false);
                }
            } else {
                // Command not recognized
                Toast.makeText(this, "Không hiểu lệnh! Vui lòng không nhập nhiều lệnh cùng một lúc", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }
}