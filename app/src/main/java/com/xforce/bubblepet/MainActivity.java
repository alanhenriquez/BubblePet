package com.xforce.bubblepet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import com.xforce.bubblepet.helpers.ChangeActivity;

public class MainActivity extends AppCompatActivity implements
        FragmentUserHome.OnFragmentInteractionListener,
        FragmentMenu.OnFragmentInteractionListener {

    FragmentUserHome fragmentUserHome;
    FragmentMenu fragmentMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentUserHome = new FragmentUserHome();
        fragmentMenu = new FragmentMenu();
        getSupportFragmentManager().beginTransaction().add(R.id.Fragments,fragmentUserHome).commit();

    }


    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (view.getId()){
            case R.id.editButton:
                ChangeActivity.build(getApplicationContext(),EditPetProfile.class).start();
                break;
            case R.id.editPerfilButton:
                ChangeActivity.build(getApplicationContext(),EditProfile.class).start();
                break;
            case R.id.menuButton:
                transaction.replace(R.id.Fragments,fragmentMenu);
                break;
            case R.id.userHomeButton:
                transaction.replace(R.id.Fragments,fragmentUserHome);
                break;
        }
        transaction.commit();
    }

}