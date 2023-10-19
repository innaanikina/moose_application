package com.example.mooseapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PredictionAdapter extends RecyclerView.Adapter<PredictionAdapter.PredictionViewHolder> {

    private List<Prediction> predictionList;
    private Context context;

    public PredictionAdapter(List<Prediction> predictionList, Context context) {
        this.predictionList = predictionList;
        this.context = context;
    }

    @NonNull
    @Override
    public PredictionAdapter.PredictionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prediction, parent, false);
        return new PredictionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PredictionViewHolder holder, int position) {
        Prediction prediction = predictionList.get(position);

        holder.cameraIdTextView.setText("Камера: " + prediction.getCamera_id());
        holder.predictionTimeTextView.setText(prediction.getPrediction_time().toString());
        holder.mooseCountTextView.setText("Лоси: " + prediction.getMoose_count());
        holder.bearCountTextView.setText("Медведи: " + prediction.getBear_count());
        holder.hogCountTextView.setText("Кабаны: " + prediction.getHog_count());
        holder.lynxCountTextView.setText("Рыси: " + prediction.getLynx_count());

        byte[] imageBytes = Base64.decode(prediction.getPhoto(), Base64.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        holder.photoImageView.setImageBitmap(bitmap);
    }

    @Override
    public int getItemCount() {
        return predictionList.size();
    }

    public static class PredictionViewHolder extends RecyclerView.ViewHolder {
        TextView cameraIdTextView;
        TextView predictionTimeTextView;
        TextView mooseCountTextView;
        TextView bearCountTextView;
        TextView hogCountTextView;
        TextView lynxCountTextView;
        ImageView photoImageView;

        public PredictionViewHolder(@NonNull View itemView) {
            super(itemView);

            cameraIdTextView = itemView.findViewById(R.id.camera_id_text_view);
            predictionTimeTextView = itemView.findViewById(R.id.prediction_time_text_view);
            mooseCountTextView = itemView.findViewById(R.id.deer_count_text_view);
            bearCountTextView = itemView.findViewById(R.id.bear_count_text_view);
            hogCountTextView = itemView.findViewById(R.id.hog_count_text_view);
            lynxCountTextView = itemView.findViewById(R.id.lynx_count_text_view);
            photoImageView = itemView.findViewById(R.id.photo_image_view);
        }
    }
}
