/**
 * The MIT License
 * Copyright (c) 2014-2016 Ilkka Seppälä
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.iluwatar.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.junit.jupiter.
api .AfterEach;importorg.junit.jupiter.
api .BeforeEach;importorg.junit.jupiter.
api .Test;importorg.junit.jupiter.api.
extension .ExtendWith;importorg.springframework.test.
context .ContextConfiguration;importorg.springframework.test.context.junit.
jupiter .SpringExtension;importorg.springframework.test.context.

support .AnnotationConfigContextLoader;importcom.google.common.

collect
.Lists;/**
 * Test case to test the functions of {@link PersonRepository}, beside the CRUD functions, the query
 * by {@link org.springframework.data.jpa.domain.Specification} are also test.
 * 
 */@ExtendWith(
SpringExtension.class) @ ContextConfiguration (classes= {AppConfig . class },loader=
AnnotationConfigContextLoader . class )

  publicclass
  AnnotationBasedRepositoryTest { @Resource

  private PersonRepository repository ; Personpeter=new Person( "Peter","Sagan"
  , 17 ) ; Personnasta=new Person( "Nasta","Kuzminova"
  , 25 ) ; Personjohn=new Person( "John","lawrence"
  , 35 ) ; Personterry=new Person( "Terry","Law"

  ,36); List < Person>persons=Arrays. asList( peter, nasta,john

  ,
  terry)
  ; /**
   * Prepare data for test
   */ @BeforeEachpublic void

    setup(){repository.save
  (

  persons)
  ; } @Testpublic void

    testFindAll(){ List < Person>actuals=Lists.newArrayList(repository.findAll
    ());assertTrue(actuals. containsAll (persons)&&persons.containsAll(
  actuals

  ))
  ; } @Testpublic void

    testSave ( ) {Personterry=repository.findByName
    ("Terry");terry.setSurname
    ("Lee");terry.setAge
    (47);repository.save

    ( terry );terry=repository.findByName
    ("Terry");assertEquals(terry. getSurname()
    ,"Lee"); assertEquals(47,terry.getAge
  (

  ))
  ; } @Testpublic void

    testDelete ( ) {Personterry=repository.findByName
    ("Terry");repository.delete

    (terry); assertEquals(3,repository.count
    ());assertNull(repository.findByName(
  "Terry"

  ))
  ; } @Testpublic void

    testCount(){ assertEquals(4,repository.count
  (

  ))
  ; } @Testpublic void

    testFindAllByAgeBetweenSpec(){ List < Person>persons=repository .findAll(newPersonSpecifications. AgeBetweenSpec(20,

    40)); assertEquals(3,persons.size
    ());assertTrue(persons.stream().allMatch ( (
      item )->{returnitem . getAge ( )>20&&item . getAge(
    )<40;
  }

  ))
  ; } @Testpublic void

    testFindOneByNameEqualSpec ( ) {Personactual=repository .findOne(newPersonSpecifications.NameEqualSpec(
    "Terry")); assertEquals(terry
  ,

  actual)
  ; } @AfterEachpublic void

    cleanup(){repository.
  deleteAll

(
