package com.deportes.sp1.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.deportes.sp1.R;
import com.deportes.sp1.models.PublicacionModelo;
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

public class PublicacionFirestoreAdapter extends FirestorePagingAdapter<PublicacionModelo, PublicacionFirestoreAdapter.ViewHolderPublicaciones> {

    private OnButtonClick onButtonClick;
    private int res;
    private Context context;
    private ProgressBar progressBar;
    private TextView mensajeCarga;

    public PublicacionFirestoreAdapter(@NonNull FirestorePagingOptions<PublicacionModelo> options,
                                       OnButtonClick onButtonClick, int res, Context context,
                                       ProgressBar progressBar, TextView mensajeCarga) {
        super(options);
        this.onButtonClick = onButtonClick;
        this.res = res;
        this.context = context;
        this.progressBar = progressBar;
        this.mensajeCarga = mensajeCarga;
    }

    private FirebaseFirestore db;
    private StorageReference storageRef;

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolderPublicaciones holder, int position, @NonNull PublicacionModelo model) {

        holder.deporte.setText(model.getDeporte());
        if(model.getDescripcion().length() > 100){
            holder.descripcion.setText(model.getDescripcion().substring(0, 101)+"...");
        }else{
            holder.descripcion.setText(model.getDescripcion());
        }

        holder.creado_en.setText(model.getCreado_en());

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
                            try {
                                Glide.with(context).load(uri).into(holder.cancha);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle any errors
                            System.out.println("Error con "+exception.getMessage());
                        }
                    });
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
    public ViewHolderPublicaciones onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        return new ViewHolderPublicaciones(view);
    }

    public class ViewHolderPublicaciones extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cancha;
        TextView deporte, descripcion, creado_en;

        public ViewHolderPublicaciones(@NonNull View itemView) {
            super(itemView);
            cancha = itemView.findViewById(R.id.imgCancha);
            deporte = itemView.findViewById(R.id.txtDeporte);
            descripcion = itemView.findViewById(R.id.txtDescripcion);
            creado_en = itemView.findViewById(R.id.txtCreado_en);

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
