package com.deportes.sp1.fragments;


import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.deportes.sp1.R;
import com.deportes.sp1.adapters.EntrenamientoFirestoreAdapter;
import com.deportes.sp1.models.EntrenamientoModelo;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class EntrenamientosFragment extends Fragment implements EntrenamientoFirestoreAdapter.OnButtonClick{


    public EntrenamientosFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerEntrenmaientos;
    private FirebaseFirestore db;
    private Query query;
    private EntrenamientoFirestoreAdapter adapter;
    private EditText filtro;
    private ProgressBar progressBar;
    private TextView mensajeCarga;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentEntrenamientos = inflater.inflate(R.layout.fragment_entrenamientos, container, false);

        recyclerEntrenmaientos = fragmentEntrenamientos.findViewById(R.id.recyclerEntrenamientos);
        cardView = fragmentEntrenamientos.findViewById(R.id.cardEntrenamiento);
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        progressBar = fragmentEntrenamientos.findViewById(R.id.progressEnt);
        mensajeCarga = fragmentEntrenamientos.findViewById(R.id.mensajeCarga2);

        filtro = fragmentEntrenamientos.findViewById(R.id.filtroEntrenamientos);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View navView =  navigationView.getHeaderView(0);
        TextView pais = navView.findViewById(R.id.navPais);
        TextView estado = navView.findViewById(R.id.navEstado);
        TextView ciudad = navView.findViewById(R.id.navCiudad);

        userCiudad = ciudad.getText().toString();//Neiva
        userEstado = estado.getText().toString();//Huila
        userPais = pais.getText().toString();//Colombia

        listarEntrenamientos();

        filtro.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                listarEntrenamientosPorDeporte(editable.toString());
            }
        });

        return fragmentEntrenamientos;
    }

    private FirebaseUser user;


    private String userCiudad, userEstado, userPais;

    private void listarEntrenamientosPorDeporte(String deporte){

        String filtro = capitalizeFirstLetter(deporte);

        query = db.collection("entrenamientos").whereEqualTo("pais", userPais)
                .whereEqualTo("estado", userEstado)
                .whereEqualTo("ciudad", userCiudad)
                .whereEqualTo("deporte", filtro);

        if(deporte.equals("")){
            query = db.collection("entrenamientos").whereEqualTo("pais", userPais)
                    .whereEqualTo("estado", userEstado)
                    .whereEqualTo("ciudad", userCiudad)
                    .orderBy("creado_en", Query.Direction.DESCENDING);
        }

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<EntrenamientoModelo> options = new FirestorePagingOptions.Builder<EntrenamientoModelo>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, config, EntrenamientoModelo.class)
                .build();

        adapter = new EntrenamientoFirestoreAdapter(options, this, progressBar, mensajeCarga);

        recyclerEntrenmaientos.setHasFixedSize(true);
        recyclerEntrenmaientos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerEntrenmaientos.setAdapter(adapter);
    }

    private void listarEntrenamientos(){
        query = db.collection("entrenamientos").whereEqualTo("pais", userPais)
                .whereEqualTo("estado", userEstado)
                .whereEqualTo("ciudad", userCiudad)
                .orderBy("creado_en", Query.Direction.DESCENDING);//https://console.cloud.google.com/firestore/indexes/composite?authuser=3&project=sportkeep-90fd6 necesita crear indices compuestos

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<EntrenamientoModelo> options = new FirestorePagingOptions.Builder<EntrenamientoModelo>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, config, EntrenamientoModelo.class)
                .build();

        adapter = new EntrenamientoFirestoreAdapter(options, this, progressBar, mensajeCarga);

        recyclerEntrenmaientos.setHasFixedSize(true);
        recyclerEntrenmaientos.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerEntrenmaientos.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                if(itemCount != 0){
                    cardView.setVisibility(View.GONE);
                }

            }
        });
    }
    private CardView cardView;
    @Override
    public void onButtonClick(DocumentSnapshot snapshot, int position) {

        Toast.makeText(getContext(), "Mostrar detalles", Toast.LENGTH_LONG).show();
    }
    private String capitalizeFirstLetter(String original) {//este metodo vuelve la primera letra mayuscula
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}
