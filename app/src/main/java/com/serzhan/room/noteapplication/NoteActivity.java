package com.serzhan.room.noteapplication;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import com.serzhan.room.noteapplication.database.NoteDatabase;
import com.serzhan.room.noteapplication.entity.Note;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NoteActivity extends AppCompatActivity {

    private static String EXTRA_NOTE_ID = "extra_note_id";

    private EditText mEditText;
    private NoteDatabase db;
    private ExecutorService executorService;
    private Handler handler;
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        mEditText = findViewById(R.id.activity_note_content_edit_text);
        executorService = Executors.newSingleThreadExecutor();
        db = NoteDatabase.getInstance(NoteActivity.this);
        handler = new Handler(Looper.getMainLooper());
        getNote();
    }

    private void getNote() {
        int id = getIntent().getExtras().getInt(EXTRA_NOTE_ID);
        executorService.execute(() -> {
            note = db.getNoteDao().getNoteByID(id);
            handler.post(() -> mEditText.setText(note.getContent()));
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_note_menu_apply_item: {
                updateNote();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateNote() {
        executorService.execute(() -> {
            note.setContent(mEditText.getText().toString());
            db.getNoteDao().update(note);
            finish();
        });
    }

    public static Intent newIntent(Context context, long id) {
        Intent intent = new Intent(context, NoteActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(EXTRA_NOTE_ID, id);
        intent.putExtras(bundle);
        return intent;
    }
}
