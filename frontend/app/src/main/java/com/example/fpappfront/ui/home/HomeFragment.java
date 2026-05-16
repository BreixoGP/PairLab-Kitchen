package com.example.fpappfront.ui.home;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fpappfront.R;
import com.example.fpappfront.data.cache.HomeCache;
import com.example.fpappfront.data.model.Ingredient;
import com.example.fpappfront.utils.ViewUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel viewModel;

    private AutoCompleteTextView actvIngredient, actvFamilies;
    private ChipGroup chipGroupSize;
    private RecyclerView recycler;
    private ComboAdapter adapter;

    private List<Ingredient> ingredientList = new ArrayList<>();

    private int selectedIngredientId = -1;
    private List<String> selectedFamilies = new ArrayList<>();

    private String[] familiesArray;
    private boolean[] checkedItems;

    private String token;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        initViews(view);
        initRecycler();
        setupObservers();
        setupUI(view);

        token = requireActivity()
                .getSharedPreferences("auth", Context.MODE_PRIVATE)
                .getString("token", null);

        viewModel.loadInitialData(requireContext(), token);
    }

    private void initViews(View view) {

        actvIngredient = view.findViewById(R.id.actvIngredient);
        actvFamilies = view.findViewById(R.id.actvFamilies);
        chipGroupSize = view.findViewById(R.id.chipGroupSize);
        recycler = view.findViewById(R.id.recyclerCombos);
    }

    private void initRecycler() {
        adapter = new ComboAdapter();
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
    }


    private void setupObservers() {

        viewModel.getIngredients().observe(getViewLifecycleOwner(), list -> {

            ingredientList = list;

            List<String> names = new ArrayList<>();
            for (Ingredient i : list) names.add(i.name);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    names
            );

            actvIngredient.setAdapter(adapter);
            actvIngredient.setThreshold(0);
            actvIngredient.setOnClickListener(v -> {
                if (!actvIngredient.isPopupShowing()) {
                    actvIngredient.showDropDown();
                }
            });

            actvIngredient.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus && !actvIngredient.isPopupShowing()) {
                    actvIngredient.showDropDown();
                }
            });

            actvIngredient.setOnItemClickListener((parent, v, pos, id) -> {
                String selectedName = (String) parent.getItemAtPosition(pos);
                for (Ingredient i : ingredientList) {
                    if (i.name.equals(selectedName)) {
                        selectedIngredientId = i.id;
                        break;
                    }
                }
            });
        });

        viewModel.getFamilies().observe(getViewLifecycleOwner(), list -> {

            if (list == null) return;

            familiesArray = list.toArray(new String[0]);
            checkedItems = new boolean[list.size()];
        });

        viewModel.getCombos().observe(getViewLifecycleOwner(), list -> {
            adapter.setData(list);
        });
    }


    private void setupUI(View view) {

        actvFamilies.setOnClickListener(v -> showFamiliesDialog(view));

        setupChipSizeSelection();

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> {

            ViewUtils.hideKeyboard(requireContext(), view);

            if (!validateInput(view)) return;

            int size = getSelectedSize();

            viewModel.loadCombos(
                    token,
                    selectedIngredientId,
                    size,
                    selectedFamilies
            );
        });

        view.findViewById(R.id.btnRefresh).setOnClickListener(v -> {

            ViewUtils.hideKeyboard(requireContext(), view);

            HomeCache.clear(requireContext());

            viewModel.loadInitialData(requireContext(), token);

            resetState();
        });
    }


    private void setupChipSizeSelection() {

        chipGroupSize.setOnCheckedStateChangeListener((group, checkedIds) -> {
            // no necesitamos guardar estado aquí si no quieres
        });
    }

    private int getSelectedSize() {

        int checkedId = chipGroupSize.getCheckedChipId();

        if (checkedId == View.NO_ID) return -1;

        Chip chip = chipGroupSize.findViewById(checkedId);

        try {
            return Integer.parseInt(chip.getText().toString());
        } catch (Exception e) {
            return -1;
        }
    }

    private boolean validateInput(View view) {

        if (selectedIngredientId == -1) {
            showError(view, "Select an ingredient");
            return false;
        }

        int size = getSelectedSize();

        if (size < 1 || size > 4) {
            showError(view, "Select combo size");
            return false;
        }

        return true;
    }

    private void showError(View view, String msg) {
        Snackbar.make(view, msg, Snackbar.LENGTH_LONG).show();
    }


    private void showFamiliesDialog(View view) {

        if (familiesArray == null || familiesArray.length == 0) {
            showError(view, "Families not loaded yet");
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Select families");

        builder.setMultiChoiceItems(familiesArray, checkedItems, (dialog, which, isChecked) -> {

            if (isChecked) selectedFamilies.add(familiesArray[which]);
            else selectedFamilies.remove(familiesArray[which]);
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            actvFamilies.setText(TextUtils.join(", ", selectedFamilies));
        });

        builder.show();
    }


    private void resetState() {

        selectedIngredientId = -1;
        selectedFamilies.clear();

        actvIngredient.setText("");
        actvFamilies.setText("");

        chipGroupSize.clearCheck();

        adapter.setData(new ArrayList<>());
    }
}