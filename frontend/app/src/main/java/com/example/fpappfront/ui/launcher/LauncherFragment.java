package com.example.fpappfront.ui.launcher;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.fpappfront.R;

public class LauncherFragment extends Fragment {

    public LauncherFragment() {
        super(R.layout.fragment_launcher);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String token = requireContext()
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("token", null);

        if (token != null) {
            Navigation.findNavController(view)
                    .navigate(R.id.homeFragment);
        } else {
            Navigation.findNavController(view)
                    .navigate(R.id.loginFragment);
        }
    }
}