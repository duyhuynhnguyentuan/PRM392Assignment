package com.example.prm392assignment.ui;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prm392assignment.R;
import com.example.prm392assignment.ui.MainActivity;
import com.example.prm392assignment.ui.MapActivity;
import com.example.prm392assignment.ui.ProfileActivity;
import com.example.prm392assignment.ui.adapter.MessageAdapter;
import com.example.prm392assignment.utility.SharedPrefManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ChatActivity extends AppCompatActivity implements TextWatcher {
    private WebSocket webSocket;
    private String SERVER_PATH = "https://nodejs-ecommerce-api-prm.onrender.com";
    private EditText messageEdit;
    private View sendBtn, pickImgBtn;
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            try {
                                InputStream is = getContentResolver().openInputStream(result.getData().getData());
                                Bitmap image = BitmapFactory.decodeStream(is);
                                sendImage(image);
                            } catch (FileNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 10);

        initiateSocketConnection();
        setupBottomNavigation();
        initializeView();

        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void initiateSocketConnection() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(SERVER_PATH).build();
        webSocket = client.newWebSocket(request, new SocketListener());
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnApplyWindowInsetsListener(null);
        bottomNavigationView.setPadding(0, 0, 0, 0);
        bottomNavigationView.setSelectedItemId(R.id.bottom_chat);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.bottom_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_map) {
                startActivity(new Intent(getApplicationContext(), MapActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            } else if (itemId == R.id.bottom_chat) {
                return true;
            } else if (itemId == R.id.bottom_profile) {
                startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                finish();
                return true;
            }
            return false;
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {
        String string = s.toString().trim();
        if (string.isEmpty()) {
            resetMessageEdit();
        } else {
            sendBtn.setVisibility(View.VISIBLE);
            pickImgBtn.setVisibility(View.INVISIBLE);
        }
    }

    private void resetMessageEdit() {
        messageEdit.removeTextChangedListener(this);
        messageEdit.setText("");
        sendBtn.setVisibility(View.INVISIBLE);
        pickImgBtn.setVisibility(View.VISIBLE);
        messageEdit.addTextChangedListener(this);
    }

    private class SocketListener extends WebSocketListener {
        @Override
        public void onMessage(@NonNull WebSocket webSocket, @NonNull String text) {
            super.onMessage(webSocket, text);
            runOnUiThread(() -> {
                try {
                    JSONObject jsonObject = new JSONObject(text);
                    jsonObject.put("isSent", false);
                    messageAdapter.addItem(jsonObject);
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @Override
        public void onOpen(@NonNull WebSocket webSocket, @NonNull Response response) {
            super.onOpen(webSocket, response);
            runOnUiThread(() -> Toast.makeText(ChatActivity.this, "Socket connection successful", Toast.LENGTH_SHORT).show());
        }
    }

    private void initializeView() {
        messageEdit = findViewById(R.id.messageEdit);
        sendBtn = findViewById(R.id.sendButton);
        pickImgBtn = findViewById(R.id.pickImgBtn);
        recyclerView = findViewById(R.id.recyclerView);

        messageAdapter = new MessageAdapter(getLayoutInflater());
        recyclerView.setAdapter(messageAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageEdit.addTextChangedListener(this);
        sendBtn.setOnClickListener(v -> {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("name", SharedPrefManager.getInstance(getApplicationContext()).getEmail());
                jsonObject.put("message", messageEdit.getText().toString());
                webSocket.send(jsonObject.toString());
                jsonObject.put("isSent", true);
                messageAdapter.addItem(jsonObject);
                recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
                resetMessageEdit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        pickImgBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            pickImageLauncher.launch(Intent.createChooser(intent, "Pick image"));
        });
    }

    private void sendImage(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        String base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", SharedPrefManager.getInstance(getApplicationContext()).getEmail());
            jsonObject.put("image", base64String);
            webSocket.send(jsonObject.toString());
            jsonObject.put("isSent", true);
            messageAdapter.addItem(jsonObject);
            recyclerView.smoothScrollToPosition(messageAdapter.getItemCount() - 1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
}
