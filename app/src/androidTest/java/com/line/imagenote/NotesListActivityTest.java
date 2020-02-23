package com.line.imagenote;

import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;

import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
public class NotesListActivityTest extends BaseAndroidTestCase {


    @Rule
    public ActivityTestRule<NotesListActivity> activityRule = new ActivityTestRule<>(NotesListActivity.class);

    @Test
    public void createNoteList() {
        onView(withId(R.id.txt_empty)).check(matches((isDisplayed())));
        onView(allOf(withId(R.id.fab), isDisplayed())).perform(click());
    }


}
