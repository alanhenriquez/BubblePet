package com.xforce.bubblepet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.xforce.bubblepet.helpers.ChangeActivity;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditPetProfile extends AppCompatActivity {
    /*-------------------------------------------------------------------------------*/
    /*Variables para texto, campos de texto y contenedores*/
    private EditText petName;
    private EditText petAge;
    private EditText petColor;
    private EditText petBreed;
    private EditText petHealth;
    private String petNameString = " ";
    private String petEgeString = " ";
    private String petColorString = " ";
    private String petBreedString = " ";
    private String petHealthString = " ";

    //ImageView para la imagen de usuario
    Uri imageUri;
    View changeImageUser;//Boton para selecionar imagen
    ImageView contImageUser;//Contenedor con la imagen del usuario
    int SELECT_PICTURE = 200;// constant to compare the activity result code

    /*Acceso a Firebase*/
    FirebaseAuth userAuth;
    DatabaseReference userDataBase;





    /*-------------------------------------------------------------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet_profile);





        /* Acceso a Instancias FireBase
         * Estos accesos los encontraras en el build.gradle tanto de proyecto como app*/
        userAuth = FirebaseAuth.getInstance();
        userDataBase = FirebaseDatabase.getInstance().getReference();





        /*Simples variables antes definidas accediendo a los id*/
        petName = findViewById(R.id.petName);
        petAge = findViewById(R.id.petEdad);
        petColor = findViewById(R.id.petColor);
        petBreed = findViewById(R.id.petRaza);
        petHealth = findViewById(R.id.petEstado);
        View btResetTextName = findViewById(R.id.resetText1);
        View btResetTextEge = findViewById(R.id.resetText2);
        View btResetTextColor = findViewById(R.id.resetText3);
        View btResetTextBreed = findViewById(R.id.resetText4);
        View btResetTextHealth = findViewById(R.id.resetText5);
        TextView saveDatosButton = findViewById(R.id.btSignUpFinish2);
        changeImageUser = findViewById(R.id.selectImageEditProfile);
        contImageUser = findViewById(R.id.imgPhotoUserEditProfile);




        /*Botones y acciones*/
        getData();/*Carga previa de los datos*/
        saveDatosButton.setOnClickListener(v ->{

            petNameString = petName.getText().toString();
            petEgeString = petAge.getText().toString();
            petColorString = petColor.getText().toString();
            petBreedString = petBreed.getText().toString();
            petHealthString = petHealth.getText().toString();

            Map<String, Object> data = new HashMap<>();
            if (petNameString != null){
                data.put("petName", petNameString);
            }
            if (petEgeString != null){
                data.put("petEge", petEgeString);
            }
            if (petColorString != null){
                data.put("petColor", petColorString);
            }
            if (petBreedString != null){
                data.put("petBreed", petBreedString);
            }
            if (petHealthString != null){
                data.put("petHealth", petHealthString);
            }

            String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
            userDataBase.child("Users").child(id).child("PetData").setValue(data).addOnCompleteListener(task1 -> {

                msgToast("Datos actualizados");

            });
            ChangeActivity.build(getApplicationContext(),MainActivity.class).start();

        });/*Actualizamos los datos del perfil*/
        ResetText(btResetTextName,petName);/*Reiniciamos el texto*/
        ResetText(btResetTextEge,petAge);/*Reiniciamos el texto*/
        ResetText(btResetTextColor,petColor);/*Reiniciamos el texto*/
        ResetText(btResetTextBreed,petBreed);/*Reiniciamos el texto*/
        ResetText(btResetTextHealth,petHealth);/*Reiniciamos el texto*/
        changeImageUser.setOnClickListener(v ->{

            String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
            userDataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String val;

                        if (snapshot.hasChild("PetData")){

                            /*-----------------*/
                            if (snapshot.child("PetData").hasChild("imgPetPerfil")){
                                msgToast("Insertado");
                                Intent i = new Intent();
                                i.setType("image/*");
                                i.setAction(Intent.ACTION_GET_CONTENT);
                                // pass the constant to compare it with the returned requestCode
                                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
                            }
                            else {
                                msgToast("Agregado");
                                Map<String, Object> data = new HashMap<>();
                                data.put("ImageMain", " ");
                                String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
                                userDataBase.child("Users").child(id).child("PetData").child("imgPetPerfil").setValue(data).addOnCompleteListener(task1 -> msgToast("Datos actualizados"));
                                CerrarSesion();
                            }
                        }
                        else {
                            if (
                                    petName.getText().toString().isEmpty() &&
                                    petAge.getText().toString().isEmpty() &&
                                    petColor.getText().toString().isEmpty() &&
                                    petBreed.getText().toString().isEmpty() &&
                                    petHealth.getText().toString().isEmpty()
                            ){
                                msgToast("Primero rellene los demas campos");
                                petName.requestFocus();
                            }else {
                                msgToast("Primero guarde los datos");
                            }
                        }

                    }else {
                        msgToast("Error");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    msgToast("Error de carga");
                }
            });

        });/*Elegimos la nueva imagen de usuario*/


    }

    @Override public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
    /*-------------------------------------------------------------------------------*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == SELECT_PICTURE) {


            imageUri = data.getData();
            contImageUser.setImageURI(imageUri);

            // Code for showing progressDialog while uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Subiendo...");
            progressDialog.show();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setOnCancelListener(new Dialog.OnCancelListener() {
                @Override public void onCancel(DialogInterface dialog) {
                    // DO SOME STUFF HERE
                }
            });

            String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
            StorageReference folder = FirebaseStorage.getInstance().getReference().child("Users").child(id);
            final StorageReference file_name = folder.child(imageUri.getLastPathSegment());
            file_name.putFile(imageUri).addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()
                        / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Exportando al " + (int)progress + "%");
            }).addOnSuccessListener(taskSnapshot -> file_name.getDownloadUrl().addOnSuccessListener(uri -> {
                //Enviamos a la base de datos la url de la imagen
                setDataImageBase(String.valueOf(uri));
                progressDialog.dismiss();
                msgToast("Se subio correctamente");
            })).addOnFailureListener(e -> {
                // Error, Image not uploaded
                progressDialog.dismiss();
                msgToast("Error en la carga " + e.getMessage());
            });


        }
    }



    /*Agregamos la Url de la imagen a la base de datos*/
    private void setDataImageBase(String link){
        Map<String, Object> data = new HashMap<>();
        data.put("ImageMain", link);
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).child("PetData").child("imgPetPerfil").setValue(data).addOnCompleteListener(task1 -> msgToast("Datos actualizados"));
        CerrarSesion();
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

    /*Funcion getData que obtiene los datos desde Firebase base de datos*/
    private void getData (){
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String val;

                    if (snapshot.hasChild("PetData")){
                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petName")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petName").getValue()).toString();
                            petName = findViewById(R.id.petName);
                            petName.setText(val);
                        }


                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petEge")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petEge").getValue()).toString();
                            petAge = findViewById(R.id.petEdad);
                            petAge.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petColor")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petColor").getValue()).toString();
                            petColor = findViewById(R.id.petColor);
                            petColor.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petBreed")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petBreed").getValue()).toString();
                            petBreed = findViewById(R.id.petRaza);
                            petBreed.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petHealth")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petHealth").getValue()).toString();
                            petHealth = findViewById(R.id.petEstado);
                            petHealth.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("imgPetPerfil")){
                            val = Objects.requireNonNull(snapshot.child("PetData").child("imgPetPerfil").child("ImageMain").getValue()).toString();
                            contImageUser = findViewById(R.id.imgPhotoPet);
                            Glide.with(getApplicationContext()).load(val).into(contImageUser);
                        }
                    }
                    else {
                        msgToast("Crear perfil de mascota");
                    }

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

    /*Reiniciamos el texto del campo de texto*/
    private void ResetText (View elemTouch, EditText textToReset){
        elemTouch.setOnClickListener(view -> textToReset.setText(""));
    }

    /*Variable para generar el mensaje Toast*/
    private void msgToast(String message) {
        Toast.makeText(getApplicationContext(),message, Toast.LENGTH_LONG).show();
    }


    /*-------------------------------------------------------------------------------*/
}