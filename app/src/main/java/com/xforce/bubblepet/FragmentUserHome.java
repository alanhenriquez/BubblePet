package com.xforce.bubblepet;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentUserHome#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentUserHome extends Fragment {

    /*Importantes de usar aqui:
     *
     * - Variables
     * - Iniciar variables en el metodo onCreat
     * - Acceder a los elementos por sus id mediante el metodo onCreateView
     * - Funcion getData que obtiene los datos desde Firebase*/

    /*-------------------------------------------------------------------------------*/
    /* - Variables*/
    /*Variables para texto, campos de texto y contenedores*/
    private TextView user;
    private TextView userMail;
    private TextView petName;
    private TextView petAge;
    private TextView petColor;
    private TextView petBreed;
    private TextView petHealth;

    private ImageView userImageProfile;
    private ImageView petImageProfile;

    /*Acceso a Firebase y AwesomeValidation*/
    FirebaseAuth userAuth;
    DatabaseReference userDataBase;



    /*-------------------------------------------------------------------------------*/
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FragmentUserHome() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentUserHome.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentUserHome newInstance(String param1, String param2) {
        FragmentUserHome fragment = new FragmentUserHome();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


        userAuth = FirebaseAuth.getInstance();
        userDataBase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_home, container, false);


        getData(view);
        return view;
    }
    /*-------------------------------------------------------------------------------*/





    /*Funcion getData que obtiene los datos desde Firebase base de datos*/
    private void getData (View v){
        String id = Objects.requireNonNull(userAuth.getCurrentUser()).getUid();
        userDataBase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String val;

                    if (snapshot.hasChild("PerfilData")) {
                        val = Objects.requireNonNull(snapshot.child("PerfilData").child("user").getValue()).toString();
                        user = v.findViewById(R.id.userName);
                        user.setText(val);
                    }

                    /*-----------------*/
                    if (snapshot.hasChild("CountData")) {
                        val = Objects.requireNonNull(snapshot.child("CountData").child("userMail").getValue()).toString();
                        userMail = v.findViewById(R.id.userMail);
                        userMail.setText(val);
                    }

                    /*-----------------*/
                    if (snapshot.hasChild("ImageData")){
                        val = Objects.requireNonNull(snapshot.child("ImageData").child("imgPerfil").child("ImageMain").getValue()).toString();
                        userImageProfile = v.findViewById(R.id.imgPhoto);
                        Glide.with(v).load(val).into(userImageProfile);
                    }


                    if (snapshot.hasChild("PetData")) {

                        if (snapshot.child("PetData").hasChild("petName")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petName").getValue()).toString();
                            petName = v.findViewById(R.id.text_targeta_pet_content_info_1);
                            petName.setText(val);
                        }


                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petEge")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petEge").getValue()).toString();
                            petAge = v.findViewById(R.id.text_targeta_pet_content_info_2);
                            petAge.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petColor")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petColor").getValue()).toString();
                            petColor = v.findViewById(R.id.text_targeta_pet_content_info_3);
                            petColor.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petBreed")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petBreed").getValue()).toString();
                            petBreed = v.findViewById(R.id.text_targeta_pet_content_info_4);
                            petBreed.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("petHealth")) {
                            val = Objects.requireNonNull(snapshot.child("PetData").child("petHealth").getValue()).toString();
                            petHealth = v.findViewById(R.id.text_targeta_pet_content_info_5);
                            petHealth.setText(val);
                        }

                        /*-----------------*/
                        if (snapshot.child("PetData").hasChild("imgPetPerfil")){
                            val = Objects.requireNonNull(snapshot.child("PetData").child("imgPetPerfil").child("ImageMain").getValue()).toString();
                            petImageProfile = v.findViewById(R.id.imagePet);
                            Glide.with(v).load(val).into(petImageProfile);
                        }

                    }


                }else {
                    msgToast("Error",v);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                msgToast("Error de carga",v);
            }
        });
    }



    /*Importante para importar la interfaz del Fragment. No eliminar*/
    public interface OnFragmentInteractionListener {

    }



    /*Variable para generar el mensaje Toast*/
    private void msgToast(String message, View v) {
        Toast.makeText(v.getContext(),message, Toast.LENGTH_LONG).show();
    }
}