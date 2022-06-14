package com.xforce.bubblepet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    /*-------------------------------------------------------------------------------*/
    /*Variables para texto, campos de texto y contenedores*/
    private EditText user;
    private EditText userName;
    private EditText userPassword;
    private String userString = " ";
    private String userNameString = " ";
    TextView signOut;
    View showPassword;

    //ImageView para la imagen de usuario
    Uri imageUri;
    View changeImageUser;//Boton para selecionar imagen
    ImageView contImageUser;//Contenedor con la imagen del usuario
    int SELECT_PICTURE = 200;// constant to compare the activity result code







    /*Acceso a Firebase y AwesomeValidation*/
    FirebaseAuth userAuth;
    DatabaseReference userDataBase;




    /*-------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);





        /* Acceso a Instancias FireBase y a la AwesomeValidacion
         * Estos accesos los encontraras en el build.gradle tanto de proyecto como app*/
        userAuth = FirebaseAuth.getInstance();
        userDataBase = FirebaseDatabase.getInstance().getReference();




        /*Simples variables antes definidas accediendo a los id*/
        user = findViewById(R.id.userEditProfile);
        userName = findViewById(R.id.userNameEditProfile);
        userPassword = findViewById(R.id.userPasswordEditProfile);
        TextView saveDatosButton = findViewById(R.id.botonGuardarDatosEditProfile);
        TextView eraseCountButton = findViewById(R.id.botonGuardarPasswordEditProfile);
        signOut = findViewById(R.id.botonCerrarSesionEditProfile);
        showPassword = findViewById(R.id.showPassword);

        changeImageUser = findViewById(R.id.selectImageEditProfile);
        contImageUser = findViewById(R.id.imgPhotoUserEditProfile);





        /*Botones y acciones*/
        getData();/*Carga previa de los datos*/
        ShowPassword(showPassword,userPassword);/*Mostramos la contraseña al presionar*/
        saveDatosButton.setOnClickListener(v ->{

            getString();
            setDataBase();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        });/*Actualizamos los datos del perfil*/
        eraseCountButton.setOnClickListener(v ->{

            getString();
            DeleteUser();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        });/*Actualizamos la contraseña*/
        changeImageUser.setOnClickListener(v ->{
            msgToast("Selecciona tu imagen");

            openGallery();


        });/*Elegimos la nueva imagen de usuario*/
        signOut.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            CerrarSesion();
        });/*Registrarse si no tienes cuenta*/
    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    /*-------------------------------------------------------------------------------*/





    /*--------------------*/
    /*Codigo de la seleccion de imagen y envio a la base de datos*/
    private void openGallery(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it with the returned requestCode
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE) {
            imageUri = data.getData();
            contImageUser.setImageURI(imageUri);

            String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
            StorageReference folder = FirebaseStorage.getInstance().getReference().child("Users").child(id);
            final StorageReference file_name = folder.child(imageUri.getLastPathSegment());
            file_name.putFile(imageUri).addOnSuccessListener(taskSnapshot ->
                    file_name.getDownloadUrl().addOnSuccessListener(uri -> {

                        //Enviamos a la base de datos la url de la imagen
                        setDataImageBase(String.valueOf(uri));
                        msgToast("Se subio correctamente");



                    }));


        }
    }

    /*Agregamos la Url de la imagen a la base de datos*/
    private void setDataImageBase(String link){
        Map<String, Object> data = new HashMap<>();
        data.put("ImageMain", link);
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).child("ImageData").child("imgPerfil").setValue(data).addOnCompleteListener(task1 -> msgToast("Datos actualizados"));

    }
    /*Termina codigo de la seleccion de imagen y envio a la base de datos*/
    /*--------------------*/





    /*Cerramos la sesion y volvemos al login*/
    private void CerrarSesion (){
        Intent loged = new Intent(getApplicationContext(), Login.class);
        loged.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(loged);
        finish();
    }



    /*Convertimos a string el contenido de los campos de texto*/
    private void getString(){
        userString = user.getText().toString();
        userNameString = userName.getText().toString();
        String userPasswordString = userPassword.getText().toString();
    }



    /*Agregamos la informacion a la base de datos*/
    private void setDataBase(){
        Map<String, Object> data = new HashMap<>();
        data.put("user", userString);
        data.put("userName", userNameString);

        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).child("PerfilData").setValue(data).addOnCompleteListener(task1 -> msgToast("Datos actualizados"));
    }



    /*Eliminamos al usuario*/
    private void DeleteUser() {
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String mail;
                    String password;

                    /*-----------------*/
                    /*Obtenemos los valores del usuario convertidos a cadena*/
                    mail = Objects.requireNonNull(snapshot.child("CountData").child("userMail").getValue()).toString();
                    password = Objects.requireNonNull(snapshot.child("CountData").child("userPassword").getValue()).toString();


                    /*-----------------*/
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    // Obtenga las credenciales de autenticación del usuario para volver a autenticarse. El siguiente ejemplo muestra
                    // credenciales de correo electrónico y contraseña, pero hay múltiples proveedores posibles,
                    // como GoogleAuthProvider o FacebookAuthProvider.
                    AuthCredential credential = EmailAuthProvider.getCredential(mail, password);
                    // Pida al usuario que vuelva a proporcionar sus credenciales de inicio de sesión
                    if (user != null) {
                        user.reauthenticate(credential).addOnCompleteListener(task ->
                                user.delete().addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Intent intent = new Intent(getApplicationContext(), Login.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }));
                    }


                    /*-----------------*/
                    /*Removemos la informacion del usuario desde la base de datos*/
                    userDataBase.child("Users").child(id).removeValue();


                }else {
                    msgToast("Creación de usuario cancelado");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                msgToast("Error de carga");
            }
        });
    }



    /*Funcion getData que obtiene los datos desde Firebase base de datos*/
    private void getData (){
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String val;

                    /*-----------------*/
                    val = Objects.requireNonNull(snapshot.child("PerfilData").child("user").getValue()).toString();
                    user = findViewById(R.id.userEditProfile);
                    user.setText(val);

                    /*-----------------*/
                    val = Objects.requireNonNull(snapshot.child("PerfilData").child("userName").getValue()).toString();
                    userName = findViewById(R.id.userNameEditProfile);
                    userName.setText(val);


                    /*-----------------*/
                    val = Objects.requireNonNull(snapshot.child("CountData").child("userPassword").getValue()).toString();
                    userPassword = findViewById(R.id.userPasswordEditProfile);
                    userPassword.setText(val);

                    /*-----------------*/
                    val = Objects.requireNonNull(snapshot.child("ImageData").child("imgPerfil").child("ImageMain").getValue()).toString();
                    ImageView userImageProfile = findViewById(R.id.imgPhotoUserEditProfile);
                    Glide.with(getApplicationContext()).load(val).into(userImageProfile);


                }else {
                    msgToast("Error");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                msgToast("Error de carga");
            }
        });
    }



    /*Mostramos la contraseña del campo de texto*/
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



    /*Variable para generar el mensaje Toast*/
    private void msgToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }




    /*-------------------------------------------------------------------------------*/
}