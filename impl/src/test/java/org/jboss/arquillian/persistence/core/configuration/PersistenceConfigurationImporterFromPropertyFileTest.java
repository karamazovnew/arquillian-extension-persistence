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
package org.jboss.arquillian.persistence.core.configuration;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Properties;

import org.jboss.arquillian.persistence.TransactionMode;
import org.jboss.arquillian.persistence.core.configuration.Configuration;
import org.jboss.arquillian.persistence.core.configuration.PersistenceConfiguration;
import org.junit.Test;

public class PersistenceConfigurationImporterFromPropertyFileTest
{

   @Test
   public void should_extract_default_data_source_from_external_configuration_file() throws Exception
   {
      // given
      String expectedDataSource = "Ike";
      Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(properties);

      // then
      assertThat(configuration.getDefaultDataSource()).isEqualTo(expectedDataSource);
   }

   @Test
   public void should_extract_scripts_to_execute_before_test_from_external_configuration_file() throws Exception
   {
      // given
      String expectedInitStatement = "SELECT * FROM ARQUILLIAN_TESTS";
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFromPropertyFile("properties/custom.arquillian.persistence.properties");

      // then
      assertThat(configuration.getScriptsToExecuteBeforeTest()).isEqualTo(expectedInitStatement);
   }

   @Test
   public void should_obtain_default_transaction_mode() throws Exception
   {
      // given
      TransactionMode expectedMode = TransactionMode.ROLLBACK;
      Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();

      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(properties);

      // then
      assertThat(configuration.getDefaultTransactionMode()).isEqualTo(expectedMode);
   }

   @Test
   public void should_be_able_to_turn_on_database_dumps() throws Exception
   {
      // given
      Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(properties);

      // then
      assertThat(configuration.isDumpData()).isTrue();
   }

   @Test
   public void should_be_able_to_define_dump_directory() throws Exception
   {
      // given
      String dumpDirectory = "/home/ike/dump";
      Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(properties);

      // then
      assertThat(configuration.getDumpDirectory()).isEqualTo(dumpDirectory);
   }

   @Test
   public void should_be_able_to_define_user_transaction_jndi() throws Exception
   {
      // given
      String expectedUserTransactionJndi = "java:jboss/UserTransaction";
      Properties properties = TestConfigurationLoader.createPropertiesFromCustomConfigurationFile();
      PersistenceConfiguration configuration = new PersistenceConfiguration();

      // when
      Configuration.importTo(configuration).loadFrom(properties);

      // then
      assertThat(configuration.getUserTransactionJndi()).isEqualTo(expectedUserTransactionJndi);
   }

}
