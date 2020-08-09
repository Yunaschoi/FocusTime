package com.example.focustime.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import com.bumptech.glide.Glide;
import com.example.focustime.Activity.MySayingActivity;
import com.example.focustime.Activity.ProfileGalleryActivity;
import com.example.focustime.ItemClickListener;
import com.example.focustime.MySayingItem;
import com.example.focustime.R;
import com.example.focustime.Activity.MySayingAnotherActivity;
import com.example.focustime.clickListener2;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MySayingAdapter extends RecyclerView.Adapter<MySayingAdapter.MyHolder> {


    Context c;
    ArrayList<MySayingItem> models;

    private clickListener mListener;
    private clickListener2 mListener2;

    public interface clickListener {
        void reviseBtnClick(int positon, View view);

    }

    public interface clickListener2 {
        void garbageClick(int positon, View view);

    }

    public void setOnItemClickListener(
            clickListener listener) {
        mListener = listener;

    }

    public void setOnItemClickListener(
            clickListener2 listener2) {
        mListener2 = listener2;

    }


    public MySayingAdapter(Context c, ArrayList<MySayingItem> models) {
        this.c = c;
        this.models = models;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {


        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.my_saying_item,null); // inflate our mySayintItem

        return new MyHolder(view,mListener,mListener2);  // item 뷰가 홀더로 리턴 될 것이다
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, int position) {


      //  holder.mPublisher.setText(models.get(position).getPublisher());

        holder.mCreateAt.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(models.get(position).getCreateAt()));  // 작성 날짜를 담아 줌
        // 날짜를 2019.10.11 이런 형식으로 가져오기 위해 SimpleDateFormat

        holder.mTitle.setText(models.get(position).getTitle());
        holder.mMemo.setText(models.get(position).getMemo());
        //  holder.mimageView.setImageResource(models.get(position).getImg());  // 이미지 리소스가 사용되어질곳 drawble 할 것 이므로

        // 이미지를 담아 줄 코드

        /*
        if(holder.models.get(position).getMemoUri().length() > 0) {

            Glide.with((MySayingActivity)c).load(models.get(position).getMemoUri()).centerCrop().override(800).into(holder.itemView1);
        } else {
            Glide.with((MySayingActivity)c).load(R.drawable.memo).centerCrop().override(800).into(holder.itemView1);
        }

         */

        Glide.with((MySayingActivity)c).load(models.get(position).getMemoUri()).centerCrop().override(800).into(holder.itemView1);

      //  Glide.with(holder.itemView).load(models.get(position).getMemoUri()).centerCrop().override(800).into(holder.itemView);

        // friends this method is than you can use when you want to use one activity
        //  선택한 아이템의 액티비티를 보고 싶을 때 이 메소드를 사용
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                String gDate =(new SimpleDateFormat("yyyy.MM.dd\n  HH:mm:ss", Locale.getDefault())
                        .format(models.get(position).getCreateAt()));

                String gTitle = models.get(position).getTitle();
                String gMemo = models.get(position).getMemo();   // these object get our data from previous activity
                String gImage = models.get(position).getMemoUri();

            //    BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.mimageView.getDrawable();  // this will get our image from drawable

            //    Bitmap bitmap = bitmapDrawable.getBitmap();

            //    ByteArrayOutputStream stream = new ByteArrayOutputStream();  // image will get stream and bytes

            //    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);  // 이미지를 압축

            //    byte[] bytes = stream.toByteArray();

                //get our data with intent
                Intent intent = new Intent(c, MySayingAnotherActivity.class);
                intent.putExtra("iTitle",gTitle);
                intent.putExtra("iMemo",gMemo);  // get data and put in intent
                intent.putExtra("iDate",gDate);
                intent.putExtra("iImage",gImage);
                c.startActivity(intent);
            }
        });

        /*

        // if you want to use different activities than you can use the logic
        // 만약 다른 액티비트 사용하는 것을 원하면 이 로직을 사용하면 됨

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {

                if(models.get(position).getTitle().equals("과제")) {

                    // then you can move another activity from if body
                }
                if(models.get(position).getTitle().equals("날씨")) {

                    // then you can move another activity from if body
                }
                if(models.get(position).getTitle().equals("학원 수업시간")) {

                    // then you can move another activity from if body
                }
                if(models.get(position).getTitle().equals("하브루타")) {

                    // then you can move another activity from if body
                }
                if(models.get(position).getTitle().equals("저녁")) {

                    // then you can move another activity from if body
                }
            }
        });

        // 하지만 여전히 하나의 activity 를 사용하고 있으므로 주석 처리

         */
    }

    @Override
    public int getItemCount() {

        return (models != null ? models.size() : 0);  // models 이 null 이 아니면 model.size() : 0 을 실행
    }


    public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener
    {

     //   ImageView mimageView;  // 비트맵으로 했을 때

     //   ImageView iv_profileImage;  // url 주소로 불러올 때

        TextView mTitle;
        TextView mMemo;
        TextView mPublisher;
        TextView mCreateAt;
        ImageView itemView1;


        ImageView garbageView;
        ImageView mTryNameRevise;

        ItemClickListener itemClickListener;


         MyHolder(@NonNull View itemView, final clickListener listener, final clickListener2 listener2) {
            super(itemView);

        //    this.iv_profileImage = itemView.findViewById(R.id.imageMemo);

             // mySayingItem.xml 의 아이디 값
            this.mTitle = itemView.findViewById(R.id.memoTitle);
            this.mMemo = itemView.findViewById(R.id.MemoTextView);
            this.mCreateAt = itemView.findViewById(R.id.Date);
            this.itemView1 = itemView.findViewById(R.id.imageMemo);
            this.mTryNameRevise= itemView.findViewById(R.id.rebuild);
            this.garbageView = itemView.findViewById(R.id.garbage);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
            // OnCreateContextMenuListener 리스너를 현재 클래스에서 구현한다고 설정

             mTryNameRevise.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if(listener != null) {
                         int positon = getAdapterPosition();
                         if(positon != RecyclerView.NO_POSITION) {

                             listener.reviseBtnClick(positon,view);
                         }
                     }
                 }
             });

             garbageView.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View view) {
                     if(listener2 != null) {
                         int positon = getAdapterPosition();
                         if(positon != RecyclerView.NO_POSITION) {

                             listener2.garbageClick(positon,view);
                         }
                     }
                 }
             });




        }


        @Override
        public void onClick(View view) {

             this.itemClickListener.onItemClickListener(view, getLayoutPosition());


        }

        public void setItemClickListener(ItemClickListener ic) {
            this.itemClickListener = ic;
        }


        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

             // 3. 컨텍스트 메뉴를 생성하고 메뉴 항목 선택시 호출되는 리스너를 등록해줍니다.
            // ID 1001, 1002로 어떤 메뉴를 선택했는지 리스너에서 구분하게 됩니다.

          //  MenuItem Edit = menu.add(Menu.NONE, 1001, 1, "편집");
            MenuItem Delete = menu.add(Menu.NONE, 1002, 2, "삭제");
         //   Edit.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);
        }


        private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {


                switch (item.getItemId()) {



                    case 1001:  // 5. 편집 항목을 선택시

                        AlertDialog.Builder builder = new AlertDialog.Builder(c);

                        // 다이얼로그를 보여주기 위해 activity_floating_action.xml 파일을 사용합니다.

                        View view = LayoutInflater.from(c)
                                .inflate(R.layout.activity_floating_action, null, false);
                        builder.setView(view);

                        final Button ButtonSubmit = view.findViewById(R.id.saveButton);
                        final EditText cTitle = view.findViewById(R.id.titleEditText);
                        final EditText cMemo = view.findViewById(R.id.contentEditText);

                        final ImageView cProfileImage = (ImageView)view.findViewById(R.id.memo_imageView);


                        // 6. 해당 줄에 입력되어 있던 데이터를 불러와서 다이얼로그에 보여줍니다.
                        cTitle.setText(models.get(getAdapterPosition()).getTitle());
                        cMemo.setText(models.get(getAdapterPosition()).getMemo());

                      //  cProfileImage.setImageURI(Uri.parse(models.get(getAdapterPosition()).getMemoUri()));

                        Glide.with((MySayingActivity)c).load(models.get(getAdapterPosition()).getMemoUri()).centerCrop().override(800).into(cProfileImage);

                        // 다이얼로그 안에 있는 이미지를 눌렀을 때 갤러리 액티비티로 이동...
                        cProfileImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Intent intent = new Intent(c,ProfileGalleryActivity.class);
                                ((MySayingActivity)c).startActivity(intent);


                            }
                        });




                        final AlertDialog dialog = builder.create();
                        ButtonSubmit.setOnClickListener(new View.OnClickListener() {


                            // 7. 수정 버튼을 클릭하면 현재 UI에 입력되어 있는 내용으로

                            public void onClick(View v) {
                                String strTitle = cTitle.getText().toString();
                                String strMemo = cMemo.getText().toString();


                               // Intent intent = new Intent(c, ProfileGalleryActivity.class);
                               // intent = ((ProfileGalleryActivity) c).getIntent();
                               // ((ProfileGalleryActivity)c).startActivityForResult(intent,0);
                               // String strProfileImage = intent.getDataString(); // string

                              //  String strProfileImage = intent.getData();  // uri

                                FirebaseUser user; // 현재 로그인한 유저를 확인하는 용도
                                user =  FirebaseAuth.getInstance().getCurrentUser();

                                // 이미지도 수정할 수 있게 봐꿔 줘야됨 !!!!

                                MySayingItem mySayingItem = new MySayingItem(strTitle, strMemo,user.getUid(),new Date());

                                // 8. ListArray에 있는 데이터를 변경하고
                                models.set(getAdapterPosition(), mySayingItem);

                                // 9. 어댑터에서 RecyclerView에 반영하도록 합니다.

                                notifyItemChanged(getAdapterPosition());

                                dialog.dismiss();
                            }
                        });

                        dialog.show();

                        break;

                    case 1002:

                        models.remove(getAdapterPosition());
                        notifyItemRemoved(getAdapterPosition());
                        notifyItemRangeChanged(getAdapterPosition(), models.size());

                        break;

                }
                return true;

            }

        };


    }
}
