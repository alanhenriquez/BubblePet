package com.xforce.bubblepet;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.xforce.bubblepet.helpers.ChangeActivity;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    /*-------------------------------------------------------------------------------*/
    /*Variables para texto, campos de texto y contenedores*/
    private String email;
    private String password;
    EditText etLoginPassword;
    EditText etLoginMail;
    TextView signUp;

    /*Acceso a Firebase y AwesomeValidation*/
    FirebaseAuth userAuth;
    FirebaseUser user;
    DatabaseReference userDataBase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userAuth = FirebaseAuth.getInstance();
        userDataBase = FirebaseDatabase.getInstance().getReference();
        user = userAuth.getCurrentUser();
        etLoginPassword = findViewById(R.id.txtPasswordLog);
        etLoginMail = findViewById(R.id.txtEmailLog);
        signUp = findViewById(R.id.btnSingupLogin);

        View btShowPass = findViewById(R.id.showPassword);
        View btResetText = findViewById(R.id.resetText);
        View btLogIn = findViewById(R.id.btnLoginUser);

        if (user != null){
            ChangeActivity.build(getApplicationContext(),MainActivity.class).start();
        }

        ShowPassword(btShowPass, etLoginPassword);
        ResetText(btResetText, etLoginMail);
        btLogIn.setOnClickListener(v -> {
            if (ValidarEmail(etLoginMail)){
                email = etLoginMail.getText().toString();
                password = etLoginPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    login();
                }
                else{
                    if (!email.isEmpty()){
                        msgToast("Ingrese la contraseña");
                    }else {
                        msgToast("Rellene los campos");
                    }
                }
            }
        });
        signUp.setOnClickListener(view -> {
            ChangeActivity.build(getApplicationContext(),SignUp.class).start();
        });

    }



    private void login (){
        userAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                ChangeActivity.build(getApplicationContext(),MainActivity.class).start();
            }else{
                String errorCode = ((FirebaseAuthException) Objects.requireNonNull(task.getException())).getErrorCode();
                dameToastdeerror(errorCode, etLoginMail, etLoginPassword);
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void ShowPassword (View elemTouch, EditText passwordToShow){
        elemTouch.setOnTouchListener((v, event) -> {

            switch ( event.getAction() ) {
                case MotionEvent.ACTION_DOWN:
                    passwordToShow.setInputType(InputType.TYPE_CLASS_TEXT);
                    break;
                case MotionEvent.ACTION_UP:
                    passwordToShow.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    break;
            }
            return true;
        });
    }

    private void ResetText (View elemTouch, EditText textToReset){
        elemTouch.setOnClickListener(view -> textToReset.setText(""));
    }

    private void msgToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }

    private void dameToastdeerror(String error, EditText mail, EditText password) {

        switch (error) {

            case "ERROR_INVALID_CUSTOM_TOKEN":
                msgToast("El formato del token personalizado es incorrecto. Por favor revise la documentación");
                break;

            case "ERROR_CUSTOM_TOKEN_MISMATCH":
                msgToast("El token personalizado corresponde a una audiencia diferente.");
                break;

            case "ERROR_INVALID_CREDENTIAL":
                msgToast("La credencial de autenticación proporcionada tiene un formato incorrecto o ha caducado.");
                break;

            case "ERROR_INVALID_EMAIL":
                msgToast("El correo electrónico no es correcto.");
                /*mail.setError("La dirección de correo electrónico está mal formateada.");*/
                mail.requestFocus();
                break;

            case "ERROR_WRONG_PASSWORD":
                msgToast("La contraseña no es correcta");
                /*msgToast("La contraseña no es válida o el usuario no tiene contraseña.");*/
                /*password.setError("la contraseña es incorrecta ");*/
                password.requestFocus();
                password.setText("");
                break;

            case "ERROR_USER_MISMATCH":
                msgToast("Las credenciales proporcionadas no corresponden al usuario que inició sesión anteriormente.");
                break;

            case "ERROR_REQUIRES_RECENT_LOGIN":
                msgToast("Esta operación es sensible y requiere autenticación reciente. Inicie sesión nuevamente antes de volver a intentar esta solicitud.");
                break;

            case "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL":
                msgToast("Ya existe una cuenta con la misma dirección de correo electrónico pero diferentes credenciales de inicio de sesión. Inicie sesión con un proveedor asociado a esta dirección de correo electrónico.");
                break;

            case "ERROR_EMAIL_ALREADY_IN_USE":
                msgToast("La dirección de correo electrónico ya está siendo utilizada por otra cuenta.");
                mail.requestFocus();
                break;

            case "ERROR_CREDENTIAL_ALREADY_IN_USE":
                msgToast("Esta credencial ya está asociada con una cuenta de usuario diferente.");
                break;

            case "ERROR_USER_DISABLED":
                msgToast("La cuenta de usuario ha sido inhabilitada por un administrador.");
                break;

            case "ERROR_USER_TOKEN_EXPIRED":
                msgToast("La credencial del usuario ha expirado. El usuario debe iniciar sesión nuevamente.");
                break;

            case "ERROR_USER_NOT_FOUND":
                msgToast("No hay registro de usuario.");
                break;

            case "ERROR_INVALID_USER_TOKEN":
                msgToast("La credencial del usuario ya no es válida. El usuario debe iniciar sesión nuevamente.");
                break;

            case "ERROR_OPERATION_NOT_ALLOWED":
                msgToast("Esta operación no está permitida. Debes habilitar este servicio en la consola.");
                break;

            case "ERROR_WEAK_PASSWORD":
                msgToast("La contraseña proporcionada no es válida.");
                /*password.setError("La contraseña no es válida, debe tener al menos 6 caracteres");*/
                password.requestFocus();
                break;

        }

    }

    private boolean ValidarEmail(EditText args) {
        // Patrón para validar el email
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");

        // El email a validar
        String email = args.getText().toString();
        Matcher mather = pattern.matcher(email);

        if (email.isEmpty()){
            args.requestFocus();
            msgToast("Ingrese un correo electronico");
            return false;
        }else {
            if (mather.find()) {
                /*El email ingresado es válido.*/
                return true;
            } else {
                msgToast("Su email ingresado es inválido.");
                return false;
            }
        }


    }


}