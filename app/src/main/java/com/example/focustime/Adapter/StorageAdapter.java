package com.example.focustime.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.focustime.ItemClickListener;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.StorageItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class StorageAdapter extends RecyclerView.Adapter<StorageAdapter.StorageHolder> {


    ArrayList<StorageItem> storageItems;
    Context c;


   private OnItemClickListener mListener;

   public interface OnItemClickListener {
        void reviseBtnClick(int positon);
    }

   public void setOnItemClickListener(
           OnItemClickListener listener) {
       mListener = listener;
   }

    public StorageAdapter(ArrayList<StorageItem> storageItems, Context c) {
        this.storageItems = storageItems;
        this.c = c;

    }


    @NonNull
    @Override
    public StorageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.save_record, null);

        return new StorageHolder(view,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull StorageHolder holder, int position) {
        holder.mStartTime.setText(storageItems.get(position).getStartTime());  // 시작시간
        holder.mSaveTime.setText(storageItems.get(position).getSaveTime());    // 저장시간
        holder.mTryName.setText(storageItems.get(position).getTryTitle());     // 타이틀 제목

    }

    @Override
    public int getItemCount() {
        return storageItems.size();
    }

    public class StorageHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener{
        TextView mStartTime;
        TextView mSaveTime;
        TextView mTryName;
        ImageView mTryNameRevise;
        FirebaseUser user;
        Date currentTime = Calendar.getInstance().getTime();
        String date_text = new SimpleDateFormat("yyyy.MM.dd. EE", Locale.getDefault()).format(currentTime);
        JSONArray jArray;

        public StorageHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            this.mStartTime = itemView.findViewById(R.id.startTime);
            this.mTryName = itemView.findViewById(R.id.tryTitle);
            this.mSaveTime = itemView.findViewById(R.id.saveTime);
            this.mTryNameRevise = itemView.findViewById(R.id.tryTitleRevise);  // 타이틀 수정해주는 이미지뷰

            itemView.setOnCreateContextMenuListener(this);

           mTryNameRevise.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if(listener != null) {
                       int positon = getAdapterPosition();
                       if(positon != RecyclerView.NO_POSITION) {

                            listener.reviseBtnClick(positon);
                       }
                   }
               }
           });

        }


        @Override
        public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
            Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);


        }

        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case 1001:  // 5. 편집 항목을 선택시


                        AlertDialog.Builder builder = new AlertDialog.Builder(c);

                        // 다이얼로그를 보여주기 위해 edit_box.xml 파일을 사용합니다.

                        View view = LayoutInflater.from(c)
                                .inflate(R.layout.edit_save_record, null, false);
                        builder.setView(view);

                        final Button ButtonSubmit = (Button) view.findViewById(R.id.edit_saveBtn);
                        final Button cancelButton = (Button) view.findViewById(R.id.edit_cancelBtn);
                        final EditText cTitle = (EditText) view.findViewById(R.id.recoreTitleEditText);

                        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                        cTitle.setText(storageItems.get(getAdapterPosition()).getTryTitle());

                        final AlertDialog dialog = builder.create();

                        cancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                            }
                        });


                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                            // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                            public void onClick(View v) {
                                String strTitle = cTitle.getText().toString();
                                String startTime = storageItems.get(getAdapterPosition()).getStartTime();
                                String saveTime =storageItems.get(getAdapterPosition()).getSaveTime();
                                String createAt = storageItems.get(getAdapterPosition()).getCreateAt();
                                // 생성자 구조 변경 및 데이터 추가해줘야 됨!!!!!!!!!!!!

                                StorageItem storageItem = new StorageItem(startTime,saveTime,strTitle,createAt);
                             //   storageItem.setTryTitle(strTitle);
                             //   storageItem.setStartTime(startTime);
                             //   storageItem.setSaveTime(saveTime);


                                // 8. ListArray에 있는 데이터를 변경하고
                                storageItems.set(getAdapterPosition(), storageItem);

                                // 9. 어댑터에서 RecyclerView에 반영하도록 합니다.

                                notifyItemChanged(getAdapterPosition());



                                user =  FirebaseAuth.getInstance().getCurrentUser();
                                //  notifyDataSetChanged();

                                SharedPreferences sharedPreferences = c.getSharedPreferences("mFile",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();

                                // JSON 으로 변환
                                try {
                                    jArray = new JSONArray();//배열
                                    for (int i = 0; i < 1; i++) {
                                        JSONObject sObject = new JSONObject();//배열 내에 들어갈 json
                                        sObject.put("startTime", startTime);
                                        sObject.put("tryTitle", strTitle);
                                        sObject.put("saveTime", saveTime);
                                        sObject.put("createAt",createAt);
                                        jArray.put(sObject);
                                    }

                                    Log.e("JSON Test", jArray.toString());

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                editor.putString(user.getEmail()+date_text,jArray.toString());
                                editor.apply();

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        user =  FirebaseAuth.getInstance().getCurrentUser();

                        storageItems.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), storageItems.size());


                        SharedPreferences sharedPreferences = c.getSharedPreferences("mFile",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.remove(user.getEmail()+date_text);
                        editor.apply();


                        break;

                }
                return true;

            }

        };


    }


}

