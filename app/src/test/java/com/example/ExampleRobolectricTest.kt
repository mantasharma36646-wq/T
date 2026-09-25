package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.PromptPresets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Taruni", appName)
  }

  @Test
  fun `verify preset configurations`() {
    assertTrue(PromptPresets.video2DStyles.isNotEmpty())
    assertTrue(PromptPresets.video3DStyles.isNotEmpty())
    assertTrue(PromptPresets.songGenres.isNotEmpty())
    assertTrue(PromptPresets.montageThemes.isNotEmpty())
  }
}
