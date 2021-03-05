package ch.timonhueppi.m335.carimba.controller;


import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.*;
import static androidx.test.espresso.matcher.ViewMatchers.*;

import ch.timonhueppi.m335.carimba.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EspressoTests {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void A_signUp() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnLoginSignUp), withText("Registrieren"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                17),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tvSignUpTitle), withText("Registrieren"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView.check(matches(withText("Registrieren")));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.tiSignUpEmail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        3),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.tiSignUpPassword),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        5),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("password123"), closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnSignUpSignUp), withText("Registrieren"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                7),
                        isDisplayed()));
        appCompatButton2.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarsTitle), withText("Meine Autos"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView2.check(matches(withText("Meine Autos")));
    }

    @Test
    public void B_logOut() {
        ViewInteraction overflowMenuButton = onView(
                allOf(withContentDescription("More options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.my_toolbar),
                                        1),
                                1),
                        isDisplayed()));
        overflowMenuButton.perform(click());

        ViewInteraction appCompatTextView = onView(
                allOf(withId(R.id.title), withText("Logout"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        appCompatTextView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(R.id.tvLoginLogin), withText("Einloggen"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class))),
                        isDisplayed()));
        textView.check(matches(withText("Einloggen")));
    }

    @Test
    public void C_logIn() {
        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.tiLoginEmail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        9),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("test@test.com"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.tiLoginPassword),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        11),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(replaceText("password123"), closeSoftKeyboard());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btnLoginLogin), withText("Einloggen"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                        0),
                                13),
                        isDisplayed()));
        appCompatButton.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView = onView(
                allOf(withId(R.id.tvCarsTitle), withText("Meine Autos"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView.check(matches(withText("Meine Autos")));
    }

    @Test
    public void D_AddCar() {
        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnCarsAdd), withText("Hinzufügen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView = onView(
                allOf(withId(R.id.tvCarsTitle), withText("Auto hinzufügen"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView.check(matches(withText("Auto hinzufügen")));

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.ddCarYear),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                4),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(8);
        appCompatCheckedTextView.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction appCompatSpinner2 = onView(
                allOf(withId(R.id.ddCarMake),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                7),
                        isDisplayed()));
        appCompatSpinner2.perform(click());

        DataInteraction appCompatCheckedTextView2 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(4);
        appCompatCheckedTextView2.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction appCompatSpinner3 = onView(
                allOf(withId(R.id.ddCarModel),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                10),
                        isDisplayed()));
        appCompatSpinner3.perform(click());

        DataInteraction appCompatCheckedTextView3 = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(14);
        appCompatCheckedTextView3.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.tiCarAddTrim),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                13),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Quattro Coupé"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnCarAddFinished), withText("Fertig"),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                15),
                        isDisplayed()));
        appCompatButton3.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarsTitle), withText("Meine Autos"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView2.check(matches(withText("Meine Autos")));
    }

    @Test
    public void E_ShowCars(){
        ViewInteraction textView = onView(
                allOf(withId(R.id.tvCarPrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(withText("Audi RS 5")));
    }

    @Test
    public void F_ChooseCar() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Audi RS 5")));
    }

    @Test
    public void G_AddModification() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Audi RS 5")));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btnModsAdd), withText("Hinzufügen"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        3),
                                2),
                        isDisplayed()));
        appCompatButton2.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tvAddMod), withText("Modifikation hinzufügen"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView3.check(matches(withText("Modifikation hinzufügen")));

        ViewInteraction appCompatSpinner = onView(
                allOf(withId(R.id.ddAddModCategory),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                5),
                        isDisplayed()));
        appCompatSpinner.perform(click());

        DataInteraction appCompatCheckedTextView = onData(anything())
                .inAdapterView(childAtPosition(
                        withClassName(is("android.widget.PopupWindow$PopupBackgroundView")),
                        0))
                .atPosition(2);
        appCompatCheckedTextView.perform(click());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.tiAddModTitle),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                9),
                        isDisplayed()));
        appCompatEditText3.perform(replaceText("Rotiform CVT"), closeSoftKeyboard());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.tiAddModDetails),
                        childAtPosition(
                                allOf(withId(R.id.linearLayout),
                                        childAtPosition(
                                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                                1)),
                                13),
                        isDisplayed()));
        appCompatEditText4.perform(replaceText("8.5x20 ET45;245/35/R20"), closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.btnAddModFinished), withText("Fertig"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        17),
                                2),
                        isDisplayed()));
        appCompatButton3.perform(click());
        SystemClock.sleep(2000);
        textView2 = onView(
                allOf(withId(R.id.tvModPrimary), withText("Räder"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Räder")));
    }

    @Test
    public void H_showMods(){
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());

        SystemClock.sleep(2000);
        ViewInteraction textView = onView(
                allOf(withId(R.id.tvModPrimary), withText("Räder"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView.check(matches(withText("Räder")));
    }

    @Test public void I_chooseMod(){
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Audi RS 5")));

        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.btnModDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton2.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        textView2 = onView(
                allOf(withId(R.id.tvModTitle), withText("Rotiform CVT"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("Rotiform CVT")));
    }

    @Test
    public void J_deleteMod(){
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Audi RS 5")));
        ViewInteraction appCompatImageButton2 = onView(
                allOf(withId(R.id.btnModDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton2.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        textView2 = onView(
                allOf(withId(R.id.tvModTitle), withText("Rotiform CVT"),
                        withParent(allOf(withId(R.id.linearLayout),
                                withParent(IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class)))),
                        isDisplayed()));
        textView2.check(matches(withText("Rotiform CVT")));

        ViewInteraction appCompatImageButton3 = onView(
                allOf(withId(R.id.btnModDelete),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                2),
                        isDisplayed()));
        appCompatImageButton3.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView3 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView3.check(matches(withText("Audi RS 5")));
    }

    @Test
    public void K_deleteCar() {
        ViewInteraction appCompatImageButton = onView(
                allOf(withId(R.id.btnCarDetail),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2)));
        appCompatImageButton.perform(scrollTo(), click());
        SystemClock.sleep(2000);
        ViewInteraction textView2 = onView(
                allOf(withId(R.id.tvCarTitlePrimary), withText("Audi RS 5"),
                        withParent(withParent(IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class))),
                        isDisplayed()));
        textView2.check(matches(withText("Audi RS 5")));

        ViewInteraction appCompatImageButton4 = onView(
                allOf(withId(R.id.btnCarDelete),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.linearLayout),
                                        1),
                                2),
                        isDisplayed()));
        appCompatImageButton4.perform(click());
        SystemClock.sleep(2000);
        ViewInteraction textView4 = onView(
                allOf(withId(R.id.tvCarsTitle), withText("Meine Autos"),
                        withParent(withParent(withId(R.id.linearLayout))),
                        isDisplayed()));
        textView4.check(matches(withText("Meine Autos")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
