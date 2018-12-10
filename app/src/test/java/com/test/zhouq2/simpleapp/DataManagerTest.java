package com.test.zhouq2.simpleapp;

import android.provider.ContactsContract;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class DataManagerTest {
    static DataManager sDataManager;

    @BeforeClass
    public static void classSetup() throws Exception {
        sDataManager = DataManager.getInstance();
    }

    @Before
    public void setUp() throws Exception {
        sDataManager.getNotes().clear();
        sDataManager.initializeExampleNotes();
    }

    @Test
    public void createNewNote() throws Exception {
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Unit test title";
        final String noteText = "Unit test text";

        int noteIdx = sDataManager.createNewNote();
        NoteInfo newNote = sDataManager.getNotes().get(noteIdx);
        newNote.setCourse(course);
        newNote.setTitle(noteTitle);
        newNote.setText(noteText);

        NoteInfo compareNote = sDataManager.getNotes().get(noteIdx);

        assertEquals(newNote.getCourse(), compareNote.getCourse());
        assertEquals(newNote.getTitle(), compareNote.getTitle());
        assertEquals(newNote.getText(), compareNote.getText());
    }

    @Test
    public void findSimilarNotes() throws Exception {
        final CourseInfo course = sDataManager.getCourse("android_async");
        final String noteTitle = "Unit test title";
        final String noteText = "Unit test text1";
        final String noteText2 = "Unit test text2";

        int noteIdx1 = sDataManager.createNewNote();
        NoteInfo newNote1 = sDataManager.getNotes().get(noteIdx1);
        newNote1.setCourse(course);
        newNote1.setTitle(noteTitle);
        newNote1.setText(noteText);

        int noteIdx2 = sDataManager.createNewNote();
        NoteInfo newNote2 = sDataManager.getNotes().get(noteIdx2);
        newNote2.setCourse(course);
        newNote2.setTitle(noteTitle);
        newNote2.setText(noteText2);

        int foundIdx1 = sDataManager.findNote(newNote1);
        assertEquals(noteIdx1, foundIdx1);

        int foundIdx2 = sDataManager.findNote(newNote2);
        assertEquals(noteIdx2, foundIdx2);
    }
}