package com.line.imagenote;

import android.content.Intent;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import com.line.imagenote.models.Note;

import org.junit.Rule;
import org.junit.Test;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
public class NoteActivityTest extends BaseAndroidTestCase {

    @Rule
    public ActivityTestRule<NoteActivity> activityRule = new ActivityTestRule<>(NoteActivity.class);


    @Test
    public void createNote() {
        activityRule.launchActivity(new Intent());

        onView(withId(R.id.et_title)).check(matches(withText("")));
        onView(withId(R.id.et_content)).check(matches(withText("")));

        onView(withId(R.id.view_pager)).check(matches(not(isDisplayed())));
        onView(withId(R.id.worm_dots_indicator)).check(matches(not(isDisplayed())));

        onView(allOf(withId(R.id.btn_delete),isDisplayed())).perform(click());

    }

    @Test
    public void updateNote() {
        Note note = new Note();
        Long noteId = dbHelper.getTime();

        note.setTimeCreated(noteId);
        note.setTimeModified(noteId);
        note.setTitle("sample title");
        note.setContent("sample content");

        dbHelper.insertNote(note);

        String uri = "https://cdn.pixabay.com/photo/2016/11/11/23/34/cat-1817970_960_720.jpg";
        dbHelper.insertAttachment(noteId, uri, "image");

        Intent intent = new Intent();
        intent.putExtra("noteId", noteId);
        intent.putExtra("isUpdate", true);
        activityRule.launchActivity(intent);

        onView(withId(R.id.et_title))
                .check(matches((withText("sample title"))));

        onView(withId(R.id.et_content))
                .check(matches((withText("sample content"))));

        onView(withId(R.id.view_pager)).check(matches((isDisplayed())));
        onView(withId(R.id.worm_dots_indicator)).check(matches((isDisplayed())));
    }





}
