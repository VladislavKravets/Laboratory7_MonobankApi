package com.example.laboratory7retrofit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.OAuthProvider;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;

    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Получение объекта GoogleSignInClient
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Получение объекта FirebaseAuth
        mAuth = FirebaseAuth.getInstance();


        // Отображение кнопки для аутентификации через Google
        Button signGoogleInButton = findViewById(R.id.loginGoogle_btn);
        signGoogleInButton.setOnClickListener(view -> loginWithGoogle());

        // Отображение кнопки для аутентификации через Yahoo
        Button signYahooInButton = findViewById(R.id.loginYahoo_btn);
        signYahooInButton.setOnClickListener(view -> loginWithYahoo());
    }

    // формируем url для google
    private void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // формируем url для yahoo
    private void loginWithYahoo() {
        signInWithYahooAuthProvider(OAuthProvider.newBuilder("yahoo.com")
                .addCustomParameter("prompt", "login")
                .addCustomParameter("language", "en")
                .setScopes(
                        new ArrayList<String>(){
                            {
                                add("email");
                                add("profile");
                            }
                        }
                )
                .build()
        );
    }

    // логика авторизация yahoo
    private void signInWithYahooAuthProvider(OAuthProvider provider) {
        Task<AuthResult> yahooPendingTaskResult = mAuth.getPendingAuthResult();
        if(yahooPendingTaskResult != null) {
            yahooPendingTaskResult.addOnCompleteListener(task ->
                    Toast.makeText(LoginActivity.this, "Успішно.", Toast.LENGTH_SHORT)
                            .show())
                    .addOnFailureListener(e ->
                            Toast.makeText(LoginActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show()
                    );
        }else{
            mAuth.startActivityForSignInWithProvider(this,provider)
                    .addOnFailureListener(e ->
                            Toast.makeText(LoginActivity.this, e.getMessage(),
                                    Toast.LENGTH_SHORT).show())
                    .addOnSuccessListener(authResult -> {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            });
        }
    }

    // гугл авторизация
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Аутентификация в Firebase с помощью учетных данных Google
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                // Аутентификация прошла успешно, переход на следующую активность
                                Intent intent = new Intent(this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // Обработка ошибок при аутентификации
                                Toast.makeText(LoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e) {
                // Обработка ошибок при выборе учетной записи Google
                Toast.makeText(LoginActivity.this,
                        "Google sign in failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}