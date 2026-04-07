package com.example.fpappfront.ui.launcher;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.fpappfront.R;

public class LauncherFragment extends Fragment {

    public LauncherFragment() {
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE);

        String token = prefs.getString("token", null);

        NavController navController = Navigation.findNavController(view);

        if (token != null) {
            navController.navigate(R.id.action_launcher_to_home);
        } else {
            navController.navigate(R.id.action_launcher_to_login);
        }
    }
}