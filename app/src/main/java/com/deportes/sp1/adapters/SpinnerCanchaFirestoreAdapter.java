package com.deportes.sp1.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.deportes.sp1.R;
import com.deportes.sp1.models.CanchaModelo;
import com.firebase.ui.firestore.paging.FirestorePagingAdapter;
import com.firebase.ui.firestore.paging.FirestorePagingOptions;
import com.firebase.ui.firestore.paging.LoadingState;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpinnerCanchaFirestoreAdapter extends FirestorePagingAdapter<CanchaModelo, SpinnerCanchaFirestoreAdapter.ViewHolderCanchas> {
    private OnButtonClick onButtonClick;
    private int res;
    private ProgressBar progressBar;
    private TextView mensajeCarga;

    public SpinnerCanchaFirestoreAdapter(@NonNull FirestorePagingOptions<CanchaModelo> options, OnButtonClick onButtonClick, int res) {
        super(options);
        this.onButtonClick = onButtonClick;
        this.res = res;
    }
    public SpinnerCanchaFirestoreAdapter(@NonNull FirestorePagingOptions<CanchaModelo> options, OnButtonClick onButtonClick, int res, ProgressBar progressBar, TextView mensajeCarga) {
        super(options);
        this.onButtonClick = onButtonClick;
        this.res = res;
        this.progressBar = progressBar;
        this.mensajeCarga = mensajeCarga;
    }

    private StorageReference storageRef;

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolderCanchas holder, int position, @NonNull CanchaModelo model) {
        holder.barrio.setText(": "+model.getBarrio());
        holder.direccion.setText(": "+model.getDireccion());
        holder.iluminacion.setText(": "+model.getIluminacion());
        holder.estado_cancha.setText(": "+model.getEstado_cancha());

        storageRef = FirebaseStorage.getInstance().getReference();

        storageRef.child("canchas/"+model.getImagen()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(holder.cancha).load(uri).into(holder.cancha);// descargo la imagen de la cancha usando Glide y el nombre lo saco del model
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                System.out.println("Error con "+exception.getMessage());
            }
        });


    }

    @Override
    protected void onLoadingStateChanged(@NonNull LoadingState state) {
        if(progressBar != null & mensajeCarga != null){
            switch (state) {
                case LOADED:
                    progressBar.setVisibility(View.GONE);
                    break;
                case FINISHED:
                    progressBar.setVisibility(View.GONE);
                    mensajeCarga.setText(R.string.no_canchas);
                    break;
            }
        }
    }

    @NonNull
    @Override
    public ViewHolderCanchas onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(res, parent, false);
        return new ViewHolderCanchas(view);
    }

    public class ViewHolderCanchas extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView cancha;
        TextView barrio, direccion, iluminacion, estado_cancha;

        public ViewHolderCanchas(@NonNull View itemView) {
            super(itemView);
            cancha = itemView.findViewById(R.id.imgSpinner);
            barrio = itemView.findViewById(R.id.spinnerBarrio);
            direccion = itemView.findViewById(R.id.spinnerDireccion);
            iluminacion = itemView.findViewById(R.id.spinnerIluminacion);
            estado_cancha = itemView.findViewById(R.id.spinnerEstadoCancha);

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
