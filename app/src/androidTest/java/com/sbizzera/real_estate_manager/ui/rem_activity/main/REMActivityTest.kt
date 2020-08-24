package com.sbizzera.real_estate_manager.ui.rem_activity.main

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.ActivityTestRule
import com.sbizzera.real_estate_manager.R
import com.sbizzera.real_estate_manager.data.repository.SharedPreferencesRepo
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4ClassRunner::class)
class REMActivityTest {
    @Rule
    @JvmField
    val activityTestRule = ActivityTestRule(REMActivity::class.java)

    @Test
    fun userNotLoggedShouldPopChooseUserDialog() {
        try {
            onView(withId(R.id.more_menu)).perform(click())
            onView(withText(R.string.log_out)).perform(click())
        } catch (e: Exception) { }
        onView(withId(R.id.username_edt)).check(matches(isDisplayed()))
    }

    @Test
    fun shouldInsertCorrectUserNameInSharedPref(){
        onView(withId(R.id.username_edt)).perform(typeText("Boris"))
        onView(withText("OK")).perform(click())
        val sharedPreferencesRepo = SharedPreferencesRepo.instance
        Assert.assertEquals("Boris",sharedPreferencesRepo.getUserName())
    }
}