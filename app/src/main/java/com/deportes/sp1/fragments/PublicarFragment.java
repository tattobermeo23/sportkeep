package com.deportes.sp1.fragments;


import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.deportes.sp1.R;
import com.deportes.sp1.adapters.SpinnerCanchaFirestoreAdapter;
import com.deportes.sp1.interfaces.Fecha;
import com.deportes.sp1.models.CanchaModelo;
import com.deportes.sp1.models.PublicacionModelo;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class PublicarFragment extends Fragment implements Fecha, SpinnerCanchaFirestoreAdapter.OnButtonClick {


    public PublicarFragment() {
        // Required empty public constructor
    }


    private Spinner deportes, tipo;

    private ArrayList<String> barrios = new ArrayList<>();
    private SearchableSpinner spinnerCanchas;
    private LocationManager ubicacion;
    private String userCiudad, userEstado, userPais, cancha = "", deporte, tipo_publicacion;
    private RecyclerView recyclerSpinnerCanchas;
    private Set<String> hs;
    private EditText form_descripcion;
    private Button publicar;
    private ScrollView scrollView;
    private TextView resultados;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentPublicar = inflater.inflate(R.layout.fragment_publicar, container, false);
        scrollView = fragmentPublicar.findViewById(R.id.scrollPublicar);
        recyclerSpinnerCanchas = fragmentPublicar.findViewById(R.id.recyclerSpinnerCanchas);
        deportes = fragmentPublicar.findViewById(R.id.formDeporte);
        tipo = fragmentPublicar.findViewById(R.id.formTipo);
        resultados = fragmentPublicar.findViewById(R.id.resultadosCanchas);
        form_descripcion = fragmentPublicar.findViewById(R.id.formDescripcion);

        resultados.setText(getString(R.string.resultados)+ " (0)");

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.combo_deportes, R.layout.support_simple_spinner_dropdown_item);

        deportes.setAdapter(adapter);

        spinnerCanchas = fragmentPublicar.findViewById(R.id.spinnerCanchas);

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View navView =  navigationView.getHeaderView(0);
        TextView pais = navView.findViewById(R.id.navPais);
        TextView estado = navView.findViewById(R.id.navEstado);
        TextView ciudad = navView.findViewById(R.id.navCiudad);

        userCiudad = ciudad.getText().toString();//Neiva
        userEstado = estado.getText().toString();//Huila
        userPais = pais.getText().toString();//Colombia

        //LLenar el array de canchas
        llenarBarriosSpinner();

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, barrios);
        spinnerCanchas.setAdapter(adapter);
        spinnerCanchas.setTitle(getString(R.string.selecciona_barrio));
        spinnerCanchas.setPositiveButton("Ok");

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.tipos_array, R.layout.support_simple_spinner_dropdown_item);

        tipo.setAdapter(adapter);

        obtenerDeporte();
        obtenerTipo();

        spinnerCanchas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String barrio = adapterView.getItemAtPosition(i).toString();// obtengo el barrio para hacer el filtro
                recyclerSpinnerCanchas.setVisibility(View.VISIBLE);
                listarCanchas(barrio);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        publicar = fragmentPublicar.findViewById(R.id.btnPublicar);

        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicar();
            }
        });

        return fragmentPublicar;
    }

    private FirebaseUser user;


    private SpinnerCanchaFirestoreAdapter adapterFirestore;
    private FirebaseFirestore db;

    private void llenarBarriosSpinner(){

        hs = new HashSet<>();//hashset para eliminar barrios repetidos

        db.collection("canchas").whereEqualTo("pais", userPais)
                .whereEqualTo("estado", userEstado)
                .whereEqualTo("ciudad", userCiudad)
                .whereEqualTo("dominio", getString(R.string.publico))
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        barrios.add(""+document.getData().get("barrio"));

                        hs.addAll(barrios);
                        barrios.clear();//elimino los barrios repetidos
                        barrios.addAll(hs);//filtro los barrios repetidos
                    }
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });
    }

    private Query query;

    private void listarCanchas(String barrio){

        query = db.collection("canchas").whereEqualTo("barrio", barrio).whereEqualTo("pais", userPais)
                .whereEqualTo("estado", userEstado)
                .whereEqualTo("ciudad", userCiudad)
                .whereEqualTo("dominio", getString(R.string.publico));

        PagedList.Config config = new PagedList.Config.Builder()
                .setInitialLoadSizeHint(3)
                .setPageSize(3)
                .build();

        FirestorePagingOptions<CanchaModelo> options = new FirestorePagingOptions.Builder<CanchaModelo>()
                .setLifecycleOwner(getActivity())
                .setQuery(query, config, CanchaModelo.class)
                .build();

        adapterFirestore = new SpinnerCanchaFirestoreAdapter(options, this, R.layout.item_spinner_cancha);

        recyclerSpinnerCanchas.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerSpinnerCanchas.setAdapter(adapterFirestore);

        adapterFirestore.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            public void onItemRangeInserted(int positionStart, int itemCount) {
                int items = adapterFirestore.getItemCount();
                resultados.setText(getString(R.string.resultados)+" ("+items+")");// guardo la cantidad de resultados

            }
        });

    }
    private void obtenerDeporte(){
        deporte = "Microfutbol";//deporte por defecto

        deportes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deporte = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    private void obtenerTipo(){
        tipo_publicacion = "Publicación";//tipo por defecto

        tipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                tipo_publicacion = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
    }

    @Override
    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        return dateFormat.format(date);
    }

    private void publicar(){

        if(form_descripcion.length() > 500){
            Toast.makeText(getContext(), R.string.no_caracteres, Toast.LENGTH_LONG).show();
        }else {

            if (cancha.equals("") || form_descripcion.getText().toString().equals("")) {
                Toast.makeText(getContext(), R.string.rellena_publicacion, Toast.LENGTH_LONG).show();
            } else {

                if (tipo_publicacion.equals("Publicación")) {

                    PublicacionModelo publicacionModelo = new PublicacionModelo(deporte, form_descripcion.getText().toString(), getDate(), user.getUid(),
                            cancha, userPais, userEstado, userCiudad);

                    DocumentReference newPublicacionRef = db.collection("publicaciones").document();// genero el id antes de escribir la colección

                    newPublicacionRef.set(publicacionModelo);

                    cancha = "";
                    form_descripcion.setText("");
                    recyclerSpinnerCanchas.setVisibility(View.GONE);
                    resultados.setText(getString(R.string.resultados) + " (0)");

                }else if(tipo_publicacion.equals("Publication")){
                    PublicacionModelo publicacionModelo = new PublicacionModelo(deporte, form_descripcion.getText().toString(), getDate(), user.getUid(),
                            cancha, userPais, userEstado, userCiudad);

                    DocumentReference newPublicacionRef = db.collection("publications").document();// genero el id antes de escribir la colección

                    newPublicacionRef.set(publicacionModelo);

                    cancha = "";
                    form_descripcion.setText("");
                    recyclerSpinnerCanchas.setVisibility(View.GONE);
                    resultados.setText(getString(R.string.resultados) + " (0)");

                }else if(tipo_publicacion.equals("Championship")){
                    PublicacionModelo publicacionModelo = new PublicacionModelo(deporte, form_descripcion.getText().toString(), getDate(), user.getUid(),
                            cancha, userPais, userEstado, userCiudad);

                    DocumentReference newPublicacionRef = db.collection("championships").document();// genero el id antes de escribir la colección

                    newPublicacionRef.set(publicacionModelo);

                    cancha = "";
                    form_descripcion.setText("");
                    recyclerSpinnerCanchas.setVisibility(View.GONE);
                    resultados.setText(getString(R.string.resultados) + " (0)");

                }else {

                    PublicacionModelo publicacionModelo = new PublicacionModelo(deporte, form_descripcion.getText().toString(), getDate(), user.getUid(),
                            cancha, userPais, userEstado, userCiudad);

                    DocumentReference newPublicacionRef = db.collection("campeonatos").document();// genero el id antes de escribir la colección

                    newPublicacionRef.set(publicacionModelo);

                    cancha = "";
                    form_descripcion.setText("");
                    recyclerSpinnerCanchas.setVisibility(View.GONE);
                    resultados.setText(getString(R.string.resultados) + " (0)");

                }

            }
        }
    }
    @Override
    public void onButtonClick(DocumentSnapshot snapshot, int position) {//aqui consigo el id de la cancha
        cancha = snapshot.getId();//guardo el id de la cancha
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);//Scroll hacia abajo al hacer click en una carta
            }
        });
        Toast.makeText(getContext(), R.string.cancha_seleccionada, Toast.LENGTH_SHORT).show();
    }

}
