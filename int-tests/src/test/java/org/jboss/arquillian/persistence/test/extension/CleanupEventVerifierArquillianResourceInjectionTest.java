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
package org.jboss.arquillian.persistence.test.extension;

import static org.fest.assertions.Assertions.assertThat;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.test.usecase.UserAccount;
import org.jboss.arquillian.persistence.testextension.event.CleanupEventVerifier;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 *
 * @author <a href="mailto:bartosz.majsak@gmail.com">Bartosz Majsak</a>
 *
 */
@RunWith(Arquillian.class)
public class CleanupEventVerifierArquillianResourceInjectionTest
{

   @Deployment
   public static Archive<?> createDeploymentPackage()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                       .addPackage(UserAccount.class.getPackage())
                       // required for remote containers in order to run tests with FEST-Asserts
                       .addPackages(true, "org.fest")
                       .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
                       .addAsResource("test-persistence.xml", "META-INF/persistence.xml");
   }

   @ArquillianResource
   CleanupEventVerifier cleanupEventVerifier;

   @Test
   public void shouldInjectCleanupEventVerifier() throws Exception
   {
      assertThat(cleanupEventVerifier).isNotNull();
   }


}
