package com.deportes.sp1.adapters;

import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.deportes.sp1.R;
import com.deportes.sp1.models.EntrenamientoModelo;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class EntrenamientoFirestoreAdapter extends FirestorePagingAdapter<EntrenamientoModelo, EntrenamientoFirestoreAdapter.ViewHolderEntrenamientos> {

    private EntrenamientoFirestoreAdapter.OnButtonClick onButtonClick;
    private ProgressBar progressBar;
    TextView mensajeCarga;

    public EntrenamientoFirestoreAdapter(@NonNull FirestorePagingOptions<EntrenamientoModelo> options,
                                         OnButtonClick onButtonClick, ProgressBar progressBar, TextView mensajeCarga) {
        super(options);
        this.onButtonClick = onButtonClick;
        this.progressBar = progressBar;
        this.mensajeCarga = mensajeCarga;
    }

    FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolderEntrenamientos holder, int position, @NonNull EntrenamientoModelo model) {
        holder.deporte.setText(model.getDeporte());
        if(model.getDia_semana().length() > 25){
            holder.dia_semana.setText(": "+model.getDia_semana().substring(0, 26)+"...");
        }else{
            holder.dia_semana.setText(": "+model.getDia_semana());
        }
        holder.hora.setText(": "+model.getHora_inicio()+" - "+model.getHora_fin());

        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        db.collection("canchas").document(model.getCancha())//consulta la cancha de la publicacion en cuestion
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    //Log.d("Data-----", ""+task.getResult().get("imagen"));
                    storageRef.child("canchas/"+task.getResult().get("imagen")).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Glide.with(holder.cancha).load(uri).into(holder.cancha);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("Error con "+exception.getMessage());
                        }
                    });
                } else {
                    Log.d("Error", "Error getting documents: ", task.getException());
                }
            }
        });

    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        switch (state) {
            case LOADED:
                progressBar.setVisibility(View.GONE);
                break;
            case FINISHED:
                progressBar.setVisibility(View.GONE);
                mensajeCarga.setText(R.string.no_publicaciones);
                break;
        }
    }

    @NonNull
    @Override
    public ViewHolderEntrenamientos onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_entrenamiento, parent, false);
        return new ViewHolderEntrenamientos(view);
    }

    public class ViewHolderEntrenamientos extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cancha;
        TextView deporte, dia_semana, hora;
        //Button mas;

        public ViewHolderEntrenamientos(@NonNull View itemView) {
            super(itemView);
            cancha = itemView.findViewById(R.id.imgEntrena);
            deporte = itemView.findViewById(R.id.deporteEntrena);
            dia_semana = itemView.findViewById(R.id.diaEntrena);
            hora = itemView.findViewById(R.id.horaEntrena);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onButtonClick.onButtonClick(getItem(getAdapterPosition()), getAdapterPosition());
        }
    }
    public interface OnButtonClick{
        void onButtonClick(DocumentSnapshot snapshot, int position);
    }
}
