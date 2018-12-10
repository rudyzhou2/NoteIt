package com.test.zhouq2.simpleapp;

import android.provider.ContactsContract;
import android.support.test.espresso.ViewInteraction;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.rule.*;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class NoteCreationTest {
    static DataManager sDataManager;

    @BeforeClass
    public static void classSetup() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Rule
    public ActivityTestRule<NoteListActivity> mNoteListActivityRule =
            new ActivityTestRule<>(NoteListActivity.class);

    @Test
    public void createNewNote() {
        final CourseInfo course = sDataManager.getCourse("java_lang");
        final String noteTitle = "Test note title";
        final String noteText = "Test note text";
        onView(withId(R.id.fab)).perform(click());
        // open course spinner, select specific course
        onView(withId(R.id.spinner_courses)).perform(click());
        onData(allOf(instanceOf(CourseInfo.class), equalTo(course))).perform(click());
        onView(withId(R.id.spinner_courses))
                .check(matches(withSpinnerText(containsString(course.getTitle()))));

        onView(withId(R.id.text_note_title)).perform(typeText(noteTitle));

        onView(withId(R.id.text_note_text)).perform(typeText(noteText), closeSoftKeyboard());

        pressBack();

        int noteIdx = sDataManager.getNotes().size()-1;
        NoteInfo note = sDataManager.getNotes().get(noteIdx);
        assertEquals(course, note.getCourse());
        assertEquals(noteTitle, note.getTitle());
        assertEquals(noteText, note.getText());
    }
}