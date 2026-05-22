package com.example.fpappfront.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

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
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private enum LoadingType {
        INITIAL, COMBOS, RECIPE
    }

    private HomeViewModel viewModel;
    private AutoCompleteTextView actvIngredient, actvFamilies;
    private ChipGroup chipGroupSize;
    private RecyclerView recycler;
    private ComboAdapter adapter;
    private AlertDialog progressDialog;
    private TextView progressTextView;

    private List<Ingredient> ingredientList = new ArrayList<>();

    private int selectedIngredientId = -1;
    private final List<String> selectedFamilies = new ArrayList<>();

    private String[] familiesArray;
    private boolean[] checkedItems;

    private String token;
    private LoadingType currentLoadingType = LoadingType.INITIAL;

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

        if (!HomeCache.hasIngredients(requireContext()) || !HomeCache.hasFamilies(requireContext())) {
            currentLoadingType = LoadingType.INITIAL;
            showLoadingDialog();
        }

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

        adapter.setOnComboClickListener(ingredientsInCombo -> {
            if (token != null && !ingredientsInCombo.isEmpty()) {
                currentLoadingType = LoadingType.RECIPE;
                viewModel.generateRecipe(token, ingredientsInCombo);
            }
        });
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

            checkInitialLoadingDataStatus();
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> {
            if (loading != null && loading) {
                showLoadingDialog();
            } else {
                hideLoadingDialog();
            }
        });

        viewModel.getRecipeResult().observe(getViewLifecycleOwner(), recipe -> {
            if (recipe != null) {
                showRecipeDialog(recipe);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMsg -> {
            if (errorMsg != null) {
                showError(getView(), errorMsg);
            }
        });

        viewModel.getFamilies().observe(getViewLifecycleOwner(), list -> {
            if (list == null) return;

            familiesArray = list.toArray(new String[0]);
            checkedItems = new boolean[list.size()];

            Arrays.fill(checkedItems, true);
            selectedFamilies.clear();
            selectedFamilies.addAll(list);

            actvFamilies.setText(getString(R.string.all_selected));

            checkInitialLoadingDataStatus();
        });

        viewModel.getCombos().observe(getViewLifecycleOwner(), list -> {
            if (list == null || list.isEmpty()) {
                adapter.setData(new ArrayList<>());
                Snackbar.make(recycler, "No matching combos found for these filters 🍳", Snackbar.LENGTH_LONG).show();
            } else {
                adapter.setData(list);
            }
        });
    }

    private void setupUI(View view) {
        actvFamilies.setOnClickListener(v -> showFamiliesDialog(view));

        setupChipSizeSelection();

        view.findViewById(R.id.btnSearch).setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), view);

            if (!validateInput(view)) return;

            int size = getSelectedSize();

            List<String> familyFilterToSend = (selectedFamilies.size() == familiesArray.length || selectedFamilies.isEmpty())
                    ? null
                    : selectedFamilies;

            currentLoadingType = LoadingType.COMBOS;
            viewModel.loadCombos(
                    token,
                    selectedIngredientId,
                    size,
                    familyFilterToSend
            );
        });

        view.findViewById(R.id.btnRefresh).setOnClickListener(v -> {
            ViewUtils.hideKeyboard(requireContext(), view);
            HomeCache.clear(requireContext());
            currentLoadingType = LoadingType.INITIAL;
            showLoadingDialog();
            viewModel.loadInitialData(requireContext(), token);
            resetState();
        });
    }

    private void setupChipSizeSelection() {
        chipGroupSize.setOnCheckedStateChangeListener((group, checkedIds) -> {});
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

        boolean[] tempCheckedItems = Arrays.copyOf(checkedItems, checkedItems.length);

        builder.setMultiChoiceItems(familiesArray, tempCheckedItems, (dialog, which, isChecked) -> tempCheckedItems[which] = isChecked);

        builder.setNeutralButton("Clear All", (dialog, which) -> {
            Arrays.fill(checkedItems, false);
            selectedFamilies.clear();
            actvFamilies.setText(getString(R.string.all_selected));
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            System.arraycopy(tempCheckedItems, 0, checkedItems, 0, checkedItems.length);

            selectedFamilies.clear();
            for (int i = 0; i < checkedItems.length; i++) {
                if (checkedItems[i]) {
                    selectedFamilies.add(familiesArray[i]);
                }
            }

            if (selectedFamilies.isEmpty() || selectedFamilies.size() == familiesArray.length) {
                actvFamilies.setText(getString(R.string.all_selected));
            } else {
                actvFamilies.setText(TextUtils.join(", ", selectedFamilies));
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void resetState() {
        selectedIngredientId = -1;
        selectedFamilies.clear();

        actvIngredient.setText("");

        if (familiesArray != null) {
            Arrays.fill(checkedItems, true);
            selectedFamilies.addAll(Arrays.asList(familiesArray));
        }
        actvFamilies.setText(getString(R.string.all_selected));

        chipGroupSize.clearCheck();
        adapter.setData(new ArrayList<>());
    }

    private void checkInitialLoadingDataStatus() {
        if (currentLoadingType == LoadingType.INITIAL && ingredientList != null && !ingredientList.isEmpty() && familiesArray != null && familiesArray.length > 0) {
            hideLoadingDialog();
        }
    }

    @SuppressLint("SetTextI18n")
    private void showLoadingDialog() {
        if (progressDialog == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
            layout.setOrientation(android.widget.LinearLayout.VERTICAL);
            layout.setPadding(50, 50, 50, 50);
            layout.setGravity(android.view.Gravity.CENTER);

            android.widget.ProgressBar progressBar = new android.widget.ProgressBar(requireContext());
            progressTextView = new android.widget.TextView(requireContext());
            progressTextView.setPadding(0, 30, 0, 0);
            progressTextView.setTextSize(16);

            layout.addView(progressBar);
            layout.addView(progressTextView);

            builder.setView(layout);
            builder.setCancelable(false);
            progressDialog = builder.create();
        }

        if (currentLoadingType == LoadingType.INITIAL) {
            progressTextView.setText(getString(R.string.gathering_ingredients));
        } else if (currentLoadingType == LoadingType.COMBOS) {
            progressTextView.setText(getString(R.string.searching_combos));
        } else {
            progressTextView.setText(getString(R.string.chefmini_cooking));
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showRecipeDialog(com.example.fpappfront.data.model.RecipeResponse recipe) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        android.widget.TextView customTitle = new android.widget.TextView(requireContext());
        customTitle.setText(getString(R.string.recipe_title_format, recipe.title));
        customTitle.setTextSize(20);
        customTitle.setTypeface(null, android.graphics.Typeface.BOLD);
        customTitle.setPadding(60, 50, 60, 20);
        customTitle.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), android.R.color.black));
        customTitle.setMaxLines(3);
        customTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);

        builder.setCustomTitle(customTitle);
        String message = getString(R.string.extra_ingredients) + "\n" +
                recipe.extraIngredients +
                "\n\n" +
                getString(R.string.steps) + "\n" +
                recipe.steps.replace(". ", ".\n\n");

        builder.setMessage(message);
        builder.setPositiveButton("Ok ", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
}