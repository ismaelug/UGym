package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myapplication.model.Reservation;
import com.example.myapplication.model.ScheduleItem;

import java.util.ArrayList;
import java.util.UUID;

import java.util.Date;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class QRCodeActivity extends AppCompatActivity {
    private static String urlHost = "https://ug-gym.link/viewqr.php";
    private String reservationUser, reservationCreatingDate, reservationDate;
    private int numReservation;

    private Button buttonSaveQRCode;
    private Bitmap bitmapQRCode;

    private ImageView imageViewQRCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        imageViewQRCode = (ImageView) findViewById(R.id.imageViewQRCode);
        buttonSaveQRCode = (Button) findViewById(R.id.buttonSaveQRCode);
        buttonSaveQRCode.setOnClickListener(buttonSaveQRCodeClickEvent());

        String urlQRCode = getUrlQrCode();

        QRGEncoder qrgEncoder = new QRGEncoder(urlQRCode, QRGContents.Type.TEXT, 50);
        try {
            bitmapQRCode = qrgEncoder.getBitmap();
            // Setting Bitmap to ImageView
            imageViewQRCode.setImageBitmap(bitmapQRCode);
        }catch (Exception e){
            showAlertDialog("Error", e.toString(), SweetAlertDialog.ERROR_TYPE);
        }
    }

    private String getUrlQrCode () {
        reservationUser = getIntent().getExtras().getString("reservationUser");
        reservationCreatingDate = getIntent().getExtras().getString("reservationCreatingDate");
        reservationDate = getIntent().getExtras().getString("reservationDate");
        numReservation = getIntent().getExtras().getInt("numReservation");

        String urlForQrCode = urlHost + "?reservationCreatingDate=" + reservationUser +
                            "&reservationUser=" + reservationUser +
                            "&reservationDate=" + reservationUser +
                            "&numReservation" + numReservation;

        for(int scheduleIndex = 0; scheduleIndex < numReservation; scheduleIndex = scheduleIndex + 1){
            int currentIndex = scheduleIndex + 1;
            String reservationStartTime = getIntent().getExtras().getString("reservationEndTime"+currentIndex);
            String reservationEndTime = getIntent().getExtras().getString("reservationEndTime"+currentIndex);
            urlForQrCode = urlForQrCode + "&reservationStartTime" + currentIndex + "=" + reservationStartTime +
                            "&reservationEndTime" + currentIndex + "=" + reservationEndTime;
        }

        return urlForQrCode;
    }

    private void showAlertDialog (String title, String message, int icon) {
        SweetAlertDialog sweetAlert = new SweetAlertDialog(QRCodeActivity.this, icon);
        sweetAlert.setTitleText(title);
        sweetAlert.setContentText(message);
        sweetAlert.show();
    }

    private View.OnClickListener buttonSaveQRCodeClickEvent () {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(bitmapQRCode == null) {
                    showAlertDialog("Error", "No se pudo encontrar el código QR", SweetAlertDialog.ERROR_TYPE);
                    return;
                }

                try {
                    String randomString = UUID.randomUUID().toString();
                    QRGSaver qrgSaver = new QRGSaver();
                    qrgSaver.save(Environment.getExternalStorageDirectory().getPath()+"/Download/", "QRImage_" + randomString, bitmapQRCode, QRGContents.ImageType.IMAGE_JPEG);
                    showAlertDialog("Código QR Guardado", "Se guardó el código Qr en la carpeta de Descargas.", SweetAlertDialog.SUCCESS_TYPE);
                }catch (Exception error) {
                    showAlertDialog("Error", "No se pudo guardar el código QR", SweetAlertDialog.ERROR_TYPE);
                }
            }
        };
    }
}