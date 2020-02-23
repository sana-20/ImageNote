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

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.line.imagenote.db.DBHelper;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class BaseAndroidTestCase {

  protected static DBHelper dbHelper;
  protected static Context testContext;


  @BeforeClass
  public static void setUpBeforeClass () {
    testContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    dbHelper = DBHelper.getInstance(testContext);

  }

  @Test
  public void useAppContext() {
    Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    assertEquals("com.line.imagenote", appContext.getPackageName());
  }

  @AfterClass
  public static void tearDownAfterClass () {
    testContext.deleteDatabase(DBHelper.getInstance(testContext).getDatabaseName());
  }



}
