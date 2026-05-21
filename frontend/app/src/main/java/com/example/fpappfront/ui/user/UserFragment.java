package com.example.fpappfront.ui.user;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.fpappfront.R;
import com.example.fpappfront.data.model.UpdateUserRequest;
import com.example.fpappfront.utils.ViewUtils;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;

public class UserFragment extends Fragment {

    private TextView tvUsername;
    private TextView tvEmail;

    private EditText etUsername;
    private EditText etEmail;

    private EditText etOldPassword;
    private EditText etNewPassword;
    private EditText etConfirmPassword;

    private Button btnEditUsername;
    private Button btnSaveUsername;
    private Button btnCancelUsername;

    private Button btnEditEmail;
    private Button btnSaveEmail;
    private Button btnCancelEmail;

    private Button btnChangePassword;
    private Button btnSavePassword;
    private Button btnCancelPassword;

    private Button btnLogout;
    private Button btnDeleteAccount;

    private LinearLayout layoutUsernameEdit;
    private LinearLayout layoutEmailEdit;
    private LinearLayout layoutPasswordEdit;

    private UserViewModel viewModel;

    private int userId;
    private String token;

    public UserFragment() {
        super(R.layout.fragment_user);
    }

    @Override
    public void onViewCreated(
            @NonNull View view,
            @Nullable Bundle savedInstanceState
    ) {

        super.onViewCreated(view, savedInstanceState);

        SharedPreferences prefs =
                requireContext().getSharedPreferences(
                        "auth",
                        Context.MODE_PRIVATE
                );

        token = prefs.getString("token", null);

        userId = prefs.getInt("user_id", -1);

        viewModel = new ViewModelProvider(this)
                .get(UserViewModel.class);

        initViews(view);

        observeViewModel(view);

        setupListeners(view);

        viewModel.loadUser(userId, token);
    }

    private void initViews(View view) {

        tvUsername = view.findViewById(R.id.tvUsername);
        tvEmail = view.findViewById(R.id.tvEmail);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);

        etOldPassword = view.findViewById(R.id.etOldPassword);
        etNewPassword = view.findViewById(R.id.etNewPassword);
        etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        btnEditUsername = view.findViewById(R.id.btnEditUsername);
        btnSaveUsername = view.findViewById(R.id.btnSaveUsername);
        btnCancelUsername = view.findViewById(R.id.btnCancelUsername);

        btnEditEmail = view.findViewById(R.id.btnEditEmail);
        btnSaveEmail = view.findViewById(R.id.btnSaveEmail);
        btnCancelEmail = view.findViewById(R.id.btnCancelEmail);

        btnChangePassword = view.findViewById(R.id.btnChangePassword);
        btnSavePassword = view.findViewById(R.id.btnSavePassword);
        btnCancelPassword = view.findViewById(R.id.btnCancelPassword);

        btnLogout = view.findViewById(R.id.btnLogout);
        btnDeleteAccount = view.findViewById(R.id.btnDeleteAccount);

        layoutUsernameEdit = view.findViewById(R.id.layoutUsernameEdit);
        layoutEmailEdit = view.findViewById(R.id.layoutEmailEdit);
        layoutPasswordEdit = view.findViewById(R.id.layoutPasswordEdit);
    }

    private void observeViewModel(View view) {

        viewModel.getUsername().observe(
                getViewLifecycleOwner(),
                username -> {

                    tvUsername.setText(username);

                    if (!etUsername.hasFocus()) {
                        etUsername.setText(username);
                    }
                });

        viewModel.getEmail().observe(
                getViewLifecycleOwner(),
                email -> {

                    tvEmail.setText(email);

                    if (!etEmail.hasFocus()) {
                        etEmail.setText(email);
                    }
                });

        viewModel.getMessage().observe(
                getViewLifecycleOwner(),
                msg -> {

                    if (msg != null) {

                        Snackbar.make(
                                view,
                                msg,
                                Snackbar.LENGTH_LONG
                        ).show();
                    }
                });
        viewModel.getMessage().observe(getViewLifecycleOwner(), msg -> {

            handleError(msg, view);
        });
    }

    private void setupListeners(View view) {

        setupUsernameSection();

        setupEmailSection();

        setupPasswordSection(view);

        setupLogout(view);

        setupDeleteAccount(view);
    }

    private void setupUsernameSection() {

        btnEditUsername.setOnClickListener(v -> {

            layoutUsernameEdit.setVisibility(View.VISIBLE);

            btnEditUsername.setVisibility(View.GONE);
        });

        btnCancelUsername.setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), v);
            layoutUsernameEdit.setVisibility(View.GONE);
            btnEditUsername.setVisibility(View.VISIBLE);
            etUsername.setText(tvUsername.getText().toString());
        });

        btnSaveUsername.setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), v);

            UpdateUserRequest request = new UpdateUserRequest();
            request.username = etUsername.getText().toString().trim();

            viewModel.updateUser(userId, token, request);
            layoutUsernameEdit.setVisibility(View.GONE);
            btnEditUsername.setVisibility(View.VISIBLE);
        });
    }

    private void setupEmailSection() {

        btnEditEmail.setOnClickListener(v -> {

            layoutEmailEdit.setVisibility(View.VISIBLE);

            btnEditEmail.setVisibility(View.GONE);
        });
        btnCancelEmail.setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), v);
            layoutEmailEdit.setVisibility(View.GONE);
            btnEditEmail.setVisibility(View.VISIBLE);
            etEmail.setText(tvEmail.getText().toString());
        });

        btnSaveEmail.setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), v);

            UpdateUserRequest request = new UpdateUserRequest();
            request.email = etEmail.getText().toString().trim();

            viewModel.updateUser(userId, token, request);
            layoutEmailEdit.setVisibility(View.GONE);
            btnEditEmail.setVisibility(View.VISIBLE);
        });
    }

    private void setupPasswordSection(View view) {

        btnChangePassword.setOnClickListener(v -> {

            layoutPasswordEdit.setVisibility(View.VISIBLE);

            btnChangePassword.setVisibility(View.GONE);
        });

        btnCancelPassword.setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), v);
            hidePasswordSection();
        });

        btnSavePassword.setOnClickListener(v -> {
            String oldPass = etOldPassword.getText().toString();
            String newPass = etNewPassword.getText().toString();
            String confirm = etConfirmPassword.getText().toString();

            if (!newPass.equals(confirm)) {
                Snackbar.make(view, "Passwords do not match", Snackbar.LENGTH_SHORT).show();
                return;
            }

            ViewUtils.hideKeyboard(requireContext(), v);

            UpdateUserRequest request = new UpdateUserRequest();
            request.old_password = oldPass;
            request.new_password = newPass;

            viewModel.updateUser(userId, token, request);
            hidePasswordSection();
        });
    }

    private void hidePasswordSection() {

        layoutPasswordEdit.setVisibility(View.GONE);

        btnChangePassword.setVisibility(View.VISIBLE);

        etOldPassword.setText("");
        etNewPassword.setText("");
        etConfirmPassword.setText("");
    }

    private void setupLogout(View view) {

        btnLogout.setOnClickListener(v -> {

            viewModel.logout(token, () -> {

                requireContext()
                        .getSharedPreferences(
                                "auth",
                                Context.MODE_PRIVATE
                        )
                        .edit()
                        .clear()
                        .apply();

                Navigation.findNavController(view)
                        .navigate(R.id.action_user_to_login);
            });
        });
    }

    private void setupDeleteAccount(View view) {

        btnDeleteAccount.setOnClickListener(v -> {

            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Delete account")
                    .setMessage("This action cannot be undone.")
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("Delete",
                            (dialog, which) -> {

                                viewModel.deleteUser(
                                        userId,
                                        token,
                                        () -> {

                                            requireContext()
                                                    .getSharedPreferences(
                                                            "auth",
                                                            Context.MODE_PRIVATE
                                                    )
                                                    .edit()
                                                    .clear()
                                                    .apply();

                                            Navigation.findNavController(view)
                                                    .navigate(R.id.action_user_to_login);
                                        });
                            })
                    .show();
        });
    }
    private void handleError(String msg, View view) {

        if (msg == null) return;

        msg = msg.toLowerCase();

        etUsername.setError(null);
        etEmail.setError(null);
        etOldPassword.setError(null);
        etNewPassword.setError(null);
        etConfirmPassword.setError(null);

        if (msg.contains("username")) {
            etUsername.setError(msg);
            return;
        }

        if (msg.contains("email")) {
            etEmail.setError(msg);
            return;
        }

        if (msg.contains("password")) {

            etOldPassword.setError(msg);
            etNewPassword.setError(msg);
            etConfirmPassword.setError(msg);
            return;
        }

        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }
}