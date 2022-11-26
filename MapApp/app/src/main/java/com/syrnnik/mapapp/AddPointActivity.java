package com.syrnnik.mapapp;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class AddPointActivity extends AppCompatActivity {

    //    EditText address;
    EditText title;
    EditText desc;

    boolean fieldsError;
    String dialogMsg;
    EditText focusField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_point);
        setTitle(R.string.activity_addPoint_title);

    }

    public void addToiletPoint(View view) {
//        address = findViewById(R.id.address);
        title = findViewById(R.id.pointTitle);
        desc = findViewById(R.id.pointDesc);
        fieldsError = false;
        dialogMsg = "";
        focusField = null;

//        if (address.getText().toString().isEmpty()) {
//            fieldsError = true;
//            dialogMsg = getResources().getString(R.string.address_error_msg);
//            focusField = address;
//        } else
        if (title.getText().toString().isEmpty()) {
            fieldsError = true;
            dialogMsg = getResources().getString(R.string.title_error_msg);
            focusField = title;
        }
//        else if (desc.getText().toString().isEmpty()) {
//            fieldsError = true;
//            dialogMsg = "Обязательное поле 'Описание' не заполнено";
//            focusField = desc;
//        }

        if (fieldsError) {
            MainActivity.sendAlert(
                    this, getResources().getString(R.string.error),
                    dialogMsg,
                    null, null,
                    getResources().getString(R.string.ok),
                    (dialogInterface, i) -> focusField.requestFocus(),
                    android.R.drawable.ic_dialog_alert);
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, ServerUrls.ADD_POINT, null,
                response -> {
//                    Log.e(MainActivity.TAG, "Added successfully");
                    MainActivity.showMsg(this, getResources().getString(R.string.added));
                },
                error -> {
                    try {
                        MainActivity.checkErrors(this, error);
                    } catch (JSONException | IOException jsonException) {
                        jsonException.printStackTrace();
                    }
                }
        ) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
//                headers.put("key", MainActivity.REQ_TOKEN);
//                headers.put("address", MainActivity.userCity + " " + address.getText().toString());
                headers.put("lat", Objects.requireNonNull(MainActivity.userCoords.get("lat")).toString());
                headers.put("lon", Objects.requireNonNull(MainActivity.userCoords.get("lon")).toString());
                headers.put("title", title.getText().toString());
                headers.put("desc", desc.getText().toString());
                return headers;
            }
        };
        request.setTag(MainActivity.TAG);
        MainActivity.queue.add(request);

        closeAddPoint(view);
    }

    public void closeAddPoint(View view) {
        finish();
    }

}