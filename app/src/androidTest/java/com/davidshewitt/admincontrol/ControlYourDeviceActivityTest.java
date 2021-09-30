/*
 * AdminControl - Additional security settings for your Android device.
 * Copyright (C) 2018 Dave Hewitt
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of  MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.davidshewitt.admincontrol;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests the main settings activity.
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class ControlYourDeviceActivityTest {

    @Rule
    public ActivityTestRule<ControlYourDeviceActivity> mActivityTestRule = new ActivityTestRule<>(ControlYourDeviceActivity.class);

    /**
     * Tests the disableFingerprintLockscreen SwitchPreference.
     */
    @Test
    public void disableFingerprintLockscreenPreferenceTest() {
        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.list_container)
                        )))
                .atPosition(0);
        linearLayout.perform(click());

        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.list_container)
                        )))
                .atPosition(0);
        linearLayout2.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + 0 + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(0));
            }
        };
    }
}
