/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.persistence.metadata;

import static org.fest.assertions.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jboss.arquillian.persistence.CleanupUsingScript;
import org.jboss.arquillian.persistence.configuration.PersistenceConfiguration;
import org.jboss.arquillian.persistence.configuration.TestConfigurationLoader;
import org.jboss.arquillian.persistence.data.descriptor.SqlScriptResourceDescriptor;
import org.jboss.arquillian.persistence.data.naming.PrefixedScriptFileNamingStrategy;
import org.jboss.arquillian.persistence.exception.InvalidDataSetLocation;
import org.jboss.arquillian.test.spi.event.suite.TestEvent;
import org.junit.Test;

public class SqlScriptProviderForCleanupScriptsTest
{

   private static final String DEFAULT_FILENAME_FOR_TEST_METHOD = "cleanup-" + CleanupUsingScriptAnnotatedClass.class.getName() + "#shouldPassWithDataFileNotSpecified.sql";

   private static final String SQL_DATA_SET_ON_CLASS_LEVEL = "scripts/class-level.sql";

   private static final String SQL_DATA_SET_ON_METHOD_LEVEL = "scripts/method-level.sql";


   private PersistenceConfiguration defaultConfiguration = TestConfigurationLoader.createDefaultConfiguration();

   @Test
   public void shouldFetchAllScriptsDefinedForTestClass() throws Exception
   {
      // given
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      Set<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestClass());

      // then
      SqlScriptDescriptorAssert.assertThat(scriptDescriptors).containsOnlyFollowingFiles(SQL_DATA_SET_ON_CLASS_LEVEL,
            SQL_DATA_SET_ON_METHOD_LEVEL, DEFAULT_FILENAME_FOR_TEST_METHOD, "one.sql", "two.sql", "three.sql");

   }

   @Test
   public void shouldFetchDataFileNameFromTestLevelAnnotation() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_METHOD_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithDataButWithoutFormatDefinedOnMethodLevel");
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> dataFiles =  new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldFetchDataFromClassLevelAnnotationWhenNotDefinedForTestMethod() throws Exception
   {
      // given
      String expectedDataFile = SQL_DATA_SET_ON_CLASS_LEVEL;
      TestEvent testEvent = createTestEvent("shouldPassWithoutDataDefinedOnMethodLevel");
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> dataFiles =  new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(dataFiles).containsOnly(expectedDataFile);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotation() throws Exception
   {
      // given
      String expectedFileName = DEFAULT_FILENAME_FOR_TEST_METHOD;
      TestEvent testEvent = createTestEvent("shouldPassWithDataFileNotSpecified");
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files =  new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldProvideDefaultFileNameWhenNotSpecifiedInAnnotationOnClassLevel() throws Exception
   {
      // given
      String expectedFileName = "cleanup-" + CleanupUsingScriptAnnotatedOnClassLevelOnly.class.getName() + ".sql";
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedOnClassLevelOnly(), CleanupUsingScriptAnnotatedOnClassLevelOnly.class.getMethod("shouldPass"));
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<String> files = new ArrayList<String>(scriptsProvider.getResourceFileNames(testEvent.getTestMethod()));

      // then
      assertThat(files).containsOnly(expectedFileName);
   }

   @Test
   public void shouldExtractAllDataSetFiles() throws Exception
   {
      // given
      SqlScriptResourceDescriptor one = new SqlScriptResourceDescriptor("one.sql");
      SqlScriptResourceDescriptor two = new SqlScriptResourceDescriptor("two.sql");
      SqlScriptResourceDescriptor three = new SqlScriptResourceDescriptor("three.sql");
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedClass(), CleanupUsingScriptAnnotatedClass.class.getMethod("shouldPassWithMultipleFilesDefined"));
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> scriptDescriptors = new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptors(testEvent.getTestMethod()));

      // then
      assertThat(scriptDescriptors).containsExactly(one, two, three);
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileInferedFromClassLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile(),
            CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile.class.getMethod("shouldFail"));
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      Collection<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test(expected = InvalidDataSetLocation.class)
   public void shouldThrowExceptionForNonExistingFileDefinedOnMethodLevelAnnotation() throws Exception
   {
      // given
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldFailForNonExistingFile"));
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      Collection<SqlScriptResourceDescriptor> scriptDescriptors = scriptsProvider.getDescriptors(testEvent.getTestMethod());

      // then
      // exception should be thrown
   }

   @Test
   public void shouldFindFileInDefaultLocationIfNotSpecifiedExplicitly() throws Exception
   {
      // given
      SqlScriptResourceDescriptor expectedFile = new SqlScriptResourceDescriptor(defaultConfiguration.getDefaultSqlScriptLocation() + "/tables-in-scripts-folder.sql");
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation(),
            CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation.class.getMethod("shouldPassForFileStoredInDefaultLocation"));
      SqlScriptProvider<CleanupUsingScript> scriptsProvider = createSqlScriptProviderFor(testEvent);

      // when
      List<SqlScriptResourceDescriptor> dataSetDescriptors = new ArrayList<SqlScriptResourceDescriptor>(scriptsProvider.getDescriptors(testEvent.getTestMethod()));

      // then
      assertThat(dataSetDescriptors).containsOnly(expectedFile);
   }

   // ----------------------------------------------------------------------------------------

   private SqlScriptProvider<CleanupUsingScript> createSqlScriptProviderFor(TestEvent testEvent)
   {
      return SqlScriptProvider
            .forAnnotation(CleanupUsingScript.class)
            .usingConfiguration(defaultConfiguration)
            .extractingMetadataUsing(new MetadataExtractor(testEvent.getTestClass()))
            .namingFollows(new PrefixedScriptFileNamingStrategy("cleanup-", "sql"))
            .build(new ValueExtractor<CleanupUsingScript>()
            {
               @Override
               public String[] extract(CleanupUsingScript a)
               {
                  return a.value();
               }
            });
   }

   private static TestEvent createTestEvent(String testMethod) throws NoSuchMethodException
   {
      TestEvent testEvent = new TestEvent(new CleanupUsingScriptAnnotatedClass(), CleanupUsingScriptAnnotatedClass.class.getMethod(testMethod));
      return testEvent;
   }

   @CleanupUsingScript(SQL_DATA_SET_ON_CLASS_LEVEL)
   private static class CleanupUsingScriptAnnotatedClass
   {
      public void shouldPassWithoutDataDefinedOnMethodLevel() {}

      @CleanupUsingScript(SQL_DATA_SET_ON_METHOD_LEVEL)
      public void shouldPassWithDataButWithoutFormatDefinedOnMethodLevel () {}

      @CleanupUsingScript
      public void shouldPassWithDataFileNotSpecified() {}

      @CleanupUsingScript({"one.sql", "two.sql", "three.sql"})
      public void shouldPassWithMultipleFilesDefined() {}

   }

   private static class CleanupUsingScriptAnnotationWithUnsupportedFormat
   {
      @CleanupUsingScript("arquillian.ike")
      public void shouldFailWithNonSupportedFileExtension() {}
   }

   @CleanupUsingScript
   private static class CleanupUsingScriptAnnotatedOnClassLevelOnly
   {
      public void shouldPass() {}
   }

   @CleanupUsingScript
   private static class CleanupUsingScriptAnnotatedOnClassLevelOnlyNonExistingFile
   {
      public void shouldFail() {}
   }

   private static class CleanupUsingScriptOnTestMethodLevelWithNonExistingFileAndDefaultLocation
   {
      @CleanupUsingScript("non-existing.sql")
      public void shouldFailForNonExistingFile() {}

      @CleanupUsingScript("tables-in-scripts-folder.sql")
      public void shouldPassForFileStoredInDefaultLocation() {}

   }

}