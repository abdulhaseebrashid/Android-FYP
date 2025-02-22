    package com.example.buddypunchclone;

    import android.content.Context;
    import android.content.Intent;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.recyclerview.widget.RecyclerView;

    import java.util.List;

    public class CameraAdapter extends RecyclerView.Adapter<CameraAdapter.CameraViewHolder> {
        private final List<com.example.buddypunchclone.Camera> cameras;
        private final Context context;

        public CameraAdapter(Context context, List<com.example.buddypunchclone.Camera> cameras) {
            this.context = context;
            this.cameras = cameras;
        }

        @NonNull
        @Override
        public CameraViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.layout_camera_item, parent, false);
            return new CameraViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CameraViewHolder holder, int position) {
            Camera camera = cameras.get(position);
            holder.cameraName.setText(camera.getName());
            holder.viewButton.setOnClickListener(v -> {
                Intent intent = new Intent(context, LiveFeedActivity.class);
                intent.putExtra("cameraUrl", camera.getUrl());
                context.startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return cameras.size();
        }

        static class CameraViewHolder extends RecyclerView.ViewHolder {
            TextView cameraName;
            Button viewButton;

            CameraViewHolder(View itemView) {
                super(itemView);
                cameraName = itemView.findViewById(R.id.cameraName);
                viewButton = itemView.findViewById(R.id.viewButton);
            }
        }
    }