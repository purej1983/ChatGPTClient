package com.thomaslam.chatgptclient

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.runner.RunWith
import androidx.room.testing.MigrationTestHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.google.common.truth.Truth.assertThat
import com.thomaslam.chatgptclient.chatecompletion.data.datasource.local.ChatGPTDatabase
import org.junit.Test


private const val DB_NAME = "testing_db"
@RunWith(AndroidJUnit4::class)
class MigrationTest {
    @get:Rule
    val helper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        ChatGPTDatabase::class.java,
        listOf(),
        FrameworkSQLiteOpenHelperFactory()
    )

    @Test
    fun migration1To2_containsCorrectData() {

        helper.createDatabase(DB_NAME, 1).apply {
            execSQL("INSERT INTO Chat VALUES('Testing Message', 1)")
            close()
        }

        var db = helper.runMigrationsAndValidate(DB_NAME, 2, true)

        db.query("SELECT * FROM Chat").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getString(getColumnIndex("state"))).isEqualTo("IDLE")
        }
    }
}