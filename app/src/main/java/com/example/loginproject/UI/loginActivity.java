package com.example.loginproject.UI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.loginproject.R;
import com.example.loginproject.UI.loginActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;

public class loginActivity extends AppCompatActivity {

    TextView lblCrearCuenta;
    EditText txtInputEmail, txtInputPassword;
    int RC_SIGN_IN = 1;
    Button btnLogin, btnGoogle;
    String TAG = "GoogleSignInLoginActivity";

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressBar;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtInputEmail = findViewById(R.id.inputEmail);
        txtInputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnlogin);
        lblCrearCuenta = findViewById(R.id.txtRegistrese);
        btnGoogle = findViewById(R.id.btnGoogle);

        lblCrearCuenta.setOnClickListener(view -> startActivity(new Intent(loginActivity.this, registerActivity.class)));

        btnLogin.setOnClickListener(view -> verificarCredenciales());

        btnGoogle.setOnClickListener(v -> signIn());

        mProgressBar = new ProgressDialog(loginActivity.this);

        // Configuración de Google SignIn
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Listener para el estado de autenticación
        mAuthStateListener = firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() != null) {
                Intent intentDashboard = new Intent(getApplicationContext(), MainActivity.class);
                intentDashboard.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intentDashboard);
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if (task.isSuccessful()) {
                try {
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    Log.w(TAG, "Google sign in failed", e);
                }
            } else {
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. " + task.getException().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "signInWithCredential:success");
                Intent dashboardActivity = new Intent(loginActivity.this, MainActivity.class);
                startActivity(dashboardActivity);
                loginActivity.this.finish();
            } else {
                Log.w(TAG, "signInWithCredential:failure", task.getException());
                Toast.makeText(loginActivity.this, "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void verificarCredenciales() {
        String email = txtInputEmail.getText().toString();
        String password = txtInputPassword.getText().toString();

        if (email.isEmpty() || !email.contains("@")) {
            showError(txtInputEmail, "Email no válido");
        } else if (password.isEmpty() || password.length() < 7) {
            showError(txtInputPassword, "Contraseña inválida");
        } else {
            mProgressBar.setTitle("Login");
            mProgressBar.setMessage("Iniciando sesión, espere un momento..");
            mProgressBar.setCanceledOnTouchOutside(false);
            mProgressBar.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                mProgressBar.dismiss(); // Ocultar ProgressBar

                if (task.isSuccessful()) {
                    Intent intent = new Intent(loginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "No se pudo iniciar Sesión, verifica el correo o contraseña", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }
}