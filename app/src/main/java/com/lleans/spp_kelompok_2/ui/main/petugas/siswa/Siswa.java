package com.lleans.spp_kelompok_2.ui.main.petugas.siswa;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.lleans.spp_kelompok_2.R;
import com.lleans.spp_kelompok_2.databinding.Petugas2SiswaBinding;
import com.lleans.spp_kelompok_2.domain.model.BaseResponse;
import com.lleans.spp_kelompok_2.domain.model.kelas.KelasSharedModel;
import com.lleans.spp_kelompok_2.domain.model.siswa.SiswaData;
import com.lleans.spp_kelompok_2.network.ApiClient;
import com.lleans.spp_kelompok_2.network.ApiInterface;
import com.lleans.spp_kelompok_2.ui.session.SessionManager;
import com.lleans.spp_kelompok_2.ui.utils.UtilsUI;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Siswa extends Fragment {

    private Petugas2SiswaBinding binding;
    private NavController controller;
    private ApiInterface apiInterface;

    private int idKelas;
    private SiswaCardAdapter cardAdapter;
    private final TextWatcher searchAction = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (cardAdapter != null) {
                cardAdapter.getFilter().filter(s);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public Siswa() {
        // Required empty public constructor
    }

    public void notFoundHandling(boolean check) {
        if (check) {
            binding.recyclerView.setVisibility(View.GONE);
            binding.notFound.getRoot().setVisibility(View.VISIBLE);
            UtilsUI.simpleAnimation(binding.notFound.getRoot());
        } else {
            binding.notFound.getRoot().setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void UILimiter() {
        binding.edit.setVisibility(View.GONE);
        binding.add.setVisibility(View.GONE);
    }

    private void calcSiswa(int siswaSize) {
        binding.jumlahSiswa.setText(siswaSize + " Siswa");
        UtilsUI.simpleAnimation(binding.jumlahSiswa);
        binding.jumlahSiswa.setVisibility(View.VISIBLE);
    }

    private void setAdapter(List<SiswaData> data) {
        cardAdapter = new SiswaCardAdapter(data, controller, this);
        notFoundHandling(cardAdapter.getItemCount() == 0);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(cardAdapter);
    }

    private void getSiswa() {
        UtilsUI.isLoading(binding.refresher, true, true);
        Call<BaseResponse<List<SiswaData>>> siswaDataCall;

        siswaDataCall = apiInterface.getSiswa(
                null,
                null,
                null,
                idKelas,
                null,
                null,
                null,
                null);
        siswaDataCall.enqueue(new Callback<BaseResponse<List<SiswaData>>>() {
            @Override
            public void onResponse(Call<BaseResponse<List<SiswaData>>> call, Response<BaseResponse<List<SiswaData>>> response) {
                UtilsUI.isLoading(binding.refresher, true, false);
                if (response.body() != null && response.isSuccessful()) {
                    calcSiswa(response.body().getDetails().size());
                    setAdapter(response.body().getDetails());
                } else {
                    if (response.code() == 404) {
                        notFoundHandling(true);
                    } else {
                        try {
                            BaseResponse message = new Gson().fromJson(response.errorBody().charStream(), BaseResponse.class);
                            UtilsUI.toaster(getContext(), message.getMessage());
                        } catch (Exception e) {
                            try {
                                UtilsUI.dialog(getContext(), "Something went wrong!", response.errorBody().string(), false).show();
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<BaseResponse<List<SiswaData>>> call, @NonNull Throwable t) {
                UtilsUI.isLoading(binding.refresher, true, false);
                UtilsUI.dialog(getContext(), "Something went wrong!", t.getLocalizedMessage(), false).show();
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller = Navigation.findNavController(view);

        binding.refresher.setOnRefreshListener(this::getSiswa);
        binding.searchBar.addTextChangedListener(searchAction);
        binding.searchBar.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                in.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
            }
        });
        binding.add.setOnClickListener(v -> controller.navigate(R.id.action_siswa_petugas_to_tambahSiswa));
        binding.edit.setOnClickListener(v -> controller.navigate(R.id.action_siswa_petugas_to_editKelas));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = Petugas2SiswaBinding.inflate(inflater, container, false);

        KelasSharedModel shared = new ViewModelProvider(requireActivity()).get(KelasSharedModel.class);
        SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
        apiInterface = ApiClient.getClient(sessionManager.getUserDetail().get(SessionManager.TOKEN)).create(ApiInterface.class);
        if (sessionManager.getUserDetail().get(SessionManager.TYPE).equals("petugas")) {
            UILimiter();
        }
        shared.getData().observe(getViewLifecycleOwner(), detailsItemKelas -> {
            this.idKelas = detailsItemKelas.getIdKelas();
            binding.namaKelas.setText(detailsItemKelas.getNamaKelas());
            binding.jurusan.setText(detailsItemKelas.getJurusan());
            UtilsUI.nicknameBuilder(getActivity().getApplicationContext(), detailsItemKelas.getNamaKelas(), binding.nick, binding.nickFrame);
            binding.angkatan.setText(String.valueOf(detailsItemKelas.getAngkatan()));
            getSiswa();
        });
        UtilsUI.simpleAnimation(binding.add);
        return binding.getRoot();
    }

}