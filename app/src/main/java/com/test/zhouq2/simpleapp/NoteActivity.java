package com.test.zhouq2.simpleapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.List;

public class NoteActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    public static final String NOTE_POSITION = "com.test.zhouq2.simpleapp.NOTE_POSITION";
    public static final String INIT_NOTE_COURSE_ID ="com.test.zhouq2.simpleapp.INIT_NOTE_COURSE_ID";
    public static final String INIT_NOTE_TITLE = "com.test.zhouq2.simpleapp.INIT_NOTE_TITLE";
    public static final String INIT_NOTE_TEXT = "com.test.zhouq2.simpleapp.INIT_NOTE_TEXT";
    public static final int POSITION_NOT_SET = -1;
    private NoteInfo mNote;
    private boolean mIsNewNote;
    private Spinner mSpinnerCourses;
    private EditText mTextNoteTitle;
    private EditText mTextNoteText;
    private int mNotePosition;
    private boolean mIsCancelling;
    private String mInitNoteCourseId;
    private String mInitNoteTitle;
    private String mInitNoteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSpinnerCourses = (Spinner) findViewById(R.id.spinner_courses);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        ArrayAdapter<CourseInfo> adapterCourses =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapterCourses.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerCourses.setAdapter(adapterCourses);

        readDisplayStateValues();
        // check saved instance state and restore if exists
        if (savedInstanceState == null) {
            saveInitialNoteValues();
        } else {
            restoreInitNoteVal(savedInstanceState);
        }
        mTextNoteTitle = (EditText) findViewById(R.id.text_note_title);
        mTextNoteText = (EditText) findViewById(R.id.text_note_text);

        if (!mIsNewNote)
            displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);

        Log.d(TAG, "onCreate");
    }

    private void restoreInitNoteVal(Bundle savedInstanceState) {
        mInitNoteCourseId = savedInstanceState.getString(INIT_NOTE_COURSE_ID);
        mInitNoteTitle = savedInstanceState.getString(INIT_NOTE_TITLE);
        mInitNoteText = savedInstanceState.getString(INIT_NOTE_TEXT);
    }

    private void saveInitialNoteValues() {
        if (mIsNewNote)
            return;
        mInitNoteCourseId = mNote.getCourse().getCourseId();
        mInitNoteTitle = mNote.getTitle();
        mInitNoteText = mNote.getText();
    }

    private void displayNote(Spinner spinnerCourses, EditText textNoteTitle, EditText textNoteText) {
        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIdx = courses.indexOf(mNote.getCourse());
        spinnerCourses.setSelection(courseIdx);
        textNoteTitle.setText(mNote.getTitle());
        textNoteText.setText(mNote.getText());
    }

    private void readDisplayStateValues() {
        Intent intent = getIntent();
        mNotePosition = intent.getIntExtra(NOTE_POSITION, POSITION_NOT_SET);
        mIsNewNote = mNotePosition == POSITION_NOT_SET;
        if (mIsNewNote) {
            createNewNote();
        }

        Log.i(TAG, "mNotePosition: " + mNotePosition);
        mNote = DataManager.getInstance().getNotes().get(this.mNotePosition);
    }

    private void createNewNote() {
        DataManager dm = DataManager.getInstance();
        mNotePosition = dm.createNewNote();
        // mNote = dm.getNotes().get(mNotePosition);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_mail) {
            sendEmail();
            return true;
        } else if (id == R.id.action_cancel) {
            mIsCancelling = true;
            finish();
        } else if (id == R.id.action_next) {
            moveNext();
        }

        return super.onOptionsItemSelected(item);
    }

    // only get called initially when menu is displayed
    // call invalidateOptionsMenu() to refresh
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.findItem(R.id.action_next);
        int lastNoteIdx = DataManager.getInstance().getNotes().size() - 1;
        menuItem.setEnabled(mNotePosition < lastNoteIdx);
        return super.onPrepareOptionsMenu(menu);
    }

    // move to next note
    private void moveNext() {
        saveNote();
        mNotePosition++;
        mNote = DataManager.getInstance().getNotes().get(mNotePosition);

        saveInitialNoteValues();
        displayNote(mSpinnerCourses, mTextNoteTitle, mTextNoteText);
        invalidateOptionsMenu();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mIsCancelling) {
            Log.i(TAG, "Cancelling note at pos: " + mNotePosition);
            if (mIsNewNote) {
                DataManager.getInstance().removeNote(mNotePosition);
            } else {
                storePrevNoteVal();
            }
        } else {
            saveNote();
        }
        Log.d(TAG, "onPause");
    }

    private void storePrevNoteVal() {
        CourseInfo course = DataManager.getInstance().getCourse(mInitNoteCourseId);
        mNote.setCourse(course);
        mNote.setTitle(mInitNoteTitle);
        mNote.setText(mInitNoteText);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(INIT_NOTE_COURSE_ID, mInitNoteCourseId);
        outState.putString(INIT_NOTE_TITLE, mInitNoteTitle);
        outState.putString(INIT_NOTE_TEXT, mInitNoteText);
    }

    private void saveNote() {
        mNote.setCourse((CourseInfo) mSpinnerCourses.getSelectedItem());
        mNote.setTitle(mTextNoteTitle.getText().toString());
        mNote.setText(mTextNoteText.getText().toString());
    }

    private void sendEmail() {
        CourseInfo course = (CourseInfo) mSpinnerCourses.getSelectedItem();
        String subject = mTextNoteTitle.getText().toString();
        String text = mTextNoteText.getText().toString();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
}
