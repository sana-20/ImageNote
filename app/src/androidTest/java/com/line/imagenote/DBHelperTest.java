/*
 * Copyright (C) 2013-2020 Federico Iosue (federico@iosue.it)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.line.imagenote;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.line.imagenote.models.Attachment;
import com.line.imagenote.models.Note;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class DBHelperTest extends BaseAndroidTestCase {


    @Test
    public void updateNote() {
        Note note = new Note();
        Long noteId = dbHelper.getTime();
        note.setTimeCreated(noteId);
        note.setTimeModified(noteId);
        note.setTitle("sample title");
        note.setContent("sample content");

        dbHelper.insertNote(note);
        ArrayList<Note> noteList = dbHelper.getAllNotes();

        assertThat(noteList.size(), is(1));

        Note updateNote = new Note();
        updateNote.setTimeCreated(noteId);
        updateNote.setTimeModified(dbHelper.getTime());
        updateNote.setTitle("sample title edited");
        updateNote.setContent("sample content edited");

        dbHelper.insertNote(updateNote);
        noteList = dbHelper.getAllNotes();

        assertThat(noteList.size(), is(1));
    }

    @Test
    public void deleteOneNote() {
        Note note = new Note();
        note.setTimeCreated(dbHelper.getTime());
        note.setTimeModified(dbHelper.getTime());
        note.setTitle("sample title");
        note.setContent("sample content");

        dbHelper.insertNote(note);
        ArrayList<Note> noteList = dbHelper.getAllNotes();

        assertThat(noteList.size(), is(1));

        dbHelper.deleteNote(note.getTimeCreated());
        noteList = dbHelper.getAllNotes();

        assertThat(noteList.size(), is(0));
    }

    @Test
    public void deleteAllNotes() {
        dbHelper.deleteAllNotes();
        ArrayList<Note> noteList = dbHelper.getAllNotes();

        assertThat(noteList.size(), is(0));
    }


    @Test
    public void deletePhoto() {
        Long noteId = dbHelper.getTime();
        dbHelper.insertAttachment(noteId, "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg", "image");
        ArrayList<Attachment> photoList = dbHelper.getAttachments(noteId, "image");

        assertThat(photoList.size(), is(1));

        dbHelper.deleteAttachment(1);
        photoList = dbHelper.getAttachments(noteId, "image");

        assertThat(photoList.size(), is(0));
    }


    @Test
    public void getOnlyOneThumbnail() {
        Note note = new Note();
        Long noteId = dbHelper.getTime();

        note.setTimeCreated(noteId);
        note.setTimeModified(noteId);
        note.setTitle("sample title");
        note.setContent("sample content");

        dbHelper.insertNote(note);

        String uri = "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg";
        dbHelper.insertAttachment(noteId, uri, "image");

        assertEquals(uri, dbHelper.getThumbnail(noteId));
    }

    @Test
    public void noThumbnail() {
        Note note = new Note();
        Long noteId = dbHelper.getTime();

        note.setTimeCreated(noteId);
        note.setTimeModified(noteId);
        note.setTitle("sample title");
        note.setContent("sample content");

        dbHelper.insertNote(note);

        assertEquals("", dbHelper.getThumbnail(noteId));
    }


}
