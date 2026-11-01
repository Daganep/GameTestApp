package com.neway.gametestapp.game.storage

import android.content.Context
import android.content.SharedPreferences
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoMoreInteractions
import org.mockito.Mockito.`when`

class PrefsScoreStorageTest {

    private lateinit var context: Context
    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var storage: PrefsScoreStorage

    @Before
    fun setUp() {
        context = mock(Context::class.java)
        prefs = mock(SharedPreferences::class.java)
        editor = mock(SharedPreferences.Editor::class.java)
        `when`(context.getSharedPreferences(eq("snake_prefs"), anyInt())).thenReturn(prefs)
        `when`(prefs.edit()).thenReturn(editor)
        `when`(editor.putInt(anyString(), anyInt())).thenReturn(editor)
        `when`(editor.apply()).thenAnswer { null }
        storage = PrefsScoreStorage(context)
    }

    @Test
    fun `getBest returns 0 when the key is missing`() {
        `when`(prefs.getInt(eq("best_score"), anyInt())).thenReturn(0)
        assertEquals(0, storage.getBest())
    }

    @Test
    fun `updateBest stores a higher score`() {
        `when`(prefs.getInt(eq("best_score"), anyInt())).thenReturn(10)
        storage.updateBest(25)
        verify(editor).putInt("best_score", 25)
        verify(editor).apply()
    }

    @Test
    fun `updateBest does not store a lower score`() {
        `when`(prefs.getInt(eq("best_score"), anyInt())).thenReturn(10)
        storage.updateBest(5)
        verify(prefs, never()).edit()
        verifyNoMoreInteractions(editor)
    }
}
