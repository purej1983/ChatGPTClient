package com.thomaslam.chatgptclient

import androidx.room.Room
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

        helper.runMigrationsAndValidate(DB_NAME, 2, true).query("SELECT * FROM Chat").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getString(getColumnIndex("state"))).isEqualTo("IDLE")
        }
    }

    @Test
    fun migration2To3_containsCorrectData() {
        helper.createDatabase(DB_NAME, 2).apply {
            close()
        }
        helper.runMigrationsAndValidate(DB_NAME, 3, true).query("SELECT * FROM Config").apply {
            assertThat(moveToFirst()).isFalse()
        }
    }

    @Test
    fun migration3To4_containsCorrectData() {
        helper.createDatabase(DB_NAME, 3).apply {
            close()
        }
        helper.runMigrationsAndValidate(DB_NAME, 4, true, ChatGPTDatabase.migration3To4).query("SELECT * FROM Config limit 1").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getInt(getColumnIndex("n"))).isEqualTo(1)
            assertThat(getInt(getColumnIndex("temperature"))).isEqualTo(1)
            assertThat(getInt(getColumnIndex("stream"))).isEqualTo(0)
            assertThat(getInt(getColumnIndex("max_tokens"))).isEqualTo(150)
        }
    }

    @Test
    fun migration4To5_containsCorrectData() {
        helper.createDatabase(DB_NAME, 4).apply {
            ChatGPTDatabase.migration3To4.migrate(this)
            close()
        }
        helper.runMigrationsAndValidate(DB_NAME, 5, true).query("SELECT * FROM Config limit 1").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getInt(getColumnIndex("n"))).isEqualTo(1)
            assertThat(getFloat(getColumnIndex("temperature"))).isEqualTo(1.0f)
            assertThat(getInt(getColumnIndex("stream"))).isEqualTo(0)
            assertThat(getInt(getColumnIndex("max_tokens"))).isEqualTo(150)
        }
    }

    @Test
    fun migration5To6_containsCorrectData() {
        val assistantMessage = "1. Big Ben and the Houses of Parliament\n" +
                "\n" +
                "2. The British Museum\n" +
                "\n" +
                "3. The Tower of London\n" +
                "\n" +
                "4. The Lake District\n" +
                "\n" +
                "5. Stonehenge"
        helper.createDatabase(DB_NAME, 5).apply {
            ChatGPTDatabase.migration3To4.migrate(this)

            execSQL("insert into Conversation (role, content, chatId) values ('user', 'Top 5 attractions in UK', 1)")
            execSQL("insert into Conversation (role, content, chatId) values ('assistant', '${assistantMessage}', 1)")
            close()
        }
        helper.runMigrationsAndValidate(DB_NAME, 6, true, ChatGPTDatabase.migration5To6).query("SELECT role,content,conversationId FROM Message").apply {
            assertThat(moveToFirst()).isTrue()
            assertThat(getString(getColumnIndex("role"))).isEqualTo("user")
            assertThat(getString(getColumnIndex("content"))).isEqualTo("Top 5 attractions in UK")
            assertThat(getInt(getColumnIndex("conversationId"))).isEqualTo(1)

            assertThat(moveToNext()).isTrue()
            assertThat(getString(getColumnIndex("role"))).isEqualTo("assistant")
            assertThat(getString(getColumnIndex("content"))).isEqualTo(assistantMessage)
            assertThat(getInt(getColumnIndex("conversationId"))).isEqualTo(2)

        }
    }
    @Test
    fun testAllMigrations() {
        helper.createDatabase(DB_NAME, 1).apply { close() }

        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            ChatGPTDatabase::class.java,
            DB_NAME
        ).addMigrations(ChatGPTDatabase.migration3To4).build().apply {
            this.query(
                query = "SELECT * FROM Config limit 1",
                args = null
            ).apply {
                assertThat(moveToFirst()).isTrue()
                assertThat(getInt(getColumnIndex("n"))).isEqualTo(1)
                assertThat(getFloat(getColumnIndex("temperature"))).isEqualTo(1.0f)
                assertThat(getInt(getColumnIndex("stream"))).isEqualTo(0)
                assertThat(getInt(getColumnIndex("max_tokens"))).isEqualTo(150)
            }
            openHelper.writableDatabase.close()
        }
    }
}