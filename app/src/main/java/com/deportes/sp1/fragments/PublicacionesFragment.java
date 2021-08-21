package com.deportes.sp1.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.deportes.sp1.Localizacion;
import com.deportes.sp1.LoginActivity;
import com.deportes.sp1.R;
import com.deportes.sp1.adapters.PublicacionFirestoreAdapter;
import com.deportes.sp1.models.PublicacionModelo;
import com.facebook.login.LoginManager;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicacionesFragment extends Fragment implements PublicacionFirestoreAdapter.OnButtonClick {

    private RecyclerView recyclerPublicaciones;
    private FirebaseFirestore db;
    private PublicacionFirestoreAdapter adapter;
    private Spinner tipo;
    private String tipo_filtro;
    private ProgressBar progressBar;
    private TextView mensajeCarga;

    public PublicacionesFragment() {
        // Required empty public constructor
    }

    private Query query;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View fragmentPublicaciones = inflater.inflate(R.layout.fragment_publicaciones, container, false);

        progressBar = fragmentPublicaciones.findViewById(R.id.progressPubli);
        mensajeCarga = fragmentPublicaciones.findViewById(R.id.mensajeCarga);

        recyclerPublicaciones = fragmentPublicaciones.findViewById(R.id.recyclerPublicaciones);
        db = FirebaseFirestore.getInstance();
        tipo = fragmentPublicaciones.findViewById(R.id.spinnerPublicaciones);

        cardView = fragmentPublicaciones.findViewById(R.id.cardPublicacion);

        ArrayList<String> tipos = new ArrayList<>();

        tipos.add(getString(R.string.menu_publicaciones));
        tipos.add(getString(R.string.menu_campeonatos));

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, tipos);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

        tipo.setAdapter(adapter);

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipo_filtro = adapterView.getItemAtPosition(i).toString();

                listarPublicaciones(tipo_filtro);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        verificaIp();

        return fragmentPublicaciones;
    }
    private int contador = 0;
    public void setLocation(Location loc) {
        if (loc.getLatitude() != 0.0 && loc.getLongitude() != 0.0) {
            try {
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                List<Address> list = geocoder.getFromLocation(loc.getLatitude(), loc.getLongitude(), 1);
                if (!list.isEmpty()) {
                    contador++;
                    userCiudad = ""+ list.get(0).getLocality();//Neiva
                    userEstado = ""+ list.get(0).getAdminArea();//Huila
                    userPais = ""+ list.get(0).getCountryName();//Colombia
                    if(contador == 1)listarPublicaciones(getString(R.string.menu_publicaciones));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void locationStart() {
        LocationManager mlocManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setFragmentPublicaciones(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0,  Local);
        Log.d("Mensaje---", "Localización agregada");
    }

    private void verificaIp(){
        db.collection("ip_bloqueadas").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if(document.get("ip").equals(getLocalIpAddress())){
                            Toast.makeText(getApplicationContext(), R.string.ip_bloqueada
                                    , Toast.LENGTH_LONG).show();
                            logout();
                        }
                    }
                }
            }
        });
    }

    private String primeraMinuscula(String original) {//este metodo vuelve la primera letra minuscula
        if (original == null || original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toLowerCase() + original.substring(1);
    }

    private void listarPublicaciones(String collection){

        if(collection.equals(getString(R.string.menu_publicaciones))){
            collection = primeraMinuscula(getString(R.string.menu_publicaciones));
        }else{
            collection = primeraMinuscula(getString(R.string.menu_campeonatos));
        }

        query = db.collection(collection).whereEqualTo("pais", userPais)
                .whereEqualTo("estado", userEstado)
                .whereEqualTo("ciudad", userCiudad)
                .orderBy("creado_en", Query.Direction.DESCENDING);//https://console.cloud.google.com/firestore/indexes/composite?authuser=3&project=sportkeep-90fd6 necesita crear indices compuestos

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<PublicacionModelo> options = new FirestorePagingOptions.Builder<PublicacionModelo>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, config, PublicacionModelo.class)
                .build();

        adapter = new PublicacionFirestoreAdapter(options, this, R.layout.item_publicacion, getContext(), progressBar, mensajeCarga);

        recyclerPublicaciones.setHasFixedSize(true);
        recyclerPublicaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerPublicaciones.setAdapter(adapter);

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {

                if(itemCount != 0){
                    cardView.setVisibility(View.GONE);
                }
            }
        });
    }

    private CardView cardView;
    private String userCiudad, userEstado, userPais;

    @Override
    public void onButtonClick(DocumentSnapshot snapshot, int position) {
        Toast.makeText(getContext(), "Mostrar detalles", Toast.LENGTH_LONG).show();
    }
    private String getLocalIpAddress(){
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                 en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("IP Address", ex.toString());
        }
        return null;
    }
    private void goLoginActivity() {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        LoginManager.getInstance().logOut();
        goLoginActivity();
    }
}
