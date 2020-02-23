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

import com.line.imagenote.models.Note;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class UnitTest {


  protected Note getNote (Long timeCreated, Long timeModified, String title, String content) {
    Note note = new Note();
    note.setTimeCreated(timeCreated);
    note.setTimeModified(timeModified);
    note.setTitle(title);
    note.setContent(content);
    return note;
  }

  @Test
  public void checkIdDuplicate () {
    Note note1 = getNote(1L, 1L, "test title", "test content");
    Note note2 = getNote(2L, 2L, "test title", "test content");
    assertFalse(Note.checkIdDuplicate(note1, note2));

    Note note3 = getNote(3L, 3L, "test title", "test content");
    Note note4 = getNote(3L, 3L,"different test title", "different test content");
    assertTrue(Note.checkIdDuplicate(note3, note4));

  }

  @Test
  public void checkTime () {
    Note note = getNote(1L, 2L, "test title", "test content");
    assertTrue(Note.checkTime(note));
  }

}