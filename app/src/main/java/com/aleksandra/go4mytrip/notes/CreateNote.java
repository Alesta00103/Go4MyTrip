package com.aleksandra.go4mytrip.notes;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.aleksandra.go4mytrip.R;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateNote extends AppCompatActivity {
    String title;
    String noteDate;
    String noteText;
    EditText title_et, noteText_et;
    TextView noteDate_tv;
    ImageView imageBack, imageDone;
    String selectedNoteColor;
    View viewSubtitleIndicator;
    ImageView imageNote;
    private TextView textWebURL;
    LinearLayout layoutWebURL;
    AlertDialog dialogAddURL;
    AlertDialog dialogDeleteNote;
    String textLink;
    String selectedImagePath;
    String id;
    NoteModel alreadyAvailableNote;

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        title_et = findViewById(R.id.inputTitle);
        noteText_et = findViewById(R.id.inputNote);
        imageBack = findViewById(R.id.backBtn);
        imageDone = findViewById(R.id.doneBtn);
        noteDate_tv = findViewById(R.id.textDate);
        viewSubtitleIndicator = findViewById(R.id.viewSubtitleIndicator);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);
        imageNote = findViewById(R.id.imageNote);

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault());
        noteDate = sdf.format(new Date());
        noteDate_tv.setText(noteDate);

        selectedNoteColor = "#333333";
        selectedImagePath = "";

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            id = getIntent().getStringExtra("id");
            String title = getIntent().getStringExtra("title");
            String text = getIntent().getStringExtra("text");
            String date = getIntent().getStringExtra("date");
            String color = getIntent().getStringExtra("color");
            String link = getIntent().getStringExtra("link");
            String image = getIntent().getStringExtra("image");
            NoteModel note = new NoteModel(id, title, text, date, color, link, image);
            alreadyAvailableNote = note;
            setViewOrUpdateNote();
        }
        imageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        imageDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = title_et.getText().toString();
                noteText = noteText_et.getText().toString();

                Intent intentNote = new Intent();
                if (alreadyAvailableNote != null) {
                    id = alreadyAvailableNote.getIdNote();
                    intentNote.putExtra("id", id);
                }
                intentNote.putExtra("date", noteDate);
                intentNote.putExtra("title", title);
                intentNote.putExtra("noteText", noteText);
                intentNote.putExtra("color", selectedNoteColor);
                intentNote.putExtra("image", selectedImagePath);

                //  if(layoutWebURL.getVisibility() == View.VISIBLE){
                textLink = textWebURL.getText().toString();
                intentNote.putExtra("link", textLink);
                //  }
                setResult(RESULT_OK, intentNote);
                finish();
            }
        });
        initChooseColor();
        setSubtitleIndicatorColor();

        findViewById(R.id.imageRemoveWebURL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textWebURL.setText(null);
                layoutWebURL.setVisibility(View.GONE);
            }
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageNote.setImageBitmap(BitmapFactory.decodeFile(null));
                imageNote.setVisibility(View.GONE);
                findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
                selectedImagePath = "";
            }
        });
    }

    private void setViewOrUpdateNote() {
        title_et.setText(alreadyAvailableNote.getNoteTitle());
        noteText_et.setText(alreadyAvailableNote.getTextNote());
        noteDate_tv.setText(alreadyAvailableNote.getDateTime());
        if (alreadyAvailableNote.getImage() != null && !alreadyAvailableNote.getImage().trim().isEmpty()) {
            imageNote.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImage()));
            imageNote.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImage();
        }
        if (alreadyAvailableNote.getLink() != null && !alreadyAvailableNote.getLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getLink());
            layoutWebURL.setVisibility(View.VISIBLE);
        }
    }

    private void initChooseColor() {
        final LinearLayout layoutNoteColor = findViewById(R.id.noteColorLayout);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(layoutNoteColor);
        layoutNoteColor.findViewById(R.id.textChooseColor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        final ImageView imageColor1 = layoutNoteColor.findViewById(R.id.imageColor1);
        final ImageView imageColor2 = layoutNoteColor.findViewById(R.id.imageColor2);
        final ImageView imageColor3 = layoutNoteColor.findViewById(R.id.imageColor3);
        final ImageView imageColor4 = layoutNoteColor.findViewById(R.id.imageColor4);
        final ImageView imageColor5 = layoutNoteColor.findViewById(R.id.imageColor5);

        layoutNoteColor.findViewById(R.id.viewColor1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#333333";
                imageColor1.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();

            }
        });

        layoutNoteColor.findViewById(R.id.viewColor2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FDBE3B";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();

            }
        });

        layoutNoteColor.findViewById(R.id.viewColor3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#FF4842";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();

            }
        });

        layoutNoteColor.findViewById(R.id.viewColor4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#3A52FC";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(R.drawable.ic_baseline_done_24);
                imageColor5.setImageResource(0);
                setSubtitleIndicatorColor();

            }
        });

        layoutNoteColor.findViewById(R.id.viewColor5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedNoteColor = "#000000";
                imageColor1.setImageResource(0);
                imageColor2.setImageResource(0);
                imageColor3.setImageResource(0);
                imageColor4.setImageResource(0);
                imageColor5.setImageResource(R.drawable.ic_baseline_done_24);
                setSubtitleIndicatorColor();

            }
        });

        if (alreadyAvailableNote != null && alreadyAvailableNote.getNoteColor() != null && !alreadyAvailableNote.getNoteColor().trim().isEmpty()) {
            switch (alreadyAvailableNote.getNoteColor()) {
                case "#FDBE3B":
                    layoutNoteColor.findViewById(R.id.viewColor2).performClick();
                    break;
                case "#FF4842":
                    layoutNoteColor.findViewById(R.id.viewColor3).performClick();
                    break;
                case "#3A52FC":
                    layoutNoteColor.findViewById(R.id.viewColor4).performClick();
                    break;
                case "#000000":
                    layoutNoteColor.findViewById(R.id.viewColor5).performClick();
                    break;
            }
        }

        layoutNoteColor.findViewById(R.id.layoutAddImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                if (ContextCompat.checkSelfPermission(
                        getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            CreateNote.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION
                    );
                } else {
                    selectImage();
                }
            }
        });

        layoutNoteColor.findViewById(R.id.layoutAddUrl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showAddURLDialog();

            }
        });

        if (alreadyAvailableNote != null) {
            layoutNoteColor.findViewById(R.id.layoutAddEvent).setVisibility(View.VISIBLE);
            layoutNoteColor.findViewById(R.id.layoutAddEvent).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    openCalendar();
                }
            });
        }

        if (alreadyAvailableNote != null) {
            layoutNoteColor.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            layoutNoteColor.findViewById(R.id.layoutDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    showDeleteNoteDialog();
                }
            });
        }
    }

    private void openCalendar() {

        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, title_et.getText().toString());


        if (textWebURL != null) {
            String eventLink = textWebURL.getText().toString();
            intent.putExtra(CalendarContract.Events.DESCRIPTION, noteText_et.getText().toString() + "\n" + eventLink);
        } else {
            intent.putExtra(CalendarContract.Events.DESCRIPTION, noteText_et.getText().toString());
        }
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, "Worldwide");

        intent.putExtra(CalendarContract.Events.ALL_DAY, true);


        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "There is no app that can support this action", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.layout_delete_note,
                    (ViewGroup) findViewById(R.id.layoutDeleteNoteContainer)
            );
            builder.setView(view);
            dialogDeleteNote = builder.create();
            if (dialogDeleteNote.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            view.findViewById(R.id.textDeleteNote).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentNote = new Intent();

                    id = alreadyAvailableNote.getIdNote();
                    intentNote.putExtra("id", id);
                    intentNote.putExtra("isNoteDeleted", true);
                    setResult(RESULT_OK, intentNote);
                    finish();
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogDeleteNote.dismiss();
                }
            });

        }
        dialogDeleteNote.show();
    }

    private void setSubtitleIndicatorColor() {
        GradientDrawable gradientDrawable = (GradientDrawable) viewSubtitleIndicator.getBackground();
        gradientDrawable.setColor(Color.parseColor(selectedNoteColor));
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_CODE_SELECT_IMAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SELECT_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        imageNote.setImageBitmap(bitmap);
                        imageNote.setVisibility(View.VISIBLE);
                        findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                        selectedImagePath = getPathFromUri(selectedImageUri);
                    } catch (Exception exception) {
                        Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private String getPathFromUri(Uri contentUri) {
        String filePath;
        Cursor cursor = getContentResolver()
                .query(contentUri, null, null, null, null);
        if (cursor == null) {
            filePath = contentUri.getPath();
        } else {
            cursor.moveToFirst();
            int index = cursor.getColumnIndex("_data");
            filePath = cursor.getString(index);
            cursor.close();
        }
        return filePath;


    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNote.this);
            View view = LayoutInflater.from(this).inflate(
                    R.layout.add_url_layout,
                    (ViewGroup) findViewById(R.id.layoutAddUrlContainer)
            );
            builder.setView(view);

            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText inputURL = view.findViewById(R.id.inputNameExpanse);
            inputURL.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inputURL.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNote.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(inputURL.getText().toString()).matches()) {
                        Toast.makeText(CreateNote.this, "Enter valid URL", Toast.LENGTH_SHORT).show();

                    } else {
                        textWebURL.setText(inputURL.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });
            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }
}