package com.lleans.spp_kelompok_2.ui.main.petugas.spp;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.lleans.spp_kelompok_2.R;
import com.lleans.spp_kelompok_2.databinding.Petugas3RincianSppBinding;
import com.lleans.spp_kelompok_2.domain.Utils;
import com.lleans.spp_kelompok_2.domain.model.spp.SppSharedModel;
import com.lleans.spp_kelompok_2.ui.session.SessionManager;
import com.lleans.spp_kelompok_2.ui.utils.UtilsUI;

import java.io.IOException;

public class RincianSpp extends Fragment {

    private Petugas3RincianSppBinding binding;
    private SessionManager sessionManager;

    public RincianSpp() {
        // Required empty public constructor
    }

    private void UILimiter() {
        if (sessionManager.getUserDetail().get(SessionManager.TYPE).equals("petugas")) {
            binding.edit.setVisibility(View.GONE);
        } else {
            binding.edit.setVisibility(View.VISIBLE);
        }
    }

    private void diagCetak() {
        try {
            binding.edit.setVisibility(View.GONE);
            UtilsUI.exportToPNG(getContext(), binding.layout, binding.tahunSpp.getText().toString() + "_" + binding.title.getText().toString() + "_" + binding.idSpp.getText().toString());
            UILimiter();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final NavController nav = Navigation.findNavController(view);

        binding.edit.setOnClickListener(v -> nav.navigate(R.id.action_rincianSpp_petugas_to_editSpp));
        binding.cetak.setOnClickListener(v -> diagCetak());
    }

    private void getSharedModel() {
        int orange = getResources().getColor(R.color.orange);
        SppSharedModel sharedModel = new ViewModelProvider(requireActivity()).get(SppSharedModel.class);
        sharedModel.getData().observe(getViewLifecycleOwner(), detailsItemSpp -> {
            binding.title.setText("Angkatan " + detailsItemSpp.getAngkatan());
            binding.tahunSpp.setText("Total SPP Tahun " + detailsItemSpp.getTahun());
            binding.totalSpp.setText(Utils.formatRupiah(detailsItemSpp.getNominal()));
            if (detailsItemSpp.getNominal() <= 400000) {
                binding.cardView.setCardBackgroundColor(orange);
                binding.totalSpp.setTextColor(orange);
                binding.iconEdit.setImageTintList(ColorStateList.valueOf(orange));
            }
            binding.idSpp.setText("SPP-" + detailsItemSpp.getIdSpp());
            binding.angkatan.setText(String.valueOf(detailsItemSpp.getAngkatan()));
            binding.tahun.setText(String.valueOf(detailsItemSpp.getTahun()));
            binding.total.setText(Utils.formatRupiah(detailsItemSpp.getNominal()));
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Petugas3RincianSppBinding.inflate(inflater, container, false);

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        UILimiter();
        getSharedModel();
        UtilsUI.simpleAnimation(binding.cardView);
        UtilsUI.simpleAnimation(binding.cardView2);
        UtilsUI.simpleAnimation(binding.cetak);
        return binding.getRoot();
    }
}