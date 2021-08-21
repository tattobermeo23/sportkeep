package com.deportes.sp1.fragments;


import android.app.TimePickerDialog;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;
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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import com.deportes.sp1.R;
import com.deportes.sp1.adapters.SpinnerCanchaFirestoreAdapter;
import com.deportes.sp1.interfaces.Fecha;
import com.deportes.sp1.models.CanchaModelo;
import com.deportes.sp1.models.EntrenamientoModelo;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
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
public class EntrenamientoFragment extends Fragment implements Fecha, SpinnerCanchaFirestoreAdapter.OnButtonClick {


    public EntrenamientoFragment() {
        // Required empty public constructor
    }

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    public final Calendar c = Calendar.getInstance();

    final int hora = c.get(Calendar.HOUR_OF_DAY);
    final int minuto = c.get(Calendar.MINUTE);

    private TextView show_inicial, show_final, txtResultados;
    private ImageButton obtener_inicial, obtener_final;
    private Spinner deportes, dias;
    private SearchableSpinner buscar;
    private Button publicar;
    private TextInputEditText entrenador, contacto, descripcion;
    private String hora_inicial, hora_final, userCiudad, userEstado, userPais, deporte, dia = "", cancha = "";
    private RecyclerView recyclerSpinnerCanchas;
    private Set<String> hs;
    private ScrollView scrollView;
    private SpinnerCanchaFirestoreAdapter adapterFirestore;
    private FirebaseFirestore db;
    private LocationManager ubicacion;
    private ArrayList<String> barrios = new ArrayList<>();
    private LinearLayout linearLayout;
    private NotificationManagerCompat notificationManager;
    private FirebaseUser user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentEntrenamiento = inflater.inflate(R.layout.fragment_entrenamiento, container, false);

        scrollView = fragmentEntrenamiento.findViewById(R.id.scrollEntrena);
        recyclerSpinnerCanchas = fragmentEntrenamiento.findViewById(R.id.recyclerSpinnerEntrenamientos);
        deportes = fragmentEntrenamiento.findViewById(R.id.formDeporte);
        txtResultados = fragmentEntrenamiento.findViewById(R.id.resultadosEntrena);
        show_inicial = fragmentEntrenamiento.findViewById(R.id.form_hora_inicial);
        show_final = fragmentEntrenamiento.findViewById(R.id.form_hora_final);
        obtener_inicial = fragmentEntrenamiento.findViewById(R.id.obtener_inicial);
        obtener_final = fragmentEntrenamiento.findViewById(R.id.obtener_final);
        entrenador = fragmentEntrenamiento.findViewById(R.id.form_entrenador);
        contacto = fragmentEntrenamiento.findViewById(R.id.form_contacto);
        deportes = fragmentEntrenamiento.findViewById(R.id.form_deporte_ent);
        dias = fragmentEntrenamiento.findViewById(R.id.form_dias);
        buscar = fragmentEntrenamiento.findViewById(R.id.form_cancha_ent);
        publicar = fragmentEntrenamiento.findViewById(R.id.btnPublicarEntrena);
        descripcion = fragmentEntrenamiento.findViewById(R.id.form_descripcion_ent);
        linearLayout = fragmentEntrenamiento.findViewById(R.id.viewDias);
        formatearHoras();

        txtResultados.setText(getString(R.string.resultados)+" (0)");

        notificationManager = NotificationManagerCompat.from(getContext());
        user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(fragmentEntrenamiento.getContext(), R.array.combo_deportes, R.layout.support_simple_spinner_dropdown_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        deportes.setAdapter(adapter);

        obtenerDeporte();

        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        View navView =  navigationView.getHeaderView(0);
        TextView pais = navView.findViewById(R.id.navPais);
        TextView estado = navView.findViewById(R.id.navEstado);
        TextView ciudad = navView.findViewById(R.id.navCiudad);

        userCiudad = ciudad.getText().toString();//Neiva
        userEstado = estado.getText().toString();//Huila
        userPais = pais.getText().toString();//Colombia

        ArrayAdapter<CharSequence> adapterDias = ArrayAdapter.createFromResource(fragmentEntrenamiento.getContext(),
                R.array.dias_array, android.R.layout.simple_spinner_item);

        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        dias.setAdapter(adapterDias);

        obtenerDia();

        db = FirebaseFirestore.getInstance();

        llenarBarriosSpinner();

        adapter = new ArrayAdapter(fragmentEntrenamiento.getContext(), android.R.layout.simple_list_item_1, barrios);

        buscar.setAdapter(adapter);
        buscar.setTitle(getString(R.string.selecciona_barrio));
        buscar.setPositiveButton("Ok");

        buscar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String barrio = adapterView.getItemAtPosition(i).toString();// obtengo el barrio para hacer el filtro
                recyclerSpinnerCanchas.setVisibility(View.VISIBLE);
                listarCanchas(barrio);

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        publicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publicar();
            }
        });

        return fragmentEntrenamiento;
    }


    public void formatearHoras(){
        hora_inicial = "00:00";
        hora_final = "00:00";

        obtener_inicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        //Formateo el hora obtenido: antepone el 0 si son menores de 10
                        String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                        //Formateo el minuto obtenido: antepone el 0 si son menores de 10
                        String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                        //Obtengo el valor a.m. o p.m., dependiendo de la selección del usuario

                        //Muestro la hora con el formato deseado
                        show_inicial.setText(horaFormateada + DOS_PUNTOS + minutoFormateado);
                        hora_inicial = horaFormateada + DOS_PUNTOS + minutoFormateado;
                    }
                    //Estos valores deben ir en ese orden
                    //Al colocar en false se muestra en formato 12 horas y true en formato 24 horas
                    //Pero el sistema devuelve la hora en formato 24 horas
                }, hora, minuto, false);

                recogerHora.show();
            }
        });

        obtener_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog recogerHora = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String horaFormateada =  (hourOfDay < 10)? String.valueOf(CERO + hourOfDay) : String.valueOf(hourOfDay);
                        String minutoFormateado = (minute < 10)? String.valueOf(CERO + minute):String.valueOf(minute);
                        show_final.setText(horaFormateada + DOS_PUNTOS + minutoFormateado);
                        hora_final = horaFormateada + DOS_PUNTOS + minutoFormateado;
                    }
                }, hora, minuto, false);

                recogerHora.show();
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

    ArrayList<String> dias_final = new ArrayList<>();
    TextView textView;
    private void obtenerDia(){

        dias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(linearLayout.getChildCount() > 6){
                    Toast.makeText(getContext(), R.string.no_7_dias, Toast.LENGTH_SHORT).show();
                }else{

                    textView = new TextView(getContext());

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );

                    textView.setLayoutParams(params);

                    textView.setText(adapterView.getItemAtPosition(i).toString());
                    textView.setTextSize(13);

                    if(dias_final.contains(textView.getText().toString())){
                        Toast.makeText(getContext(), R.string.no_repetir_dias, Toast.LENGTH_SHORT).show();
                    }else{
                        linearLayout.addView(textView);

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dias_final.clear();
                                linearLayout.removeAllViews();
                                dia = "";
                            }
                        });

                        dia += textView.getText().toString()+" ";
                        dias_final.add(textView.getText().toString());
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

    }

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
                txtResultados.setText(getString(R.string.resultados)+" ("+items+")");// guardo la cantidad de resultados

            }
        });

    }

    private void publicar(){
        if(descripcion.length() > 500){
            Toast.makeText(getContext(), R.string.no_caracteres, Toast.LENGTH_LONG).show();
        }else {
            if (cancha.equals("") || entrenador.getText().toString().equals("") || contacto.getText().toString().equals("")
                    || hora_inicial.equals("00:00") || hora_final.equals("00:00") || descripcion.getText().toString().equals("")) {
                Toast.makeText(getContext(), R.string.rellena_entrenamiento, Toast.LENGTH_LONG).show();
            } else {

                EntrenamientoModelo modelo = new EntrenamientoModelo(cancha, userCiudad, contacto.getText().toString(), getDate(),
                        deporte, descripcion.getText().toString(), dia, entrenador.getText().toString(), userEstado, hora_final, hora_inicial, user.getUid(), userPais);

                DocumentReference newEntrenaRef = db.collection("entrenamientos").document();

                newEntrenaRef.set(modelo);

                cancha = "";
                contacto.setText("");
                entrenador.setText("");
                show_inicial.setText("00:00");
                show_final.setText("00:00");
                hora_inicial = "00:00";
                hora_final = "00:00";
                descripcion.setText("");
                recyclerSpinnerCanchas.setVisibility(View.GONE);
                txtResultados.setText(getString(R.string.resultados) + " (0)");
                dias_final.clear();
                linearLayout.removeAllViews();
                dia = "";

            }
        }
    }

    @Override
    public void onButtonClick(DocumentSnapshot snapshot, int position) {
        cancha = snapshot.getId();//guardo el id de la cancha
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(View.FOCUS_DOWN);//Scroll hacia abajo al hacer click en una carta
            }
        });
        Toast.makeText(getContext(), R.string.cancha_seleccionada, Toast.LENGTH_SHORT).show();
    }

    @Override
    public String getDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM hh:mm a", Locale.getDefault());
        Date date = Calendar.getInstance().getTime();

        return dateFormat.format(date);
    }
}
